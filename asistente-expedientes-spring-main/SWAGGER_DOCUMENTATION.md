# 📘 Documentación API REST con Swagger - Sistema Asistente de Expedientes

## 🎯 Introducción

Este documento explica cómo acceder y usar la documentación automática de la API REST generada con **Swagger/OpenAPI 3.0** usando la biblioteca **Springdoc OpenAPI**.

La documentación Swagger permite:
- ✅ **Visualizar todos los endpoints** disponibles de forma interactiva
- ✅ **Probar los servicios REST** directamente desde el navegador
- ✅ **Generar código de clientes** en cualquier lenguaje (Java, JavaScript, Python, C#, etc.)
- ✅ **Mantener contratos de API actualizados** automáticamente
- ✅ **Independizar el backend** para múltiples frontends (Angular, Mobile, externos)

---

## 🌐 Acceso a Swagger UI

### 1. Iniciar el Backend
```bash
cd asistente-expedientes-spring-main
mvn spring-boot:run
```

### 2. Abrir Swagger UI en el Navegador
```
http://localhost:8080/swagger-ui.html
```

### 3. Obtener Especificación OpenAPI (JSON)
```
http://localhost:8080/v3/api-docs
```

---

## 📋 Estructura de la API

### Grupos de Endpoints (Tags)

| Tag | Descripción | Endpoints |
|-----|-------------|-----------|
| **Bitácora** | API para registro de auditoría y seguimiento | `/apiv1/bitacora/*` |
| **Encuestas** | API para gestión de encuestas de satisfacción | `/apiv1/encuesta/*` |
| **Expedientes** | API para búsqueda y consulta de expedientes judiciales | `/apiv1/expediente/*` |
| **Personas** | API para validación y autenticación de usuarios ciudadanos | `/apiv1/persona/*` |

### Nomenclatura de Campos

El sistema usa prefijos estándar para identificar tipos de campo:

| Prefijo | Tipo | Descripción | Ejemplo |
|---------|------|-------------|---------|
| `n_` | Numérico | IDs, contadores, códigos numéricos | `n_id_usuario`, `n_calificacion` |
| `c_` | Código | Códigos alfanuméricos cortos | `c_dni`, `c_pc_ip` |
| `x_` | Descripción | Texto descriptivo | `x_nombres`, `x_descripcion` |
| `t_` | Texto largo | Texto extenso (memo) | `t_observaciones` |
| `f_` | Fecha/Timestamp | Campos de fecha y hora | `f_fecha_hora`, `f_aud` |
| `l_` | Lógico | Indicadores binarios | `l_activo` (S/N) |
| `b_` | Bandera | Flags de control | `b_aud` (I/U/D) |

### Esquemas de Base de Datos

| Prefijo | Esquema | Descripción |
|---------|---------|-------------|
| `seg_*` | Seguridad | `seg_usuario`, `seg_modulo` |
| `met_*` | Métricas | `met_encuesta`, `met_bitacora` |
| `ope_*` | Operaciones | `ope_expediente`, `ope_descarga` |

---

## 🧪 Cómo Probar Endpoints en Swagger UI

### Ejemplo: Buscar Expedientes

1. **Expandir el grupo "Expedientes"**
2. **Hacer clic en `GET /apiv1/expediente/buscar`**
3. **Hacer clic en "Try it out"**
4. **Llenar parámetros:**
   - `numero`: `12345`
   - `anio`: `2024`
   - `especialidad`: `CI`
5. **Hacer clic en "Execute"**
6. **Ver respuesta en tiempo real**

---

## 👨‍💻 Guía para Desarrolladores

### 1. Documentar un Nuevo Controller

```java
package com.ncpp.asistenteexpedientes.asistente.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Documentos", description = "API para gestión de documentos digitalizados")
@RestController
@RequestMapping("/apiv1/documento")
public class DocumentoController {

    @Operation(
        summary = "Buscar documento por ID", 
        description = "Obtiene el documento PDF digitalizado asociado al ID proporcionado. " +
                      "Registra automáticamente la descarga en la bitácora."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documento encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Documento no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<byte[]> buscarPorId(
        @Parameter(description = "ID único del documento", example = "12345", required = true)
        @PathVariable Long id
    ) {
        // Implementación...
    }
}
```

### 2. Documentar una Entidad (Entity/DTO)

```java
package com.ncpp.asistenteexpedientes.asistente.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.*;
import lombok.Data;

@Schema(description = "Documento digitalizado registrado en ope_documento")
@Entity
@Table(name = "ope_documento")
@Data
public class Documento {

    @Schema(
        description = "ID único del documento",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "n_id_documento")
    private Long nIdDocumento;

    @Schema(
        description = "Número de expediente asociado",
        example = "12345-2024-0-1801-JR-CI-01",
        required = true,
        maxLength = 50
    )
    @Column(name = "c_numero_expediente", nullable = false, length = 50)
    private String cNumeroExpediente;

    @Schema(
        description = "Tipo de documento (1=Resolución, 2=Acta, 3=Escrito)",
        example = "1",
        required = true,
        allowableValues = {"1", "2", "3"}
    )
    @Column(name = "n_tipo_documento", nullable = false)
    private Integer nTipoDocumento;

    @Schema(
        description = "Archivo PDF en formato Base64",
        required = true
    )
    @Lob
    @Column(name = "b_archivo")
    private byte[] bArchivo;

    // Campos de auditoría
    @Schema(description = "Fecha de auditoría", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(name = "f_aud")
    private Date fAud;

    @Schema(
        description = "Bandera de auditoría (I=Insert, U=Update, D=Delete)",
        accessMode = Schema.AccessMode.READ_ONLY,
        allowableValues = {"I", "U", "D"}
    )
    @Column(name = "b_aud", length = 1)
    private String bAud;
}
```

### 3. Documentar Parámetros de Request

```java
@Operation(summary = "Buscar con múltiples filtros")
@GetMapping("/filtrar")
public ResponseEntity<List<Expediente>> filtrar(
    @Parameter(
        description = "Número de expediente (formato: 12345-2024-0-1801-JR-CI-01)",
        example = "12345",
        required = false
    )
    @RequestParam(required = false) String numero,
    
    @Parameter(
        description = "Año del expediente (mínimo: 2014)",
        example = "2024",
        required = true
    )
    @RequestParam Integer anio,
    
    @Parameter(
        description = "DNI del usuario que realiza la consulta",
        example = "12345678",
        required = true
    )
    @RequestParam String dni
) {
    // Implementación...
}
```

### 4. Documentar Request Body

```java
@Operation(summary = "Crear nuevo usuario")
@PostMapping("/crear")
public ResponseEntity<Usuario> crear(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Datos del nuevo usuario",
        required = true
    )
    @RequestBody Usuario usuario
) {
    // Implementación...
}
```

---

## 🔧 Configuración Avanzada

### Editar Información General de la API

Modificar archivo: `src/main/java/com/ncpp/asistenteexpedientes/asistente/config/OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI asistenteExpedientesOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API REST - Sistema Asistente de Expedientes")
                .version("1.0")
                .description("Descripción completa de la API...")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("soporte@pj.gob.pe")
                    .url("https://www.pj.gob.pe")
                )
                .license(new License()
                    .name("Uso Interno - Poder Judicial del Perú")
                )
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Servidor de Desarrollo"),
                new Server().url("https://api.pj.gob.pe").description("Servidor de Producción")
            ));
    }
}
```

---

## 📦 Generar Clientes de API

### 1. Descargar especificación OpenAPI

```bash
curl http://localhost:8080/v3/api-docs > openapi.json
```

### 2. Usar OpenAPI Generator

#### Cliente JavaScript/TypeScript
```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.json \
  -g typescript-axios \
  -o ./generated-client
```

#### Cliente Java
```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.json \
  -g java \
  -o ./java-client
```

#### Cliente Python
```bash
npx @openapitools/openapi-generator-cli generate \
  -i openapi.json \
  -g python \
  -o ./python-client
```

---

## ⚙️ Configuración en `application.properties`

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.show-actuator=false

# Para personalizar la URL base de la API
server.servlet.context-path=/
```

---

## 🚀 Mejores Prácticas

### ✅ DO (Hacer)

1. **Documentar TODOS los endpoints nuevos** con `@Operation`
2. **Incluir ejemplos realistas** en `@Parameter` y `@Schema`
3. **Especificar códigos de respuesta** con `@ApiResponses`
4. **Usar descripciones en español** para facilitar comprensión
5. **Marcar campos opcionales/requeridos** correctamente
6. **Documentar validaciones** (maxLength, allowableValues, etc.)
7. **Mantener actualizada la versión** en `OpenApiConfig.java`

### ❌ DON'T (Evitar)

1. ❌ No dejar endpoints sin documentar
2. ❌ No usar descripciones vagas ("Busca data", "Obtiene info")
3. ❌ No omitir ejemplos de valores esperados
4. ❌ No olvidar marcar campos deprecados con `@Deprecated`
5. ❌ No documentar campos internos sensibles (passwords, tokens)

---

## 📊 Anotaciones Swagger Disponibles

| Anotación | Uso | Nivel |
|-----------|-----|-------|
| `@Tag` | Agrupar endpoints relacionados | Controller |
| `@Operation` | Documentar método/endpoint | Method |
| `@ApiResponses` | Especificar respuestas HTTP | Method |
| `@Parameter` | Documentar parámetros de request | Parameter |
| `@RequestBody` | Documentar cuerpo de petición | Parameter |
| `@Schema` | Documentar entidades/DTOs | Class/Field |
| `@Hidden` | Ocultar endpoint de la documentación | Method/Controller |

---

## 🐛 Troubleshooting

### Problema: Swagger UI no carga

**Solución:**
```bash
# Verificar que el backend está corriendo
curl http://localhost:8080/actuator/health

# Verificar URL correcta
http://localhost:8080/swagger-ui.html
# O también:
http://localhost:8080/swagger-ui/index.html
```

### Problema: Los cambios no se reflejan

**Solución:**
```bash
# Limpiar y recompilar
mvn clean install

# Reiniciar el servidor
mvn spring-boot:run
```

### Problema: Errores de importación de anotaciones Swagger

**Solución:**
```xml
<!-- Verificar en pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>
```

---

## 📞 Soporte

Para consultas sobre la documentación de la API:
- **Email:** soporte@pj.gob.pe
- **Documentación Oficial Springdoc:** https://springdoc.org/
- **OpenAPI Specification:** https://swagger.io/specification/

---

## 📝 Historial de Versiones

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 0.11 | 2025-01-XX | Implementación inicial de Swagger/OpenAPI |
| 1.0 | TBD | Release con todos los endpoints documentados |

---

**¡La API está lista para ser consumida por cualquier cliente! 🎉**
