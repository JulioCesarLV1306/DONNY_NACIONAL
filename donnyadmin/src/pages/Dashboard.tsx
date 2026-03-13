import { FormEvent, useEffect, useMemo, useState } from 'react';
import { estadisticaService } from '../services/estadistica.service';
import { Estadistica } from '../types/estadistica';

interface DashboardProps {
  title?: string;
}

function getTodayYmd() {
  return new Date().toISOString().slice(0, 10);
}

function formatBytes(bytes: number) {
  if (!Number.isFinite(bytes) || bytes <= 0) {
    return '0 B';
  }

  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let value = bytes;
  let unitIndex = 0;

  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024;
    unitIndex += 1;
  }

  return `${value.toFixed(value >= 10 ? 0 : 1)} ${units[unitIndex]}`;
}

function sumStats(items: Estadistica[]) {
  return items.reduce(
    (accumulator, item) => ({
      ndocumentos: accumulator.ndocumentos + item.ndocumentos,
      nhojas: accumulator.nhojas + item.nhojas,
      nbytes: accumulator.nbytes + item.nbytes,
      nvideos: accumulator.nvideos + item.nvideos,
      nresoluciones: accumulator.nresoluciones + item.nresoluciones,
    }),
    {
      ndocumentos: 0,
      nhojas: 0,
      nbytes: 0,
      nvideos: 0,
      nresoluciones: 0,
    }
  );
}

export default function Dashboard({ title = 'Estadísticas Globales' }: DashboardProps) {
  const [statsHoy, setStatsHoy] = useState<Estadistica[]>([]);
  const [statsRango, setStatsRango] = useState<Estadistica[]>([]);
  const [isLoadingHoy, setIsLoadingHoy] = useState(false);
  const [isLoadingRango, setIsLoadingRango] = useState(false);
  const [errorHoy, setErrorHoy] = useState('');
  const [errorRango, setErrorRango] = useState('');
  const [fechaInicio, setFechaInicio] = useState(getTodayYmd().slice(0, 8) + '01');
  const [fechaFin, setFechaFin] = useState(getTodayYmd());

  const resumenHoy = useMemo(() => sumStats(statsHoy), [statsHoy]);
  const resumenRango = useMemo(() => sumStats(statsRango), [statsRango]);

  const loadStatsHoy = async () => {
    setErrorHoy('');
    setIsLoadingHoy(true);
    try {
      const response = await estadisticaService.listarHoy();
      setStatsHoy(response);
    } catch (error) {
      setErrorHoy(error instanceof Error ? error.message : 'No se pudo cargar estadísticas de hoy.');
    } finally {
      setIsLoadingHoy(false);
    }
  };

  const loadStatsRango = async (start: string, end: string) => {
    setErrorRango('');
    setIsLoadingRango(true);
    try {
      const response = await estadisticaService.listarRango(start, end);
      setStatsRango(response);
    } catch (error) {
      setErrorRango(error instanceof Error ? error.message : 'No se pudo cargar estadísticas por rango.');
    } finally {
      setIsLoadingRango(false);
    }
  };

  useEffect(() => {
    const initialFechaFin = getTodayYmd();
    const initialFechaInicio = initialFechaFin.slice(0, 8) + '01';
    void loadStatsHoy();
    void loadStatsRango(initialFechaInicio, initialFechaFin);
  }, []);

  const handleBuscarRango = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!fechaInicio || !fechaFin) {
      setErrorRango('Debes ingresar fecha inicio y fecha fin.');
      return;
    }
    if (fechaInicio > fechaFin) {
      setErrorRango('La fecha inicio no puede ser mayor que la fecha fin.');
      return;
    }

    await loadStatsRango(fechaInicio, fechaFin);
  };

  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-slate-900">{title}</h2>
        <p className="mt-1 text-sm text-slate-500">Resumen de carga documental por día y por rango de fechas.</p>

        <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-5">
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Documentos hoy</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenHoy.ndocumentos}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Hojas hoy</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenHoy.nhojas}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Peso hoy</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{formatBytes(resumenHoy.nbytes)}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Videos hoy</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenHoy.nvideos}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Resoluciones hoy</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenHoy.nresoluciones}</p>
          </div>
        </div>

        <div className="mt-4 flex items-center justify-between">
          <p className="text-xs text-slate-500">Registros de hoy: {statsHoy.length}</p>
          <button
            type="button"
            onClick={() => void loadStatsHoy()}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Recargar hoy
          </button>
        </div>
        {isLoadingHoy && <p className="mt-3 text-sm text-slate-500">Cargando estadísticas de hoy...</p>}
        {errorHoy && <p className="mt-3 text-sm text-rose-600">{errorHoy}</p>}
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold text-slate-900">Consulta por rango</h3>
        <p className="mt-1 text-sm text-slate-500">Servicio: /apiv1/estadistica/rango</p>

        <form onSubmit={handleBuscarRango} className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-4 md:items-end">
          <div>
            <label className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-600">Fecha inicio</label>
            <input
              type="date"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
              required
            />
          </div>
          <div>
            <label className="mb-1 block text-xs font-semibold uppercase tracking-wide text-slate-600">Fecha fin</label>
            <input
              type="date"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm"
              required
            />
          </div>
          <button
            type="submit"
            disabled={isLoadingRango}
            className="rounded-lg bg-[#820000] px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:opacity-70"
          >
            {isLoadingRango ? 'Consultando...' : 'Consultar rango'}
          </button>
          <div className="text-xs text-slate-500">Resultados: {statsRango.length}</div>
        </form>

        {errorRango && <p className="mt-3 text-sm text-rose-600">{errorRango}</p>}

        <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-5">
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Documentos rango</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenRango.ndocumentos}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Hojas rango</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenRango.nhojas}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Peso rango</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{formatBytes(resumenRango.nbytes)}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Videos rango</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenRango.nvideos}</p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-3">
            <p className="text-xs text-slate-500">Resoluciones rango</p>
            <p className="mt-1 text-lg font-semibold text-slate-900">{resumenRango.nresoluciones}</p>
          </div>
        </div>

        <div className="mt-4 overflow-x-auto rounded-xl border border-slate-200">
          <table className="min-w-full divide-y divide-slate-200 bg-white text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">
              <tr>
                <th className="px-3 py-2">Fecha</th>
                <th className="px-3 py-2">Módulo</th>
                <th className="px-3 py-2">Documentos</th>
                <th className="px-3 py-2">Hojas</th>
                <th className="px-3 py-2">Peso</th>
                <th className="px-3 py-2">Videos</th>
                <th className="px-3 py-2">Familia</th>
                <th className="px-3 py-2">Civil</th>
                <th className="px-3 py-2">Laboral</th>
                <th className="px-3 py-2">Penal</th>
                <th className="px-3 py-2">Actas</th>
                <th className="px-3 py-2">Resoluciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {!isLoadingRango && statsRango.length === 0 && (
                <tr>
                  <td className="px-3 py-4 text-center text-slate-500" colSpan={12}>
                    Sin registros para el rango indicado.
                  </td>
                </tr>
              )}
              {isLoadingRango && (
                <tr>
                  <td className="px-3 py-4 text-center text-slate-500" colSpan={12}>
                    Cargando estadísticas por rango...
                  </td>
                </tr>
              )}
              {statsRango.map((item) => (
                <tr key={item.nidEstadistica}>
                  <td className="px-3 py-2">{item.ffecha}</td>
                  <td className="px-3 py-2">{item.nidModulo}</td>
                  <td className="px-3 py-2">{item.ndocumentos}</td>
                  <td className="px-3 py-2">{item.nhojas}</td>
                  <td className="px-3 py-2">{formatBytes(item.nbytes)}</td>
                  <td className="px-3 py-2">{item.nvideos}</td>
                  <td className="px-3 py-2">{item.nfamilia}</td>
                  <td className="px-3 py-2">{item.ncivil}</td>
                  <td className="px-3 py-2">{item.nlaboral}</td>
                  <td className="px-3 py-2">{item.npenal}</td>
                  <td className="px-3 py-2">{item.nactas}</td>
                  <td className="px-3 py-2">{item.nresoluciones}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}
