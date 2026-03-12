/*******************************************************************************
OBJETO          : db_sistema_busqueda_judicial (Esquema Completo)
TIPO DE OBJETO  : ESTRUCTURA + TRIGGERS + DATA INICIAL
GLPI            : 20260311
PROPOSITO       : Implementación integral de base de datos con módulos de 
                  seguridad (seg_) y métricas (met_), integrando prefijos 
                  técnicos y auditoría automática.
AUTOR           : JC (Desarrollador)
CREADO EL       : 11-03-2026 15:30
VERSION         : 2.0
BASE DE DATOS   : PostgreSQL 14+
*******************************************************************************/

BEGIN;

-- =============================================================================
-- 1. LIMPIEZA DE OBJETOS (OPCIONAL - PARA DESARROLLO)
-- =============================================================================
DROP TABLE IF EXISTS public.met_estadistica CASCADE;
DROP TABLE IF EXISTS public.met_encuesta CASCADE;
DROP TABLE IF EXISTS public.met_descarga CASCADE;
DROP TABLE IF EXISTS public.met_bitacora CASCADE;
DROP TABLE IF EXISTS public.seg_usuario CASCADE;
DROP TABLE IF EXISTS public.seg_tipo_usuario CASCADE;
DROP TABLE IF EXISTS public.seg_modulo CASCADE;

-- =============================================================================
-- 2. MÓDULO DE SEGURIDAD (seg_)
-- =============================================================================

-- Tabla de Tipos de Usuario (Roles)
CREATE TABLE public.seg_tipo_usuario (
    n_id_tipo          SERIAL PRIMARY KEY,          -- n_ Número/Secuencia
    x_nombre           VARCHAR(200) NOT NULL,       -- x_ Descripción
    x_restriccion      VARCHAR(40),                 -- x_ Descripción
    -- Auditoría Obligatoria
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP, -- f_ Fecha
    b_aud              CHAR(1) DEFAULT 'I',                   -- b_ Tipo de Dato
    c_aud_uid          VARCHAR(30)                            -- c_ Código Usuario
);

-- Tabla de Usuarios Centralizada
CREATE TABLE public.seg_usuario (
    n_id_usuario       SERIAL PRIMARY KEY,
    n_id_tipo          INTEGER NOT NULL,            -- FK a roles
    c_dni              VARCHAR(10) NOT NULL UNIQUE, -- c_ Código
    x_ape_paterno      VARCHAR(100) NOT NULL,       -- x_ Descripción
    x_ape_materno      VARCHAR(100) NOT NULL,
    x_nombres          VARCHAR(200) NOT NULL,
    c_telefono         VARCHAR(20),                 -- c_ Código
    x_correo           VARCHAR(150),
    l_activo           CHAR(1) DEFAULT 'S',          -- l_ Indicador (S/N)
    -- Auditoría Obligatoria
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    c_aud_uid          VARCHAR(30),
    CONSTRAINT fk_usuario_tipo FOREIGN KEY (n_id_tipo) REFERENCES public.seg_tipo_usuario(n_id_tipo)
);

-- Tabla de Infraestructura (Módulos/PC)
CREATE TABLE public.seg_modulo (
    n_id_modulo        SERIAL PRIMARY KEY,
    c_pc_ip            VARCHAR(15) NOT NULL UNIQUE, -- c_ Registro de negocio
    c_pc_usuario       VARCHAR(300) NOT NULL,       -- c_ Código
    c_pc_clave         VARCHAR(100) NOT NULL,
    x_descripcion      VARCHAR(500),                -- x_ Descripción
    c_ubicacion        VARCHAR(200) NOT NULL,       -- c_ Código
    n_estado           INTEGER NOT NULL,            -- n_ Secuencia
    -- Auditoría Técnica
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    c_aud_uid          VARCHAR(30)
);

-- =============================================================================
-- 3. MÓDULO DE MÉTRICAS Y SEGUIMIENTO (met_)
-- =============================================================================

-- Bitácora de Acciones vinculada a Usuario
CREATE TABLE public.met_bitacora (
    n_id_bitacora      SERIAL PRIMARY KEY,
    n_id_usuario       INTEGER NOT NULL,            -- FK a seg_usuario
    c_ip_modulo        VARCHAR(15),                 -- c_ Código
    c_codigo_accion    VARCHAR(500) NOT NULL,       -- c_ Código
    t_descripcion_acc  TEXT,                        -- t_ No estructurado
    f_fecha_hora       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP, -- f_ Fecha
    -- Auditoría
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    c_aud_uid          VARCHAR(30),
    CONSTRAINT fk_bitacora_usuario FOREIGN KEY (n_id_usuario) REFERENCES public.seg_usuario(n_id_usuario)
);

-- Control de Descargas
CREATE TABLE public.met_descarga (
    n_id_descarga      SERIAL PRIMARY KEY,
    c_key_descarga     VARCHAR(2024) NOT NULL UNIQUE,
    x_estado           VARCHAR(250),                -- x_ Descripción
    n_porcentaje_desc  INTEGER,                     -- n_ Número
    n_conteo_desc      INTEGER,
    n_total_desc       INTEGER,
    x_mensaje_final    VARCHAR(1024),
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    c_aud_uid          VARCHAR(30)
);

-- Encuestas de Satisfacción
CREATE TABLE public.met_encuesta (
    n_id_encuesta      SERIAL PRIMARY KEY,
    n_id_modulo        INTEGER NOT NULL,
    n_id_usuario       INTEGER NOT NULL,
    n_calificacion     INTEGER NOT NULL,            -- n_ Número
    f_fecha_hora       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    CONSTRAINT fk_encuesta_modulo FOREIGN KEY (n_id_modulo) REFERENCES public.seg_modulo(n_id_modulo),
    CONSTRAINT fk_encuesta_usuario FOREIGN KEY (n_id_usuario) REFERENCES public.seg_usuario(n_id_usuario)
);

-- Estadísticas Documentales
CREATE TABLE public.met_estadistica (
    n_id_estadistica   SERIAL PRIMARY KEY,
    n_id_modulo        INTEGER NOT NULL,
    n_actas            INTEGER NOT NULL,
    n_resoluciones     INTEGER NOT NULL,
    n_documentos       INTEGER NOT NULL,
    n_hojas            INTEGER NOT NULL,
    n_bytes            BIGINT,
    n_penal            BIGINT,
    n_laboral          BIGINT,
    n_civil            BIGINT,
    n_familia          BIGINT,
    f_fecha            DATE,                        -- f_ Fecha
    f_aud              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    b_aud              CHAR(1) DEFAULT 'I',
    CONSTRAINT fk_estadistica_modulo FOREIGN KEY (n_id_modulo) REFERENCES public.seg_modulo(n_id_modulo)
);

-- =============================================================================
-- 4. AUTOMATIZACIÓN DE AUDITORÍA (TRIGGERS)
-- =============================================================================

CREATE OR REPLACE FUNCTION public.fn_auditar_cambios()
RETURNS TRIGGER AS $$
BEGIN
    NEW.f_aud := CURRENT_TIMESTAMP;
    IF (TG_OP = 'INSERT') THEN
        NEW.b_aud := 'I';
    ELSIF (TG_OP = 'UPDATE') THEN
        NEW.b_aud := 'U';
    END IF;
    -- n_aud_ip se captura desde la sesión si se desea automatizar
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_aud_usuario BEFORE INSERT OR UPDATE ON seg_usuario FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambios();
CREATE TRIGGER trg_aud_bitacora BEFORE INSERT OR UPDATE ON met_bitacora FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambios();
CREATE TRIGGER trg_aud_encuesta BEFORE INSERT OR UPDATE ON met_encuesta FOR EACH ROW EXECUTE FUNCTION fn_auditar_cambios();

-- =============================================================================
-- 5. CARGA DE DATOS MAESTROS
-- =============================================================================

INSERT INTO public.seg_tipo_usuario (x_nombre, x_restriccion, c_aud_uid) VALUES 
('Administrador', 'Todo', 'SYSTEM'),
('Fiscal Provincial PE', 'Todo', 'SYSTEM'),
('Fiscal Adjunto Penal', 'Todo', 'SYSTEM'),
('Asistente de Fiscal', 'Todo', 'SYSTEM'),
('Defensor Publico', 'Todo', 'SYSTEM'),
('Procuraduria', 'Todo', 'SYSTEM'),
('Abogados (a)', 'No Penal', 'SYSTEM'),
('Parte del Proceso', 'No Penal', 'SYSTEM'),
('Invitado', 'No Penal', 'SYSTEM'),
('CEM', 'No Penal', 'SYSTEM');

COMMIT;