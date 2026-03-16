import { FormEvent, useEffect, useMemo, useState } from 'react';
import { moduloService } from '../services/modulo.service';
import { CreateModuloPayload, Modulo } from '../types/modulo';
import ToastMessage from '../components/ToastMessage';
import { 
  Plus, 
  RefreshCw, 
  Search, 
  Server, 
  X, 
  Edit3, 
  Monitor, 
  MapPin, 
  ChevronLeft, 
  ChevronRight,
  ShieldAlert
} from 'lucide-react';

const PAGE_SIZE = 10;
const MODULES_TABLE_STATE_KEY = 'donnyadmin_modules_table_state';

function getEstadoBadgeClass(estado: number) {
  return estado === 1
    ? 'bg-emerald-100 text-emerald-700 border-emerald-200'
    : 'bg-rose-100 text-rose-700 border-rose-200';
}

function getEstadoLabel(estado: number) {
  return estado === 1 ? 'Activo' : 'Inactivo';
}

const initialForm: CreateModuloPayload = {
  cPcIp: '',
  cPcUsuario: '',
  cPcClave: '',
  xDescripcion: '',
  cUbicacion: '',
  nEstado: 1,
};

export default function ModulesPage() {
  // UI States
  const [toastMessage, setToastMessage] = useState('');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Data States
  const [modulos, setModulos] = useState<Modulo[]>([]);
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [listError, setListError] = useState('');

  // Filter States
  const [ipFilter, setIpFilter] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<'ALL' | '1' | '0'>('ALL');
  const [currentPage, setCurrentPage] = useState(1);
  const [goToPageInput, setGoToPageInput] = useState('1');

  // Form States
  const [form, setForm] = useState<CreateModuloPayload>(initialForm);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  
  const [editingModuloId, setEditingModuloId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<CreateModuloPayload>(initialForm);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState('');

  // --- LOGIC: Persistence ---
  useEffect(() => {
    try {
      const persisted = localStorage.getItem(MODULES_TABLE_STATE_KEY);
      if (persisted) {
        const parsed = JSON.parse(persisted);
        setIpFilter(parsed.ipFilter || '');
        setEstadoFilter(parsed.estadoFilter || 'ALL');
        if (parsed.currentPage) setCurrentPage(parsed.currentPage);
      }
    } catch { localStorage.removeItem(MODULES_TABLE_STATE_KEY); }
  }, []);

  useEffect(() => {
    localStorage.setItem(MODULES_TABLE_STATE_KEY, JSON.stringify({ ipFilter, estadoFilter, currentPage }));
    setGoToPageInput(String(currentPage));
  }, [ipFilter, estadoFilter, currentPage]);

  // --- LOGIC: Fetch ---
  const loadModulos = async () => {
    setListError('');
    setIsLoadingList(true);
    try {
      const response = await moduloService.listar();
      setModulos(response);
    } catch (loadError) {
      setListError(loadError instanceof Error ? loadError.message : 'No se pudo listar módulos.');
    } finally {
      setIsLoadingList(false);
    }
  };

  useEffect(() => { void loadModulos(); }, []);

  // --- LOGIC: Filter & Pagination ---
  const filteredModulos = useMemo(() => {
    const normalizedIp = ipFilter.trim().toLowerCase();
    return modulos.filter((m) => {
      const ipOk = m.cPcIp.toLowerCase().includes(normalizedIp);
      const estadoOk = estadoFilter === 'ALL' ? true : String(m.nEstado) === estadoFilter;
      return ipOk && estadoOk;
    });
  }, [modulos, ipFilter, estadoFilter]);

  const sortedModulos = useMemo(() => [...filteredModulos].sort((a, b) => b.nIdModulo - a.nIdModulo), [filteredModulos]);
  const totalPages = Math.max(1, Math.ceil(sortedModulos.length / PAGE_SIZE));
  const paginatedModulos = useMemo(() => sortedModulos.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE), [sortedModulos, currentPage]);

  useEffect(() => { setCurrentPage(1); }, [ipFilter, estadoFilter]);

  // --- ACTIONS ---
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      await moduloService.crear(form);
      setToastMessage('Módulo registrado con éxito');
      setForm(initialForm);
      setIsCreateModalOpen(false);
      await loadModulos();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear.');
    } finally { setIsSubmitting(false); }
  };

  const startEditModulo = (modulo: Modulo) => {
    setEditingModuloId(modulo.nIdModulo);
    setEditForm({
      cPcIp: modulo.cPcIp || '',
      cPcUsuario: modulo.cPcUsuario || '',
      cPcClave: modulo.cPcClave || '',
      xDescripcion: modulo.xDescripcion || '',
      cUbicacion: modulo.cUbicacion || '',
      nEstado: modulo.nEstado === 1 ? 1 : 0,
    });
  };

  const handleUpdateModulo = async (e: FormEvent) => {
    e.preventDefault();
    if (!editingModuloId) return;
    setIsUpdating(true);
    try {
      await moduloService.actualizar(editingModuloId, editForm);
      setToastMessage('Módulo actualizado correctamente');
      setEditingModuloId(null);
      await loadModulos();
    } catch (err) {
      setUpdateError(err instanceof Error ? err.message : 'Error al actualizar.');
    } finally { setIsUpdating(false); }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6 p-4 animate-in fade-in duration-700">
      <ToastMessage message={toastMessage} onClose={() => setToastMessage('')} />

      {/* HEADER */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-8 rounded-3xl border border-slate-200 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-[#820000]/5 rounded-full -mr-16 -mt-16 blur-2xl" />
        <div className="relative z-10 flex items-center gap-4">
          <div className="p-3 bg-slate-50 rounded-2xl border border-slate-100">
            <Server className="text-[#820000]" size={28} />
          </div>
          <div>
            <h2 className="text-3xl font-black text-slate-900 tracking-tight">Módulos</h2>
            <p className="text-slate-500 mt-1 text-sm font-medium uppercase tracking-tight">Infraestructura y Puntos de Atención</p>
          </div>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="relative z-10 inline-flex items-center justify-center gap-2 rounded-2xl bg-[#820000] px-6 py-3 text-sm font-bold text-white transition-all hover:bg-slate-800 hover:shadow-xl hover:shadow-[#820000]/20 active:scale-95"
        >
          <Plus size={20} strokeWidth={3} />
          Registrar Módulo
        </button>
      </div>

      {/* FILTROS Y LISTADO */}
      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex flex-col md:flex-row gap-4">
          <div className="relative flex-[2]">
            <Search className="absolute left-4 top-3 text-slate-400" size={18} />
            <input
              type="text"
              value={ipFilter}
              onChange={(e) => setIpFilter(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 pl-12 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-[#820000]/10 outline-none transition-all"
              placeholder="Filtrar por dirección IP..."
            />
          </div>
          <select
            value={estadoFilter}
            onChange={(e) => setEstadoFilter(e.target.value as 'ALL' | '1' | '0')}
            className="flex-1 rounded-2xl border border-slate-200 px-4 py-2.5 text-sm font-medium outline-none focus:ring-2 focus:ring-[#820000]/10"
          >
            <option value="ALL">Todos los estados</option>
            <option value="1">🟢 Activos</option>
            <option value="0">🔴 Inactivos</option>
          </select>
          <button
            onClick={() => void loadModulos()}
            className="p-3 rounded-2xl border border-slate-200 text-slate-600 hover:bg-white hover:text-[#820000] transition-all"
          >
            <RefreshCw size={20} className={isLoadingList ? 'animate-spin' : ''} />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="text-[10px] font-black uppercase tracking-widest text-slate-400 bg-slate-50/50 border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">ID</th>
                <th className="px-6 py-4">Dirección IP / Red</th>
                <th className="px-6 py-4">Credencial</th>
                <th className="px-6 py-4">Descripción / Uso</th>
                <th className="px-6 py-4">Ubicación Física</th>
                <th className="px-6 py-4">Estado</th>
                <th className="px-6 py-4 text-center">Acción</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoadingList ? (
                <tr><td colSpan={7} className="px-6 py-12 text-center text-slate-400 font-medium">Sincronizando módulos operativos...</td></tr>
              ) : paginatedModulos.length === 0 ? (
                <tr><td colSpan={7} className="px-6 py-12 text-center text-slate-400">Sin módulos registrados.</td></tr>
              ) : paginatedModulos.map((m) => (
                <tr key={m.nIdModulo} className="hover:bg-slate-50/80 transition-colors group">
                  <td className="px-6 py-4 font-mono text-xs text-slate-400">{m.nIdModulo}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      <div className="w-2 h-2 rounded-full bg-blue-400 animate-pulse" />
                      <span className="font-bold text-slate-700">{m.cPcIp}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-slate-500 font-medium">{m.cPcUsuario}</td>
                  <td className="px-6 py-4 text-slate-600 italic">"{m.xDescripcion}"</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-1.5 text-slate-500">
                      <MapPin size={14} className="text-slate-400" />
                      {m.cUbicacion}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase border ${getEstadoBadgeClass(m.nEstado)}`}>
                      {getEstadoLabel(m.nEstado)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <button
                      onClick={() => startEditModulo(m)}
                      className="p-2 rounded-xl text-slate-400 hover:text-[#820000] hover:bg-[#820000]/5 transition-all opacity-0 group-hover:opacity-100"
                    >
                      <Edit3 size={18} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* PAGINACIÓN */}
        <div className="p-6 border-t border-slate-100 flex items-center justify-between bg-slate-50/30 font-bold">
          <div className="text-xs text-slate-400">Pág {currentPage} / {totalPages}</div>
          <div className="flex items-center gap-2">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(p => p - 1)}
              className="p-2 rounded-xl border border-slate-200 disabled:opacity-30 hover:bg-white"
            >
              <ChevronLeft size={18} />
            </button>
            <input
              type="number"
              value={goToPageInput}
              onChange={(e) => setGoToPageInput(e.target.value)}
              onBlur={() => setCurrentPage(Math.min(totalPages, Math.max(1, Number(goToPageInput))))}
              className="w-12 text-center text-xs py-2 rounded-xl border border-slate-200 outline-none focus:border-[#820000]"
            />
            <button
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(p => p + 1)}
              className="p-2 rounded-xl border border-slate-200 disabled:opacity-30 hover:bg-white"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </section>

      {/* MODAL CREAR / EDITAR */}
      {(isCreateModalOpen || editingModuloId !== null) && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
          <div 
            className="absolute inset-0 bg-slate-900/60 backdrop-blur-md animate-in fade-in duration-300" 
            onClick={() => { setIsCreateModalOpen(false); setEditingModuloId(null); }} 
          />
          <section className="relative w-full max-w-2xl bg-white rounded-[2.5rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300">
            <div className="p-8 border-b border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-[#820000] rounded-2xl text-white shadow-lg shadow-[#820000]/30">
                  <Monitor size={24} />
                </div>
                <div>
                  <h3 className="text-xl font-black text-slate-900">
                    {isCreateModalOpen ? 'Configurar Módulo' : `Editar Módulo #${editingModuloId}`}
                  </h3>
                  <p className="text-[10px] text-slate-500 font-black uppercase tracking-widest">Estación de Trabajo Judicial</p>
                </div>
              </div>
              <button 
                onClick={() => { setIsCreateModalOpen(false); setEditingModuloId(null); }}
                className="p-2 rounded-full hover:bg-slate-100 text-slate-400 transition-colors"
              >
                <X size={24} />
              </button>
            </div>

            <form 
              onSubmit={isCreateModalOpen ? handleSubmit : handleUpdateModulo} 
              className="p-8 grid grid-cols-1 md:grid-cols-2 gap-6"
            >
              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Dirección IP</label>
                <input
                  type="text"
                  value={isCreateModalOpen ? form.cPcIp : editForm.cPcIp}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, cPcIp: e.target.value})) : setEditForm(p => ({...p, cPcIp: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-bold focus:ring-4 focus:ring-[#820000]/5 outline-none transition-all"
                  placeholder="192.168.X.X"
                  required
                />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Estado Operativo</label>
                <select
                  value={isCreateModalOpen ? form.nEstado : editForm.nEstado}
                  onChange={(e) => {
                    const val = Number(e.target.value) as 0 | 1;
                    isCreateModalOpen ? setForm(p => ({...p, nEstado: val})) : setEditForm(p => ({...p, nEstado: val}));
                  }}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-bold focus:ring-4 focus:ring-[#820000]/5 outline-none"
                >
                  <option value={1}>ACTIVO / DISPONIBLE</option>
                  <option value={0}>INACTIVO / MANTENIMIENTO</option>
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Usuario de Acceso</label>
                <input
                  type="text"
                  value={isCreateModalOpen ? form.cPcUsuario : editForm.cPcUsuario}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, cPcUsuario: e.target.value})) : setEditForm(p => ({...p, cPcUsuario: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-medium outline-none"
                  placeholder="User_DONNY"
                  required
                />
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Clave de Enlace</label>
                <input
                  type="password"
                  value={isCreateModalOpen ? form.cPcClave : editForm.cPcClave}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, cPcClave: e.target.value})) : setEditForm(p => ({...p, cPcClave: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm outline-none"
                  placeholder="••••••••"
                  required={isCreateModalOpen}
                />
              </div>

              <div className="md:col-span-2 space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Descripción del Módulo</label>
                <input
                  type="text"
                  value={isCreateModalOpen ? form.xDescripcion : editForm.xDescripcion}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, xDescripcion: e.target.value})) : setEditForm(p => ({...p, xDescripcion: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
                  placeholder="Ej: Módulo de Atención al Público - Sala 04"
                  required
                />
              </div>

              <div className="md:col-span-2 space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Ubicación Física</label>
                <div className="relative">
                  <MapPin className="absolute left-4 top-3 text-slate-300" size={18} />
                  <input
                    type="text"
                    value={isCreateModalOpen ? form.cUbicacion : editForm.cUbicacion}
                    onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, cUbicacion: e.target.value})) : setEditForm(p => ({...p, cUbicacion: e.target.value}))}
                    className="w-full rounded-2xl border border-slate-200 pl-11 pr-4 py-3 text-sm"
                    placeholder="Sede Central - Piso 2"
                    required
                  />
                </div>
              </div>

              <div className="md:col-span-2 flex gap-4 mt-6">
                <button
                  type="submit"
                  disabled={isSubmitting || isUpdating}
                  className="flex-1 bg-[#820000] text-white font-black py-4 rounded-2xl hover:bg-slate-900 transition-all shadow-xl shadow-[#820000]/20 disabled:opacity-50 uppercase tracking-widest text-xs"
                >
                  {isSubmitting || isUpdating ? 'Procesando...' : isCreateModalOpen ? 'Guardar Configuración' : 'Actualizar Módulo'}
                </button>
                <button
                  type="button"
                  onClick={() => { setIsCreateModalOpen(false); setEditingModuloId(null); }}
                  className="px-8 text-slate-500 font-bold hover:bg-slate-50 rounded-2xl transition-all"
                >
                  Cerrar
                </button>
              </div>

              {(error || updateError) && (
                <div className="md:col-span-2 p-4 rounded-2xl bg-rose-50 border border-rose-100 flex items-center gap-3 text-rose-600 text-xs font-bold">
                  <ShieldAlert size={18} />
                  {error || updateError}
                </div>
              )}
            </form>
          </section>
        </div>
      )}
    </div>
  );
}