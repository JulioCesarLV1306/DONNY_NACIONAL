BEGIN;

ALTER TABLE IF EXISTS public.met_estadistica
ADD COLUMN IF NOT EXISTS n_videos INTEGER;

UPDATE public.met_estadistica
SET n_videos = 0
WHERE n_videos IS NULL;

ALTER TABLE IF EXISTS public.met_estadistica
ALTER COLUMN n_videos SET NOT NULL;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'estadisticas'
    ) THEN
        UPDATE public.met_estadistica me
        SET n_videos = e.videos
        FROM public.estadisticas e
        WHERE me.n_id_modulo = e.id_modulo
          AND me.f_fecha = e.fecha;
    END IF;
END $$;

COMMIT;
