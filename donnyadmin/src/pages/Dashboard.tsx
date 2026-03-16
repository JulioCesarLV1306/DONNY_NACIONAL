import { FormEvent, useEffect, useMemo, useState } from 'react';
import { estadisticaService } from '../services/estadistica.service';
import { Estadistica } from '../types/estadistica';
import { 
  FileText, 
  Files, 
  HardDrive, 
  Video, 
  Gavel, 
  Calendar, 
  Search, 
  RefreshCw,
  LayoutDashboard
} from 'lucide-react';

interface DashboardProps {
  title?: string;
}

// Utilidades de formato
const getTodayYmd = () => new Date().toISOString().slice(0, 10);

const formatBytes = (bytes: number) => {
  if (!Number.isFinite(bytes) || bytes <= 0) return '0 B';
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let value = bytes;
  let unitIndex = 0;
  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024;
    unitIndex += 1;
  }
  return `${value.toFixed(value >= 10 ? 0 : 1)} ${units[unitIndex]}`;
};

function sumStats(items: Estadistica[]) {
  return items.reduce(
    (acc, item) => ({
      ndocumentos: acc.ndocumentos + item.ndocumentos,
      nhojas: acc.nhojas + item.nhojas,
      nbytes: acc.nbytes + item.nbytes,
      nvideos: acc.nvideos + item.nvideos,
      nresoluciones: acc.nresoluciones + item.nresoluciones,
    }),
    { ndocumentos: 0, nhojas: 0, nbytes: 0, nvideos: 0, nresoluciones: 0 }
  );
}

export default function Dashboard({ title = 'Dashboard de Estadísticas' }: DashboardProps) {
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
    } catch (e) {
      setErrorHoy('Error al sincronizar datos de hoy.');
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
    } catch (e) {
      setErrorRango('Error al consultar el rango seleccionado.');
    } finally {
      setIsLoadingRango(false);
    }
  };

  useEffect(() => {
    void loadStatsHoy();
    void loadStatsRango(fechaInicio, fechaFin);
  }, []);

  const handleBuscarRango = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (fechaInicio > fechaFin) {
      setErrorRango('La fecha inicial es posterior a la final.');
      return;
    }
    await loadStatsRango(fechaInicio, fechaFin);
  };

  return (
    <div className="max-w-7xl mx-auto space-y-8 p-4 animate-in fade-in duration-500">
      
      {/* HEADER DINÁMICO */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <div className="p-3 bg-[#820000] rounded-2xl text-white shadow-lg shadow-[#820000]/20">
            <LayoutDashboard size={28} />
          </div>
          <div>
            <h1 className="text-2xl font-black text-slate-900 tracking-tight">{title}</h1>
            <p className="text-sm text-slate-500 font-medium">Análisis de producción documental DONNY</p>
          </div>
        </div>
        <button
          onClick={() => void loadStatsHoy()}
          className="inline-flex items-center gap-2 px-4 py-2 rounded-xl border border-slate-200 bg-white text-sm font-bold text-slate-700 hover:bg-slate-50 transition-all shadow-sm"
        >
          <RefreshCw size={16} className={isLoadingHoy ? 'animate-spin' : ''} />
          Sincronizar Hoy
        </button>
      </div>

      {/* METRICAS DE HOY */}
      <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
        <StatCard icon={<FileText />} label="Docs Hoy" value={resumenHoy.ndocumentos} color="blue" />
        <StatCard icon={<Files />} label="Hojas Hoy" value={resumenHoy.nhojas} color="purple" />
        <StatCard icon={<HardDrive />} label="Peso Hoy" value={formatBytes(resumenHoy.nbytes)} color="emerald" />
        <StatCard icon={<Video />} label="Videos Hoy" value={resumenHoy.nvideos} color="amber" />
        <StatCard icon={<Gavel />} label="Resoluciones" value={resumenHoy.nresoluciones} color="rose" />
      </section>

      {/* SECCIÓN DE CONSULTA POR RANGO */}
      <section className="rounded-[2.5rem] border border-slate-200 bg-white p-8 shadow-sm">
        <div className="flex items-center gap-3 mb-8">
          <Calendar className="text-[#820000]" size={22} />
          <h3 className="text-xl font-extrabold text-slate-800">Análisis Histórico</h3>
        </div>

        <form onSubmit={handleBuscarRango} className="grid grid-cols-1 gap-6 md:grid-cols-4 items-end bg-slate-50 p-6 rounded-3xl border border-slate-100">
          <div className="space-y-2">
            <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-1">Fecha Inicio</label>
            <input
              type="date"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 px-4 py-2.5 text-sm font-bold outline-none focus:ring-4 focus:ring-[#820000]/5"
            />
          </div>
          <div className="space-y-2">
            <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-1">Fecha Fin</label>
            <input
              type="date"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 px-4 py-2.5 text-sm font-bold outline-none focus:ring-4 focus:ring-[#820000]/5"
            />
          </div>
          <button
            type="submit"
            disabled={isLoadingRango}
            className="flex items-center justify-center gap-2 rounded-2xl bg-[#820000] px-6 py-3 text-sm font-bold text-white hover:bg-slate-900 transition-all shadow-lg shadow-[#820000]/20 disabled:opacity-50"
          >
            <Search size={18} />
            {isLoadingRango ? 'Buscando...' : 'Consultar Rango'}
          </button>
          <div className="hidden md:block text-right">
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-tighter leading-none">Resultados encontrados</p>
            <p className="text-2xl font-black text-slate-900">{statsRango.length}</p>
          </div>
        </form>

        {/* TABLA DE RESULTADOS */}
        <div className="mt-8 overflow-hidden rounded-3xl border border-slate-200">
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="bg-slate-50 text-[10px] font-black uppercase tracking-widest text-slate-400 border-b border-slate-100">
                <tr>
                  <th className="px-4 py-4">Fecha / Módulo</th>
                  <th className="px-4 py-4 text-center">Docs</th>
                  <th className="px-4 py-4 text-center">Hojas</th>
                  <th className="px-4 py-4 text-center">Peso</th>
                  <th className="px-4 py-4 text-center">Videos</th>
                  <th className="px-4 py-4 text-center">Especialidades</th>
                  <th className="px-4 py-4 text-center">Actas/Res.</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {statsRango.map((item) => (
                  <tr key={item.nidEstadistica} className="hover:bg-slate-50/50 transition-colors">
                    <td className="px-4 py-4">
                      <div className="font-bold text-slate-800">{item.ffecha}</div>
                      <div className="text-[10px] font-mono text-slate-400 tracking-tighter">MOD: {item.nidModulo}</div>
                    </td>
                    <td className="px-4 py-4 text-center font-medium">{item.ndocumentos}</td>
                    <td className="px-4 py-4 text-center font-medium">{item.nhojas}</td>
                    <td className="px-4 py-4 text-center text-slate-500 font-mono text-xs">{formatBytes(item.nbytes)}</td>
                    <td className="px-4 py-4 text-center">
                       <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${item.nvideos > 0 ? 'bg-amber-100 text-amber-700' : 'text-slate-300'}`}>
                        {item.nvideos} vid
                       </span>
                    </td>
                    <td className="px-4 py-4">
                      <div className="flex flex-wrap justify-center gap-1">
                        <Tag label="FAM" val={item.nfamilia} />
                        <Tag label="CIV" val={item.ncivil} />
                        <Tag label="LAB" val={item.nlaboral} />
                        <Tag label="PEN" val={item.npenal} />
                      </div>
                    </td>
                    <td className="px-4 py-4 text-center">
                      <div className="text-xs font-bold text-slate-700">A: {item.nactas}</div>
                      <div className="text-[10px] font-bold text-[#820000]">R: {item.nresoluciones}</div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
}

// Sub-componentes internos para limpieza
function StatCard({ icon, label, value, color }: { icon: React.ReactNode, label: string, value: any, color: string }) {
  const colors: any = {
    blue: 'text-blue-600 bg-blue-50',
    purple: 'text-purple-600 bg-purple-50',
    emerald: 'text-emerald-600 bg-emerald-50',
    amber: 'text-amber-600 bg-amber-50',
    rose: 'text-rose-600 bg-rose-50',
  };

  return (
    <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-sm transition-transform hover:scale-[1.02]">
      <div className={`w-10 h-10 rounded-2xl flex items-center justify-center mb-4 ${colors[color]}`}>
        {icon}
      </div>
      <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{label}</p>
      <p className="text-2xl font-black text-slate-900 mt-1">{value}</p>
    </div>
  );
}

function Tag({ label, val }: { label: string, val: number }) {
  if (val === 0) return null;
  return (
    <span className="px-1.5 py-0.5 rounded-md bg-slate-100 text-slate-500 text-[9px] font-black border border-slate-200">
      {label}: {val}
    </span>
  );
}