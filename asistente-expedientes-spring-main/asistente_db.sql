CREATE USER "ASISTENTESANTA_ADM" WITH
	LOGIN
	SUPERUSER
	CREATEDB
	CREATEROLE
	INHERIT
	REPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'ADMIN7895123$$#';

CREATE DATABASE "ASISTENTE_SANTA"
  WITH OWNER = "ASISTENTESANTA_ADM"
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;

\c ASISTENTE_SANTA;

CREATE TABLE public.modulo
(
    id_modulo SERIAL PRIMARY KEY,
    pc_ip character varying(15) NOT NULL unique,
    pc_usuario character varying(300) NOT NULL,
    pc_clave character varying(100) NOT NULL,
    descripcion character varying(500) NULL,
    ubicacion character varying(200) NOT NULL,
    estado integer NOT NULL
)TABLESPACE pg_default;

CREATE TABLE public.bitacora
(
    id_bitacora SERIAL PRIMARY KEY,
    ip_modulo character varying(15) NULL,
    usuario_modulo character varying(200) NULL,
    dni_sece character varying(10) NULL,
    nombre_sece character varying(200) NULL,
    codigo_accion character varying(500) NOT NULL,
    descripcion_accion character varying(5000) NULL,
    fecha_hora timestamp not null default current_timestamp
)TABLESPACE pg_default;

CREATE TABLE public.estadisticas
(
    id_estadistica SERIAL PRIMARY KEY,
    id_modulo integer REFERENCES modulo NOT NULL,
    actas integer NOT NULL,
    resoluciones integer NOT NULL,
    documentos integer NOT NULL,
    videos integer NOT NULL,
    hojas integer NOT NULL,
    bytes bigint NULL,
    penal bigint  NULL,
    laboral bigint  NULL,
    civil bigint  NULL,
    familia bigint  NULL,
    fecha date  NULL
)TABLESPACE pg_default;


CREATE TABLE public.descarga
(
    id_descarga SERIAL PRIMARY KEY,
    key_descarga character varying(2024) NOT NULL unique
)TABLESPACE pg_default;


CREATE TABLE public.encuesta
(
    id_encuesta SERIAL PRIMARY KEY,
    id_modulo integer REFERENCES modulo NOT NULL,
    dni_sece character varying(10) NOT NULL,
    nombre_sece character varying(200) NOT NULL,
    calificacion integer NOT NULL,
    fecha_hora timestamp not null default current_timestamp
)TABLESPACE pg_default;


