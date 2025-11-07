package pe.adcomp.suma.sunat.params;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pe.adcomp.suma.sunat.params.dto.CambioSunatDTO;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;

import java.util.HashMap;

@SpringBootApplication
@RegisterReflectionForBinding({
	PadronSunatDTO.class,
	CambioSunatDTO.class,
	HashMap.class
})
public class MrsSboSumaSunatParamsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MrsSboSumaSunatParamsApplication.class, args);
	}

}
