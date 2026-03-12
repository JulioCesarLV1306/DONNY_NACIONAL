# 🚀 Guía Rápida de Inicio - Sistema Refactorizado V2.0

Esta guía proporciona los pasos esenciales para desplegar el sistema refactorizado con la nueva base de datos PostgreSQL auditable.

---

## 📋 Pre-requisitos

- ✅ Java 8+ instalado
- ✅ Maven 3.6+ instalado
- ✅ PostgreSQL 14+ instalado y ejecutándose
- ✅ Usuario con permisos SUPERUSER en PostgreSQL

---

## 🔧 Configuración en 5 Pasos

### Paso 1: Configurar Base de Datos

```bash
# 1. Conectarse a PostgreSQL como superusuario
psql -U postgres

# 2. Ejecutar el script de creación (desde psql)
\i c:/Users/jlopezv/Desktop/DONY_NACIONAL/basededatosrefactorizada.sql

# O desde línea de comandos:
psql -U postgres -f basededatosrefactorizada.sql
```

### Paso 2: Configurar Variables de Entorno

Editar el archivo `.env` en la raíz del proyecto:

```bash
cd asistente-expedientes-spring-main
notepad .env
```

**Contenido mínimo requerido:**
```env
gs_db_host=localhost
gs_db_port=5432
gs_db_name=ASISTENTE_SANTA
gs_db_user=ASISTENTESANTA_ADM
gs_db_pass=ADMIN7895123$$#
gs_audit_user=SYSTEM_APP
```

> ⚠️ **IMPORTANTE:** Nunca versionar el archivo `.env` en Git. Debe estar en `.gitignore`.

### Paso 3: Compilar el Proyecto

```bash
cd asistente-expedientes-spring-main
mvn clean package -DskipTests
```

Resultado esperado:
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.234 s
```

### Paso 4: Ejecutar la Aplicación

```bash
java -jar target/asistente-expedientes.jar
```

O con Maven:
```bash
mvn spring-boot:run
```

### Paso 5: Verificar Funcionamiento

**5.1 Verificar logs de inicio:**
```
[DatabaseConfig] Archivo .env cargado exitosamente
[DatabaseConfig] Configuración inicializada:
  - Driver: org.postgresql.Driver
  - URL: jdbc:postgresql://localhost:5432/ASISTENTE_SANTA
  - User: ASISTENTESANTA_ADM
  - Audit User: SYSTEM_APP
[AccesoAsistente] Conexión establecida a: jdbc:postgresql://localhost:5432/ASISTENTE_SANTA
```

**5.2 Verificar conexión a BD:**
```sql
-- Conectarse a PostgreSQL
psql -U ASISTENTESANTA_ADM -d ASISTENTE_SANTA

-- Verificar tablas creadas
\dt

-- Debería mostrar:
-- seg_modulo, seg_usuario, seg_tipo_usuario
-- met_bitacora, met_descarga, met_encuesta, met_estadistica
```

---

## 🔍 Verificación de Auditoría

### Probar Inserción con Auditoría

```sql
-- Insertar un usuario de prueba
INSERT INTO seg_usuario (n_id_tipo, c_dni, x_ape_paterno, x_ape_materno, x_nombres, c_aud_uid)
VALUES (1, '12345678', 'GARCIA', 'LOPEZ', 'JUAN CARLOS', 'TEST_USER');

-- Verificar campos de auditoría
SELECT c_dni, f_aud, b_aud, c_aud_uid FROM seg_usuario WHERE c_dni = '12345678';

-- Resultado esperado:
-- c_dni     | f_aud                       | b_aud | c_aud_uid
-- 12345678  | 2026-03-12 10:30:45.123456  | I     | TEST_USER
```

### Actualizar Registro

```sql
-- Actualizar usuario
UPDATE seg_usuario 
SET x_nombres = 'JUAN CARLOS ACTUALIZADO', c_aud_uid = 'ADMIN'
WHERE c_dni = '12345678';

-- Verificar cambio de b_aud a 'U'
SELECT c_dni, b_aud, f_aud FROM seg_usuario WHERE c_dni = '12345678';

-- Resultado esperado:
-- b_aud debería ser 'U' y f_aud debería actualizarse
```

---

## 🐛 Solución de Problemas Comunes

### Error: "No se encontro el driver de la base de datos"

**Causa:** Dependencia de PostgreSQL no incluida en `pom.xml`

**Solución:**
```xml
<!-- Agregar en pom.xml -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.5.1</version>
</dependency>
```

### Error: "No se pudo cargar el archivo .env"

**Causa:** Archivo `.env` no existe o está en ubicación incorrecta

**Solución:**
```bash
# Verificar ubicación
ls -la .env

# Copiar desde plantilla
cp .env.example .env

# Editar credenciales
notepad .env
```

### Error: "Connection refused" al conectar a PostgreSQL

**Causa:** PostgreSQL no está ejecutándose o configuración de red incorrecta

**Solución:**
```bash
# Verificar estado de PostgreSQL (Linux)
sudo systemctl status postgresql

# Iniciar PostgreSQL (Linux)
sudo systemctl start postgresql

# Verificar puerto abierto (Windows)
netstat -an | findstr :5432

# Verificar configuración de pg_hba.conf
# Debe permitir conexión local con MD5
```

### Error: "Tabla no existe" (tabla con nombre antiguo)

**Causa:** Script de migración no ejecutado completamente

**Solución:**
```bash
# Volver a ejecutar script completo
psql -U ASISTENTESANTA_ADM -d ASISTENTE_SANTA -f basededatosrefactorizada.sql
```

### Error: Campos con nombres antiguos en queries

**Causa:** Service no refactorizado o cache de compilación

**Solución:**
```bash
# Limpiar cache de Maven
mvn clean

# Recompilar
mvn package -DskipTests
```

---

## 📊 Monitoreo y Logs

### Ubicación de Logs

**Spring Boot:**
- Console output: stdout
- Archivo configurado en `application.properties`:
```properties
logging.file.name=logs/asistente-expedientes.log
logging.level.com.ncpp.asistenteexpedientes=DEBUG
```

**PostgreSQL:**
- Linux: `/var/log/postgresql/postgresql-14-main.log`
- Windows: `C:\Program Files\PostgreSQL\14\data\log\`

### Logs Clave a Monitorear

**Inicio exitoso de DatabaseConfig:**
```
[DatabaseConfig] Archivo .env cargado exitosamente desde: .env
[DatabaseConfig] Configuración inicializada:
```

**Conexión exitosa a BD:**
```
[AccesoAsistente] Conexión establecida a: jdbc:postgresql://...
```

**Inserciones exitosas:**
```
[BitacoraServiceImpl] Registro insertado en met_bitacora
[DescargaServiceImpl] Descarga creada: 123
[EstadisticasServiceImpl] Actualizado n_actas en módulo 1
```

---

## 🔄 Rollback de Emergencia

Si necesitas revertir a la versión anterior:

```bash
# 1. Detener aplicación
kill <PID>

# 2. Restaurar backup de BD
psql -U ASISTENTESANTA_ADM -d ASISTENTE_SANTA -f backup_pre_migracion.sql

# 3. Revertir código
git checkout <commit-anterior>
mvn clean package
java -jar target/asistente-expedientes.jar
```

---

## 📚 Documentación Adicional

- **Documentación Completa:** [DOCUMENTACION_REFACTORIZACION.md](DOCUMENTACION_REFACTORIZACION.md)
- **Esquema de BD:** [basededatosrefactorizada.sql](../basededatosrefactorizada.sql)
- **Configuración de Entorno:** [.env.example](.env.example)

---

## 📞 Soporte

**Equipo de Desarrollo**  
Email: equipo.desarrollo@ejemplo.com  
GLPI: 20260311  
Documentado: 12 de marzo de 2026

---

## ✅ Checklist de Despliegue

- [ ] Base de datos creada y triggers instalados
- [ ] Archivo `.env` configurado con credenciales correctas
- [ ] Proyecto compilado sin errores (`mvn package`)
- [ ] Aplicación inicia correctamente
- [ ] Logs muestran conexión exitosa a BD
- [ ] Prueba de inserción/actualización con auditoría funciona
- [ ] Datos migrados desde tablas antiguas (si aplica)
- [ ] Backup de BD anterior creado
- [ ] Frontend/Integraciones funcionan correctamente

---

**¡Listo para producción! 🎉**
