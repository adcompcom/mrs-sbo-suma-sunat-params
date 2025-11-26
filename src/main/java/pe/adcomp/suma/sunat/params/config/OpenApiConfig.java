package pe.adcomp.suma.sunat.params.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SUNAT Params API")
                        .version("1.0.0")
                        .description("API REST para consultar información del Padrón SUNAT (RUC/DNI) y Tipo de Cambio SUNAT. **Requiere autenticación mediante Bearer Token (JWT).**")
                        .contact(new Contact()
                                .name("ADCOMP")
                                .email("soporte@ad-comp.net")
                                .url("https://ad-comp.net"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local"),
                        new Server()
                                .url("https://dev.adcomp.work")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://int.adcomp.work")
                                .description("Servidor de Integración"),
                        new Server()
                                .url("https://cal.adcomp.work")
                                .description("Servidor de Calidad")
                ))
                // Configuración de seguridad Bearer Token
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT obtenido del servicio de autenticación AWS Cognito")))
                // Aplicar seguridad globalmente a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
