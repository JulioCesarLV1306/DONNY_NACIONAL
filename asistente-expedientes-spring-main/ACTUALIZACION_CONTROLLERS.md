# Actualización de Controladores - Nuevo Esquema BD

## Fecha: 12-03-2026
## Versión: 2.0

---

## RESUMEN DE CAMBIOS

Se han refactorizado los controladores `BitacoraController` y `EncuestaController` para utilizar el nuevo esquema de base de datos con la tabla `seg_usuario` y nomenclatura actualizada.

---

## CAMBIOS IMPLEMENTADOS

### 1. Nuevo Servicio: UsuarioService

**Archivos creados:**
- `UsuarioService.java` - Interfaz del servicio
- `UsuarioServiceImpl.java` - Implementación

**Métodos principales:**
```java
Usuario findByDni(String dni)
Usuario createIfNotExists(String dni, String nombreCompleto)
Usuario create(Usuario usuario)
```

**Funcionalidad:**
- Busca usuarios existentes por DNI en `seg_usuario`
- Crea automáticamente usuarios nuevos si no existen
- Parsea nombres completos en formato "APELLIDO_PAT APELLIDO_MAT NOMBRES"
- Asigna tipo de usuario 9 (Invitado) por defecto

---

### 2. BitacoraController - Refactorización Completa

**Cambios principales:**

#### Imports agregados:
```java
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
```

#### Nueva instancia de servicio:
```java
UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
```

#### Lógica actualizada en métodos `/error` y `/create`:

**ANTES:**
```java
bitacora.setCodigoAccion(codigo);         // Nombre incorrecto
bitacora.setDescripcionAccion(desc);      // Nombre incorrecto
bitacora.setDniSece(dni);                 // Campo deprecated
bitacora.setNombreSece(nombre);           // Campo deprecated
bitacora.setIpModulo(ip);                 // Nombre incorrecto
bitacora.setUsuarioModulo(usuario);       // Campo eliminado
```

**AHORA:**
```java
// 1. Obtener/crear usuario primero
Usuario usuario = usuarioService.createIfNotExists(dni, nombre);
bitacora.setNIdUsuario(usuario.getNIdUsuario());  // ✅ Campo obligatorio FK

// 2. Usar nombres correctos de campos
bitacora.setCCodigoAccion(codigo);                // ✅ c_ = código
bitacora.setTDescripcionAccion(desc);             // ✅ t_ = texto
bitacora.setCIpModulo(ip);                        // ✅ c_ = código

// 3. Mantener campos deprecated para compatibilidad temporal
bitacora.setDniSece(dni);
bitacora.setNombreSece(nombre);
```

---

### 3. EncuestaController - Refactorización Completa

**Cambios principales:**

#### Imports agregados:
```java
import com.ncpp.asistenteexpedientes.asistente.entity.Usuario;
import com.ncpp.asistenteexpedientes.asistente.service.impl.UsuarioServiceImpl;
```

#### Nueva instancia de servicio:
```java
UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
```

#### Lógica actualizada en método `/crear`:

**ANTES:**
```java
encuestaService.create(encuesta);    // Sin validar n_id_usuario

Bitacora bitacora = new Bitacora();
bitacora.setCodigoAccion("ENCUESTA");
bitacora.setDescripcionAccion(mensaje);
bitacora.setDniSece(dni);
bitacora.setNombreSece(nombre);
bitacora.setIpModulo(ipModulo);
bitacora.setUsuarioModulo(usuarioModulo);
```

**AHORA:**
```java
// 1. Verificar/crear usuario si es necesario
if (encuesta.getNIdUsuario() == null && encuesta.getDniSece() != null) {
    Usuario usuario = usuarioService.createIfNotExists(
        encuesta.getDniSece(), 
        encuesta.getNombreSece()
    );
    encuesta.setNIdUsuario(usuario.getNIdUsuario());
}

encuestaService.create(encuesta);    // ✅ Ahora con n_id_usuario válido

// 2. Bitácora con nuevo esquema
Bitacora bitacora = new Bitacora();
bitacora.setCCodigoAccion("ENCUESTA");              // ✅ Nombre correcto
bitacora.setTDescripcionAccion(mensaje);            // ✅ Nombre correcto
bitacora.setNIdUsuario(encuesta.getNIdUsuario());   // ✅ FK obligatoria
bitacora.setCIpModulo(ipModulo);                    // ✅ Nombre correcto

// Mantener campos deprecated para compatibilidad
bitacora.setDniSece(encuesta.getDniSece());
bitacora.setNombreSece(encuesta.getNombreSece());
```

---

### 4. BitacoraServiceImpl - Validación Mejorada

**Cambios:**
- ✅ Eliminado `TODO` de mapeo dni_sece → n_id_usuario
- ✅ Agregada validación obligatoria de `n_id_usuario`:
  ```java
  if (bitacora.getNIdUsuario() == null) {
      throw new IllegalArgumentException(
          "n_id_usuario es obligatorio. Use UsuarioService.createIfNotExists()"
      );
  }
  ```
- ✅ Mejora en manejo de `c_aud_uid` con valor por defecto "SYSTEM"

---

### 5. EncuestaServiceImpl - Validación Mejorada

**Cambios:**
- ✅ Eliminado `TODO` de mapeo dni_sece → n_id_usuario
- ✅ Agregadas validaciones obligatorias:
  ```java
  if (encuesta.getNIdUsuario() == null) {
      throw new IllegalArgumentException(...);
  }
  if (encuesta.getNIdModulo() == null) {
      throw new IllegalArgumentException(...);
  }
  ```
- ✅ Mensajes de log mejorados con IDs y valores de calificación

---

## COMPATIBILIDAD CON VERSIÓN ANTERIOR

### Campos Deprecated Mantenidos:
Los siguientes campos se mantienen temporalmente en las entidades para compatibilidad:

```java
@Deprecated
private String dniSece;      // Mapea a seg_usuario.c_dni

@Deprecated
private String nombreSece;   // Mapea a getNombreCompleto() de Usuario
```

**IMPORTANTE:** 
- Los controladores **DEBEN** establecer `nIdUsuario` usando `UsuarioService`
- Los campos `dniSece/nombreSece` son opcionales y solo para compatibilidad
- En futuras versiones estos campos serán eliminados

---

## FLUJO DE TRABAJO ACTUALIZADO

### Crear Bitácora:
```java
// 1. Instanciar servicios
BitacoraServiceImpl bitacoraService = new BitacoraServiceImpl();
UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();

// 2. Obtener/crear usuario
Usuario usuario = usuarioService.createIfNotExists(dni, nombreCompleto);

// 3. Crear bitácora con ID de usuario
Bitacora bitacora = new Bitacora();
bitacora.setNIdUsuario(usuario.getNIdUsuario());  // ✅ OBLIGATORIO
bitacora.setCCodigoAccion("ACCION_001");
bitacora.setTDescripcionAccion("Descripción de la acción");
bitacora.setCIpModulo("192.168.1.10");

// 4. Guardar
bitacoraService.create(bitacora);
```

### Crear Encuesta:
```java
// 1. Instanciar servicios
EncuestaServiceImpl encuestaService = new EncuestaServiceImpl();
UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();

// 2. Obtener/crear usuario
Usuario usuario = usuarioService.createIfNotExists(dni, nombreCompleto);

// 3. Crear encuesta con IDs
Encuesta encuesta = new Encuesta();
encuesta.setNIdUsuario(usuario.getNIdUsuario());   // ✅ OBLIGATORIO
encuesta.setNIdModulo(idModulo);                   // ✅ OBLIGATORIO
encuesta.setNCalificacion(5);

// 4. Guardar
encuestaService.create(encuesta);
```

---

## TESTING Y VALIDACIÓN

### Verificación de compilación:
```bash
mvn clean compile
```

### Resultado:
✅ **0 errores de compilación**

### Verificación de sintaxis:
✅ Todos los métodos usan nomenclatura correcta:
- `c_*` para códigos (setCCodigoAccion, setCIpModulo)
- `t_*` para texto no estructurado (setTDescripcionAccion)
- `n_*` para números/IDs (setNIdUsuario, setNCalificacion)
- `x_*` para descripciones (xApePaterno, xNombres)

---

## IMPACTO EN OTROS COMPONENTES

### ✅ Sin cambios necesarios:
- Entidades (Usuario, Bitacora, Encuesta) - Ya refactorizadas
- Base de datos - Schema ya actualizado
- Triggers de auditoría - Funcionan correctamente

### ⚠️ Posibles ajustes futuros:
- Frontend Angular: Puede necesitar enviar `nIdModulo` en requests de encuesta
- Otros controllers no revisados: Pueden necesitar refactorización similar

---

## PRÓXIMOS PASOS RECOMENDADOS

1. **Testing exhaustivo:**
   - Probar endpoint `/bitacora/create` con datos válidos
   - Probar endpoint `/bitacora/error` con errores simulados
   - Probar endpoint `/encuesta/crear` con diferentes usuarios
   - Probar endpoint `/encuesta/buscar` por DNI

2. **Monitoreo de logs:**
   - Verificar creación automática de usuarios en `seg_usuario`
   - Confirmar inserción correcta en `met_bitacora` y `met_encuesta`
   - Validar que c_aud_uid se establece correctamente

3. **Migración de datos:**
   - Ejecutar `SCRIPT_MIGRACION_DATOS.sql` si no se ha hecho
   - Verificar que todos los DNI existentes en bitacora/encuesta tengan usuarios creados

4. **Documentación API:**
   - Actualizar Swagger/OpenAPI si existe
   - Documentar que `nIdUsuario` es manejado automáticamente

---

## CONTACTO Y SOPORTE

Para dudas sobre la refactorización:
- Revisar `DOCUMENTACION_REFACTORIZACION.md` (documento completo)
- Revisar `README_QUICK_START.md` (guía de deployment)
- Consultar logs en consola para mensajes de validación

---

**Última actualización:** 12-03-2026 - v2.0  
**Autor:** JC (Desarrollador)  
**Estado:** ✅ COMPLETADO - 0 errores de compilación
