/*******************************************************************************
OBJETO          : Script de Migración de Datos - Sistema de Búsqueda Judicial
TIPO DE OBJETO  : SCRIPT DE MIGRACIÓN
GLPI            : 20260311
PROPOSITO       : Migrar datos desde el esquema antiguo (sin prefijos) al nuevo
                  esquema refactorizado (con prefijos seg_ y met_)
AUTOR           : JC (Desarrollador)
CREADO EL       : 12-03-2026
VERSION         : 2.0
BASE DE DATOS   : PostgreSQL 14+

IMPORTANTE:
- Ejecutar DESPUÉS de crear el nuevo esquema con basededatosrefactorizada.sql
- Las tablas antiguas deben existir en la MISMA base de datos
- Si están en otra BD, usar pg_dump/pg_restore o dblink
*******************************************************************************/

BEGIN;

-- =============================================================================
-- PASO 1: VERIFICACIÓN PRE-MIGRACIÓN
-- =============================================================================

DO $$
BEGIN
    RAISE NOTICE '=== INICIANDO VERIFICACIÓN DE TABLAS ANTIGUAS ===';
    
    -- Verificar existencia de tablas antiguas
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'modulo') THEN
        RAISE EXCEPTION 'Tabla antigua "modulo" no existe. Verificar esquema origen.';
    END IF;
    
    RAISE NOTICE 'Tablas antiguas encontradas. Continuando migración...';
END $$;

-- =============================================================================
-- PASO 2: MIGRACIÓN DE MÓDULOS (INFRAESTRUCTURA)
-- =============================================================================

RAISE NOTICE '=== MIGRANDO TABLA: modulo -> seg_modulo ===';

INSERT INTO public.seg_modulo (
    c_pc_ip, 
    c_pc_usuario, 
    c_pc_clave, 
    x_descripcion, 
    c_ubicacion, 
    n_estado, 
    c_aud_uid
)
SELECT 
    pc_ip,
    pc_usuario,
    pc_clave,
    descripcion,
    ubicacion,
    estado,
    'MIGRATION' AS c_aud_uid
FROM public.modulo;

-- Verificar conteo
DO $$
DECLARE
    v_count_origen INTEGER;
    v_count_destino INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_origen FROM public.modulo;
    SELECT COUNT(*) INTO v_count_destino FROM public.seg_modulo WHERE c_aud_uid = 'MIGRATION';
    
    RAISE NOTICE 'Módulos migrados: % de %', v_count_destino, v_count_origen;
    
    IF v_count_origen != v_count_destino THEN
        RAISE WARNING 'Discrepancia en migración de módulos: Origen=%, Destino=%', v_count_origen, v_count_destino;
    END IF;
END $$;

-- =============================================================================
-- PASO 3: CREACIÓN DE USUARIOS DESDE BITÁCORA
-- =============================================================================

RAISE NOTICE '=== CREANDO USUARIOS DESDE BITÁCORA ===';

-- 3.1 Insertar usuarios únicos desde bitácora (DNI + Nombre completo)
INSERT INTO public.seg_usuario (
    n_id_tipo,
    c_dni,
    x_ape_paterno,
    x_ape_materno,
    x_nombres,
    l_activo,
    c_aud_uid
)
SELECT DISTINCT ON (b.dni_sece)
    4 AS n_id_tipo,  -- Tipo por defecto: "Asistente de Fiscal"
    b.dni_sece AS c_dni,
    SPLIT_PART(TRIM(b.nombre_sece), ' ', 1) AS x_ape_paterno,  -- Primer palabra = Apellido Paterno
    COALESCE(SPLIT_PART(TRIM(b.nombre_sece), ' ', 2), '') AS x_ape_materno,  -- Segunda palabra = Apellido Materno
    SUBSTRING(TRIM(b.nombre_sece) FROM POSITION(' ' IN TRIM(b.nombre_sece))+1) AS x_nombres,  -- Resto = Nombres
    'S' AS l_activo,
    'MIGRATION' AS c_aud_uid
FROM public.bitacora b
WHERE b.dni_sece IS NOT NULL 
  AND b.dni_sece != ''
  AND b.nombre_sece IS NOT NULL
ON CONFLICT (c_dni) DO NOTHING;  -- Evitar duplicados

-- 3.2 Insertar usuarios desde encuesta (si no existen)
INSERT INTO public.seg_usuario (
    n_id_tipo,
    c_dni,
    x_ape_paterno,
    x_ape_materno,
    x_nombres,
    l_activo,
    c_aud_uid
)
SELECT DISTINCT ON (e.dni_sece)
    9 AS n_id_tipo,  -- Tipo: "Invitado"
    e.dni_sece AS c_dni,
    SPLIT_PART(TRIM(e.nombre_sece), ' ', 1) AS x_ape_paterno,
    COALESCE(SPLIT_PART(TRIM(e.nombre_sece), ' ', 2), '') AS x_ape_materno,
    SUBSTRING(TRIM(e.nombre_sece) FROM POSITION(' ' IN TRIM(e.nombre_sece))+1) AS x_nombres,
    'S' AS l_activo,
    'MIGRATION' AS c_aud_uid
FROM public.encuesta e
WHERE e.dni_sece IS NOT NULL 
  AND e.dni_sece != ''
  AND e.nombre_sece IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM public.seg_usuario u WHERE u.c_dni = e.dni_sece)
ON CONFLICT (c_dni) DO NOTHING;

-- Verificar usuarios creados
DO $$
DECLARE
    v_count_usuarios INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_usuarios FROM public.seg_usuario WHERE c_aud_uid = 'MIGRATION';
    RAISE NOTICE 'Usuarios creados desde bitácora/encuesta: %', v_count_usuarios;
END $$;

-- =============================================================================
-- PASO 4: MIGRACIÓN DE BITÁCORA
-- =============================================================================

RAISE NOTICE '=== MIGRANDO TABLA: bitacora -> met_bitacora ===';

INSERT INTO public.met_bitacora (
    n_id_usuario,
    c_ip_modulo,
    c_codigo_accion,
    t_descripcion_acc,
    f_fecha_hora,
    c_aud_uid
)
SELECT 
    u.n_id_usuario,
    b.ip_modulo,
    b.codigo_accion,
    b.descripcion_accion,
    b.fecha_hora,
    'MIGRATION' AS c_aud_uid
FROM public.bitacora b
INNER JOIN public.seg_usuario u ON u.c_dni = b.dni_sece
WHERE b.dni_sece IS NOT NULL;

-- Registros sin usuario asociado (huérfanos)
INSERT INTO public.met_bitacora (
    n_id_usuario,
    c_ip_modulo,
    c_codigo_accion,
    t_descripcion_acc,
    f_fecha_hora,
    c_aud_uid
)
SELECT 
    1 AS n_id_usuario,  -- Usuario genérico/sistema
    b.ip_modulo,
    b.codigo_accion,
    b.descripcion_accion || ' [HUERFANO: DNI=' || COALESCE(b.dni_sece, 'NULL') || ']' AS t_descripcion_acc,
    b.fecha_hora,
    'MIGRATION_ORPHAN' AS c_aud_uid
FROM public.bitacora b
WHERE b.dni_sece IS NULL 
   OR NOT EXISTS (SELECT 1 FROM public.seg_usuario u WHERE u.c_dni = b.dni_sece);

-- Verificar conteo
DO $$
DECLARE
    v_count_origen INTEGER;
    v_count_destino INTEGER;
    v_count_huerfanos INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_origen FROM public.bitacora;
    SELECT COUNT(*) INTO v_count_destino FROM public.met_bitacora WHERE c_aud_uid LIKE 'MIGRATION%';
    SELECT COUNT(*) INTO v_count_huerfanos FROM public.met_bitacora WHERE c_aud_uid = 'MIGRATION_ORPHAN';
    
    RAISE NOTICE 'Bitácora migrada: % de % (Huérfanos: %)', v_count_destino, v_count_origen, v_count_huerfanos;
END $$;

-- =============================================================================
-- PASO 5: MIGRACIÓN DE DESCARGAS
-- =============================================================================

RAISE NOTICE '=== MIGRANDO TABLA: descarga -> met_descarga ===';

INSERT INTO public.met_descarga (
    c_key_descarga,
    x_estado,
    n_porcentaje_desc,
    n_conteo_desc,
    n_total_desc,
    x_mensaje_final,
    c_aud_uid
)
SELECT 
    key_descarga,
    estado,
    porcentaje_descarga,
    conteo_descarga,
    total_descarga,
    mensaje_final,
    'MIGRATION' AS c_aud_uid
FROM public.descarga;

-- Verificar conteo
DO $$
DECLARE
    v_count_origen INTEGER;
    v_count_destino INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_origen FROM public.descarga;
    SELECT COUNT(*) INTO v_count_destino FROM public.met_descarga WHERE c_aud_uid = 'MIGRATION';
    
    RAISE NOTICE 'Descargas migradas: % de %', v_count_destino, v_count_origen;
END $$;

-- =============================================================================
-- PASO 6: MIGRACIÓN DE ENCUESTAS
-- =============================================================================

RAISE NOTICE '=== MIGRANDO TABLA: encuesta -> met_encuesta ===';

INSERT INTO public.met_encuesta (
    n_id_modulo,
    n_id_usuario,
    n_calificacion,
    f_fecha_hora
)
SELECT 
    e.id_modulo,
    u.n_id_usuario,
    e.calificacion,
    e.fecha_hora
FROM public.encuesta e
INNER JOIN public.seg_usuario u ON u.c_dni = e.dni_sece;

-- Encuestas sin usuario asociado (huérfanas)
INSERT INTO public.met_encuesta (
    n_id_modulo,
    n_id_usuario,
    n_calificacion,
    f_fecha_hora
)
SELECT 
    e.id_modulo,
    1 AS n_id_usuario,  -- Usuario genérico
    e.calificacion,
    e.fecha_hora
FROM public.encuesta e
WHERE NOT EXISTS (SELECT 1 FROM public.seg_usuario u WHERE u.c_dni = e.dni_sece);

-- Verificar conteo
DO $$
DECLARE
    v_count_origen INTEGER;
    v_count_destino INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_origen FROM public.encuesta;
    SELECT COUNT(*) INTO v_count_destino FROM public.met_encuesta;
    
    RAISE NOTICE 'Encuestas migradas: % de %', v_count_destino, v_count_origen;
END $$;

-- =============================================================================
-- PASO 7: MIGRACIÓN DE ESTADÍSTICAS
-- =============================================================================

RAISE NOTICE '=== MIGRANDO TABLA: estadisticas -> met_estadistica ===';

INSERT INTO public.met_estadistica (
    n_id_modulo,
    n_actas,
    n_resoluciones,
    n_documentos,
    n_hojas,
    n_bytes,
    n_penal,
    n_laboral,
    n_civil,
    n_familia,
    f_fecha
)
SELECT 
    id_modulo,
    actas,
    resoluciones,
    documentos,
    hojas,
    bytes,
    penal,
    laboral,
    civil,
    familia,
    fecha
FROM public.estadisticas;

-- Verificar conteo
DO $$
DECLARE
    v_count_origen INTEGER;
    v_count_destino INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count_origen FROM public.estadisticas;
    SELECT COUNT(*) INTO v_count_destino FROM public.met_estadistica;
    
    RAISE NOTICE 'Estadísticas migradas: % de %', v_count_destino, v_count_origen;
END $$;

-- =============================================================================
-- PASO 8: VERIFICACIÓN POST-MIGRACIÓN
-- =============================================================================

RAISE NOTICE '=== RESUMEN DE MIGRACIÓN ===';

DO $$
BEGIN
    RAISE NOTICE 'Tabla                | Registros Migrados';
    RAISE NOTICE '---------------------+-------------------';
    RAISE NOTICE 'seg_modulo          | %', (SELECT COUNT(*) FROM public.seg_modulo);
    RAISE NOTICE 'seg_usuario         | %', (SELECT COUNT(*) FROM public.seg_usuario);
    RAISE NOTICE 'met_bitacora        | %', (SELECT COUNT(*) FROM public.met_bitacora);
    RAISE NOTICE 'met_descarga        | %', (SELECT COUNT(*) FROM public.met_descarga);
    RAISE NOTICE 'met_encuesta        | %', (SELECT COUNT(*) FROM public.met_encuesta);
    RAISE NOTICE 'met_estadistica     | %', (SELECT COUNT(*) FROM public.met_estadistica);
END $$;

-- =============================================================================
-- PASO 9: ACTUALIZACIÓN DE SECUENCIAS
-- =============================================================================

RAISE NOTICE '=== ACTUALIZANDO SECUENCIAS ===';

-- Actualizar secuencias para evitar conflictos de PK
SELECT setval('seg_modulo_n_id_modulo_seq', COALESCE((SELECT MAX(n_id_modulo) FROM seg_modulo), 1));
SELECT setval('seg_usuario_n_id_usuario_seq', COALESCE((SELECT MAX(n_id_usuario) FROM seg_usuario), 1));
SELECT setval('met_bitacora_n_id_bitacora_seq', COALESCE((SELECT MAX(n_id_bitacora) FROM met_bitacora), 1));
SELECT setval('met_descarga_n_id_descarga_seq', COALESCE((SELECT MAX(n_id_descarga) FROM met_descarga), 1));
SELECT setval('met_encuesta_n_id_encuesta_seq', COALESCE((SELECT MAX(n_id_encuesta) FROM met_encuesta), 1));
SELECT setval('met_estadistica_n_id_estadistica_seq', COALESCE((SELECT MAX(n_id_estadistica) FROM met_estadistica), 1));

RAISE NOTICE 'Secuencias actualizadas correctamente.';

-- =============================================================================
-- FINALIZACIÓN
-- =============================================================================

COMMIT;

RAISE NOTICE '=== MIGRACIÓN COMPLETADA EXITOSAMENTE ===';
RAISE NOTICE 'Próximos pasos:';
RAISE NOTICE '1. Verificar integridad de datos migrados';
RAISE NOTICE '2. Respaldar tablas antiguas: pg_dump -t modulo -t bitacora ...';
RAISE NOTICE '3. Opcional: Eliminar tablas antiguas tras validación exitosa';
RAISE NOTICE '   DROP TABLE modulo, bitacora, descarga, encuesta, estadisticas CASCADE;';
