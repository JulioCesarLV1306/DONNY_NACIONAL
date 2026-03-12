# GUÍA RÁPIDA - DONYDRIVECLIENT REFACTORIZADO

## ⚡ CAMBIOS CLAVE - RESUMEN EJECUTIVO

### 🎯 Objetivo
Adaptar DonyDriveClient al nuevo esquema PostgreSQL refactorizado con nomenclatura técnica y auditoría centralizada.

---

## 📦 RESUMEN DE CAMBIOS

### Entidades Refactorizadas

| Entidad | Tabla Antes | Tabla Después | Cambios Principales |
|---------|-------------|---------------|---------------------|
| **Usuario** | N/A | `seg_usuario` | ✅ **NUEVA** - gestión centralizada de usuarios |
| **Bitacora** | `bitacora` | `met_bitacora` | FK `n_id_usuario`, campos prefijados, auditoría |
| **Modulo** | `modulo` | `seg_modulo` | Campos prefijados, auditoría |
| **Descarga** | N/A (POJO) | N/A (POJO) | Campos prefijados (nIdDescarga, cKeyDescarga, etc.) |

### Servicios Actualizados

| Servicio | Estado | Cambios |
|----------|--------|---------|
| **UsuarioService** | ✅ NUEVO | `findByDni()`, `createIfNotExists()` |
| **BitacoraService** | ✅ Sin cambios | Compatible con nueva entidad |
| **ModuloService** | ✅ Actualizado | JPQL con campos prefijados |
| **DescargaService** | ✅ Actualizado | SQL con `met_descarga` y campos prefijados |
| **DownloaderService** | ✅ Actualizado | Integración con UsuarioService |

### Clases Principales

| Clase | Cambios |
|-------|---------|
| **Main.java** | Login/Logout con `createIfNotExists()`, setters refactorizados |
| **MasterUI.java** | Getters refactorizados, logout con nIdUsuario |

---

## 🔄 MAPEO DE CAMBIOS CRÍTICOS

### Bitacora - Antes y Después

```java
// ❌ ANTES
bitacora.setIpModulo(ip);
bitacora.setUsuarioModulo(usuario);
bitacora.setCodigoAccion("LOGIN_MODULO");
bitacora.setDescripcionAccion("...");
bitacora.setDniSece(dni);
bitacora.setNombreSece(nombre);

// ✅ DESPUÉS
UsuarioService usuarioService = new UsuarioService();
Usuario usuario = usuarioService.createIfNotExists(dni, nombre);

bitacora.setCIpModulo(ip);
bitacora.setNIdUsuario(usuario.getNIdUsuario());  // FK obligatoria
bitacora.setCCodigoAccion("LOGIN_MODULO");
bitacora.setTDescripcionAccion("...");
bitacora.setCAudUid(dni);  // Auditoría manual
```

### Modulo - Antes y Después

```java
// ❌ ANTES
modulo.getPcIp()
modulo.getPcUsuario()
modulo.getDescripcion()
modulo.getEstado()

// ✅ DESPUÉS
modulo.getCPcIp()
modulo.getCPcUsuario()
modulo.getXDescripcion()
modulo.getNEstado()
```

### Descarga - Antes y Después

```java
// ❌ ANTES
descarga.getIdDescarga()
descarga.setEstado("completo")

// ✅ DESPUÉS
descarga.getNIdDescarga()
descarga.setXEstado("completo")
```

---

## 🚀 FLUJO DE USUARIO

### 1. Crear/Obtener Usuario

```java
UsuarioService usuarioService = new UsuarioService();
Usuario usuario = usuarioService.createIfNotExists("12345678", "PEREZ GOMEZ JUAN");
```

**Nota**: `createIfNotExists()` hace:
- ✅ Busca usuario por DNI
- ✅ Si existe, lo devuelve
- ✅ Si no existe, lo crea con tipo 9 (Invitado)
- ✅ Parsea el nombre automáticamente (apellidos + nombres)

### 2. Registrar Acción en Bitácora

```java
Bitacora bitacora = new Bitacora();
bitacora.setNIdUsuario(usuario.getNIdUsuario());  // FK obligatoria
bitacora.setCCodigoAccion("COPIA_ARCHIVOS");
bitacora.setCIpModulo(ipModulo);
bitacora.setTDescripcionAccion("Usuario copió archivos a USB...");
bitacora.setCAudUid(usuario.getCDni());  // ⚠️ Obligatorio para auditoría

BitacoraService bitacoraService = new BitacoraService();
bitacoraService.create(bitacora);
```

---

## 📋 NOMENCLATURA RÁPIDA

### Prefijos de Campos

| Prefijo | Tipo | Ejemplo |
|---------|------|---------|
| `n_` | Numérico/ID | `n_id_usuario`, `n_estado` |
| `c_` | Código | `c_dni`, `c_ip_modulo`, `c_key_descarga` |
| `x_` | Descripción | `x_nombres`, `x_descripcion` |
| `t_` | Texto largo | `t_descripcion_acc` |
| `f_` | Fecha/Timestamp | `f_fecha_hora`, `f_aud` |
| `l_` | Lógico S/N | `l_activo` |
| `b_` | Bandera I/U/D | `b_aud` |

### Ejemplo Completo

```java
@Column(name = "n_id_usuario")  // Numérico
private Long nIdUsuario;

@Column(name = "c_dni")  // Código
private String cDni;

@Column(name = "x_nombres")  // Descripción
private String xNombres;

@Column(name = "l_activo")  // Lógico
private String lActivo;  // "S" o "N"
```

---

## ⚠️ PUNTOS CRÍTICOS

### 1. FK n_id_usuario es OBLIGATORIA

```java
// ❌ ESTO FALLARÁ
bitacora.setCCodigoAccion("LOGIN_MODULO");
bitacoraService.create(bitacora);  // ERROR: violación FK n_id_usuario NOT NULL

// ✅ CORRECTO
Usuario usuario = usuarioService.createIfNotExists(dni, nombre);
bitacora.setNIdUsuario(usuario.getNIdUsuario());
bitacoraService.create(bitacora);
```

### 2. c_aud_uid debe asignarse manualmente

```java
// ⚠️ NO hay inyección automática en DonyDriveClient
bitacora.setCAudUid(usuario.getCDni());  // Obligatorio
```

### 3. Triggers automáticos gestionan f_aud y b_aud

```java
// ✅ NO asignes f_aud ni b_aud manualmente
// El trigger fn_auditar_cambios() lo hace automáticamente
bitacora.setFFechaHora(new Date());  // ❌ No necesario, el trigger lo hace
```

---

## 🧪 VALIDACIÓN RÁPIDA

### 1. Verificar Login de Módulo

```sql
SELECT 
    b.c_codigo_accion,
    u.c_dni AS modulo,
    b.c_ip_modulo,
    b.f_fecha_hora
FROM met_bitacora b
JOIN seg_usuario u ON u.n_id_usuario = b.n_id_usuario
WHERE b.c_codigo_accion = 'LOGIN_MODULO'
ORDER BY b.f_fecha_hora DESC
LIMIT 1;
```

### 2. Verificar Copia de Archivos

```sql
SELECT 
    u.c_dni,
    u.x_nombres,
    b.t_descripcion_acc,
    b.f_fecha_hora
FROM met_bitacora b
JOIN seg_usuario u ON u.n_id_usuario = b.n_id_usuario
WHERE b.c_codigo_accion = 'COPIA_ARCHIVOS'
ORDER BY b.f_fecha_hora DESC
LIMIT 5;
```

### 3. Verificar Usuarios Creados

```sql
SELECT 
    c_dni,
    x_ape_paterno,
    x_nombres,
    n_id_tipo,
    f_aud AS fecha_creacion
FROM seg_usuario
WHERE c_dni LIKE 'MODULO_%' OR c_dni = 'NO_IDENTIFICADO'
ORDER BY f_aud DESC;
```

---

## 📁 ARCHIVOS MODIFICADOS

### Creados (2)
- ✅ `src/main/java/model/Usuario.java`
- ✅ `src/main/java/service/UsuarioService.java`

### Refactorizados (9)
- ✅ `src/main/java/model/Bitacora.java`
- ✅ `src/main/java/model/Modulo.java`
- ✅ `src/main/java/model/Descarga.java`
- ✅ `src/main/java/service/ModuloService.java`
- ✅ `src/main/java/service/DescargaService.java`
- ✅ `src/main/java/service/DownloaderService.java`
- ✅ `src/main/java/Main/Main.java`
- ✅ `src/main/java/UI/MasterUI.java`

### Sin Cambios (1)
- ✅ `src/main/java/service/BitacoraService.java`

---

## ✅ CHECKLIST DE DESPLIEGUE

- [ ] Backup de base de datos realizado
- [ ] Esquema refactorizado desplegado (seg_usuario, seg_modulo, met_bitacora, met_descarga)
- [ ] Triggers de auditoría creados (fn_auditar_cambios)
- [ ] Módulos migrados a seg_modulo
- [ ] persistence.xml actualizado con credenciales correctas
- [ ] DonyDriveClient compilado (`mvn clean package`)
- [ ] Test de login exitoso
- [ ] Test de login fallido
- [ ] Test de copia de archivos
- [ ] Test de logout
- [ ] Verificación SQL de bitácoras con usuarios

---

## 🆘 SOLUCIÓN DE PROBLEMAS

### Error: "No se encuentra la tabla met_bitacora"

**Causa**: Esquema de base de datos no actualizado

**Solución**:
```bash
psql -U postgres -d ASISTENTE_SANTA -f SCRIPT_MIGRACION_DATOS.sql
```

### Error: "violación FK n_id_usuario"

**Causa**: No se creó el usuario antes de insertar bitácora

**Solución**:
```java
Usuario usuario = usuarioService.createIfNotExists(dni, nombre);
bitacora.setNIdUsuario(usuario.getNIdUsuario());
```

### Error: "No such method: getIdDescarga()"

**Causa**: Código antiguo no refactorizado

**Solución**:
```java
// Reemplazar
descarga.getIdDescarga() → descarga.getNIdDescarga()
```

---

## 📞 SOPORTE

Para más detalles, consulta:
- **Documentación Completa**: `REFACTORIZACION_DONYDRIVECLIENT.md`
- **Documentación Backend**: `../asistente-expedientes-spring-main/DOCUMENTACION_REFACTORIZACION.md`

---

**Versión**: 2.0  
**Última Actualización**: 2026-03-12  
**Autor**: JC (Desarrollador)
