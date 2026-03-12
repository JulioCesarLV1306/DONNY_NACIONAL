package com.ncpp.asistenteexpedientes.asistente.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación automática del API REST
 * 
 * Acceso a la documentación interactiva:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * 
 * @author Sistema Asistente de Expedientes
 * @version 1.0.0
 * @since 2024-12
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI asistenteExpedientesOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers());
    }

    /**
     * Información general del API
     */
    private Info apiInfo() {
        return new Info()
                .title("API REST - Sistema Asistente de Expedientes")
                .version("0.11")
                .description(
                        "API REST para el Sistema Asistente de Expedientes del Poder Judicial.\n\n" +
                        "**Funcionalidades principales:**\n" +
                        "- Gestión de usuarios y autenticación\n" +
                        "- Búsqueda de expedientes judiciales\n" +
                        "- Consulta de documentos digitalizados\n" +
                        "- Descarga de archivos y resoluciones\n" +
                        "- Registro de bitácoras y auditoría\n" +
                        "- Encuestas de satisfacción\n" +
                        "- Estadísticas de uso\n\n" +
                        "**Nomenclatura de campos:**\n" +
                        "- `n_` = Campos numéricos (IDs, contadores)\n" +
                        "- `c_` = Códigos e identificadores\n" +
                        "- `x_` = Descripciones y textos cortos\n" +
                        "- `t_` = Textos largos\n" +
                        "- `f_` = Fechas y timestamps\n" +
                        "- `l_` = Lógicos (S/N)\n" +
                        "- `b_` = Banderas de auditoría (I/U/D)\n\n" +
                        "**Esquemas de base de datos:**\n" +
                        "- `seg_*` = Seguridad (usuarios, módulos, accesos)\n" +
                        "- `met_*` = Métricas (bitácoras, encuestas, estadísticas)\n" +
                        "- `ope_*` = Operaciones (expedientes, documentos, descargas)"
                )
                .contact(apiContact())
                .license(apiLicense());
    }

    /**
     * Información de contacto
     */
    private Contact apiContact() {
        return new Contact()
                .name("Equipo de Desarrollo - Poder Judicial")
                .email("soporte@pj.gob.pe")
                .url("https://www.pj.gob.pe");
    }

    /**
     * Información de licencia
     */
    private License apiLicense() {
        return new License()
                .name("Uso Interno - Poder Judicial del Perú")
                .url("https://www.pj.gob.pe");
    }

    /**
     * Servidores disponibles
     */
    private List<Server> apiServers() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Servidor Local de Desarrollo");

        Server productionServer = new Server()
                .url("http://servidor-produccion:8080")
                .description("Servidor de Producción (configurar según ambiente)");

        return Arrays.asList(localServer, productionServer);
    }
}
