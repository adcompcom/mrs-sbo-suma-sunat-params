package pe.adcomp.suma.sunat.params.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Configuración del DataSource para agregar el nombre de la base de datos por código
 */
@Configuration
public class DataSourceConfig {

     private static final Logger logger = Logger.getLogger(DataSourceConfig.class.getName());
 
    @Bean
    @Primary
    public DataSource jpaDataSource(DataSourceProperties dataSourceProperties) {
         logger.info("[CONFIG] Creando MySQL DataSource para JPA");
        dataSourceProperties.setUrl(dataSourceProperties.getUrl().concat("bdsupra?useSSL=false&serverTimezone=UTC"));
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}
