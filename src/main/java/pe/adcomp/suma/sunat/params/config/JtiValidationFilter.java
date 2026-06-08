package pe.adcomp.suma.sunat.params.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verifica con el Auth Server que el JTI del JWT no fue revocado
 * (sesión cerrada por el usuario o por detección de takeover).
 *
 * - Fail-open si el Auth Server no responde (disponibilidad > seguridad estricta)
 * - Cooldown de 60 s por JTI para no saturar el Auth Server en cada request
 * - 404 → JTI revocado → 401 al cliente
 * - 403 → fuera de ventana de acceso → fail-open
 */
@Component
public class JtiValidationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JtiValidationFilter.class);

    private static final String HEADER_JTI    = "X-Token-JTI";
    private static final String HEADER_SECRET = "X-Internal-Secret";

    private final RestTemplate restTemplate;
    private final String authServerUrl;
    private final String internalSecret;
    private final boolean enabled;
    private final long cooldownMs;

    // jti → último timestamp de validación exitosa (ms)
    private final ConcurrentHashMap<String, Long> cache = new ConcurrentHashMap<>();

    public JtiValidationFilter(
            RestTemplateBuilder builder,
            @Value("${app.auth-server.url}") String authServerUrl,
            @Value("${app.auth-server.internal-secret}") String internalSecret,
            @Value("${app.auth-server.jti-validation.enabled:false}") boolean enabled,
            @Value("${app.auth-server.jti-validation.cooldown-seconds:60}") int cooldownSeconds) {

        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        this.authServerUrl  = authServerUrl;
        this.internalSecret = internalSecret;
        this.enabled        = enabled;
        this.cooldownMs     = cooldownSeconds * 1000L;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) return true;
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getServletPath();
        return path.startsWith("/swagger-ui")
            || path.startsWith("/api-docs")
            || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String jti = request.getHeader(HEADER_JTI);
        if (jti == null || jti.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        Long ultimaValidacion = cache.get(jti);
        if (ultimaValidacion != null && (System.currentTimeMillis() - ultimaValidacion) < cooldownMs) {
            chain.doFilter(request, response);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HEADER_JTI, jti);
            headers.set(HEADER_SECRET, internalSecret);

            restTemplate.exchange(
                    authServerUrl + "/api/session/active",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Void.class);

            cache.put(jti, System.currentTimeMillis());
            purgarCacheExpirada();
            chain.doFilter(request, response);

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("JTI revocado [{}] — rechazando request a {}", jti, request.getServletPath());
                cache.remove(jti);
                responder401(response);
                return;
            }

            log.warn("Auth Server respondió {} al validar JTI — fail-open", e.getStatusCode().value());
            chain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("Auth Server no disponible para validar JTI — fail-open: {}", e.getMessage());
            chain.doFilter(request, response);
        }
    }

    private void responder401(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\":\"session_takeover\"}");
    }

    private void purgarCacheExpirada() {
        long umbral = System.currentTimeMillis() - (cooldownMs * 2);
        cache.entrySet().removeIf(e -> e.getValue() < umbral);
    }
}
