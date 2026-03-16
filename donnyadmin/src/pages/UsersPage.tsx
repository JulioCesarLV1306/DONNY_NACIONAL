import { FormEvent, useEffect, useMemo, useState } from 'react';
import { usuarioService } from '../services/usuario.service';
import { CreateUserPayload, UserResponse } from '../types/user';
import ToastMessage from '../components/ToastMessage';
import { 
  Plus, 
  RefreshCw, 
  Search, 
  UserPlus, 
  X, 
  Edit3, 
  Mail, 
  Fingerprint, 
  ChevronLeft, 
  ChevronRight,
  MoreHorizontal
} from 'lucide-react';

const PAGE_SIZE = 10;
const USERS_TABLE_STATE_KEY = 'donnyadmin_users_table_state';

const USER_TYPES: Record<number, { name: string; badgeClass: string }> = {
  1: { name: 'Administrador', badgeClass: 'bg-purple-100 text-purple-700 border-purple-200' },
  2: { name: 'Fiscal Provincial PE', badgeClass: 'bg-blue-100 text-blue-700 border-blue-200' },
  3: { name: 'Fiscal Adjunto Penal', badgeClass: 'bg-indigo-100 text-indigo-700 border-indigo-200' },
  4: { name: 'Asistente de Fiscal', badgeClass: 'bg-cyan-100 text-cyan-700 border-cyan-200' },
  5: { name: 'Defensor Público', badgeClass: 'bg-emerald-100 text-emerald-700 border-emerald-200' },
  6: { name: 'Procuraduría', badgeClass: 'bg-lime-100 text-lime-700 border-lime-200' },
  7: { name: 'Abogados (a)', badgeClass: 'bg-amber-100 text-amber-700 border-amber-200' },
  8: { name: 'Parte del Proceso', badgeClass: 'bg-orange-100 text-orange-700 border-orange-200' },
  9: { name: 'Invitado', badgeClass: 'bg-slate-100 text-slate-700 border-slate-200' },
  10: { name: 'CEM', badgeClass: 'bg-rose-100 text-rose-700 border-rose-200' },
};

const USER_TYPE_OPTIONS = Object.entries(USER_TYPES)
  .map(([id, data]) => ({ id: Number(id), name: data.name }))
  .sort((a, b) => a.id - b.id);

const initialForm: CreateUserPayload = {
  n_id_tipo: 1,
  c_dni: '',
  x_ape_paterno: '',
  x_ape_materno: '',
  x_nombres: '',
  c_telefono: '',
  x_correo: '',
  l_activo: 'S',
};

export default function UsersPage() {
  // UI States
  const [toastMessage, setToastMessage] = useState('');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  
  // Data States
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [listError, setListError] = useState('');
  
  // Filter States
  const [dniFilter, setDniFilter] = useState('');
  const [correoFilter, setCorreoFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [goToPageInput, setGoToPageInput] = useState('1');

  // Form States (Create)
  const [form, setForm] = useState<CreateUserPayload>(initialForm);
  const [isCreating, setIsCreating] = useState(false);
  const [createError, setCreateError] = useState('');

  // Form States (Edit)
  const [editingUserId, setEditingUserId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<CreateUserPayload>(initialForm);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState('');

  // --- LOGIC: Persistence ---
  useEffect(() => {
    try {
      const persisted = localStorage.getItem(USERS_TABLE_STATE_KEY);
      if (persisted) {
        const parsed = JSON.parse(persisted);
        setDniFilter(parsed.dniFilter || '');
        setCorreoFilter(parsed.correoFilter || '');
        if (parsed.currentPage) setCurrentPage(parsed.currentPage);
      }
    } catch { localStorage.removeItem(USERS_TABLE_STATE_KEY); }
  }, []);

  useEffect(() => {
    localStorage.setItem(USERS_TABLE_STATE_KEY, JSON.stringify({ dniFilter, correoFilter, currentPage }));
    setGoToPageInput(String(currentPage));
  }, [dniFilter, correoFilter, currentPage]);

  // --- LOGIC: Fetch ---
  const loadUsers = async () => {
    setListError('');
    setIsLoadingList(true);
    try {
      const response = await usuarioService.listar();
      setUsers(response);
    } catch (error) {
      setListError(error instanceof Error ? error.message : 'Error al cargar usuarios.');
    } finally {
      setIsLoadingList(false);
    }
  };

  useEffect(() => { void loadUsers(); }, []);

  // --- LOGIC: Filter & Pagination ---
  const filteredUsers = useMemo(() => {
    const d = dniFilter.trim().toLowerCase();
    const c = correoFilter.trim().toLowerCase();
    return users.filter(u => u.c_dni.toLowerCase().includes(d) && (u.x_correo || '').toLowerCase().includes(c));
  }, [users, dniFilter, correoFilter]);

  const sortedUsers = useMemo(() => [...filteredUsers].sort((a, b) => b.n_id_usuario - a.n_id_usuario), [filteredUsers]);
  const totalPages = Math.max(1, Math.ceil(sortedUsers.length / PAGE_SIZE));
  const paginatedUsers = useMemo(() => sortedUsers.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE), [sortedUsers, currentPage]);

  useEffect(() => { setCurrentPage(1); }, [dniFilter, correoFilter]);

  // --- ACTIONS: Create ---
  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    setCreateError('');
    setIsCreating(true);
    try {
      await usuarioService.crear(form);
      setToastMessage('Usuario creado correctamente');
      setForm(initialForm);
      setIsCreateModalOpen(false);
      await loadUsers();
    } catch (error) {
      setCreateError(error instanceof Error ? error.message : 'Error al crear.');
    } finally {
      setIsCreating(false);
    }
  };

  // --- ACTIONS: Edit ---
  const startEditUser = (user: UserResponse) => {
    setEditingUserId(user.n_id_usuario);
    setEditForm({
      n_id_tipo: user.n_id_tipo,
      c_dni: user.c_dni || '',
      x_ape_paterno: user.x_ape_paterno || '',
      x_ape_materno: user.x_ape_materno || '',
      x_nombres: user.x_nombres || '',
      c_telefono: user.c_telefono || '',
      x_correo: user.x_correo || '',
      l_activo: user.l_activo === 'N' ? 'N' : 'S',
    });
  };

  const handleUpdateUser = async (e: FormEvent) => {
    e.preventDefault();
    if (!editingUserId) return;
    setIsUpdating(true);
    try {
      await usuarioService.actualizar(editingUserId, editForm);
      setToastMessage('Usuario actualizado con éxito');
      setEditingUserId(null);
      await loadUsers();
    } catch (error) {
      setUpdateError(error instanceof Error ? error.message : 'Error al actualizar.');
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto space-y-6 p-4 animate-in fade-in duration-700">
      <ToastMessage message={toastMessage} onClose={() => setToastMessage('')} />

      {/* HEADER */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-8 rounded-3xl border border-slate-200 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-[#820000]/5 rounded-full -mr-16 -mt-16 blur-2xl" />
        <div className="relative z-10">
          <h2 className="text-3xl font-black text-slate-900 tracking-tight">Usuarios</h2>
          <p className="text-slate-500 mt-1">Control de acceso institucional del sistema DONNY.</p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="relative z-10 inline-flex items-center justify-center gap-2 rounded-2xl bg-[#820000] px-6 py-3 text-sm font-bold text-white transition-all hover:bg-slate-800 hover:shadow-xl hover:shadow-[#820000]/20 active:scale-95"
        >
          <Plus size={20} strokeWidth={3} />
          Nuevo Usuario
        </button>
      </div>

      {/* FILTROS Y TABLA */}
      <section className="rounded-3xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="p-6 border-b border-slate-100 bg-slate-50/50 flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Fingerprint className="absolute left-4 top-3 text-slate-400" size={18} />
            <input
              type="text"
              value={dniFilter}
              onChange={(e) => setDniFilter(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 pl-12 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-[#820000]/10 outline-none transition-all"
              placeholder="Buscar por DNI..."
            />
          </div>
          <div className="relative flex-1">
            <Mail className="absolute left-4 top-3 text-slate-400" size={18} />
            <input
              type="text"
              value={correoFilter}
              onChange={(e) => setCorreoFilter(e.target.value)}
              className="w-full rounded-2xl border border-slate-200 pl-12 pr-4 py-2.5 text-sm focus:ring-2 focus:ring-[#820000]/10 outline-none transition-all"
              placeholder="Buscar por correo..."
            />
          </div>
          <button
            onClick={() => void loadUsers()}
            className="p-3 rounded-2xl border border-slate-200 text-slate-600 hover:bg-white hover:text-[#820000] transition-all"
          >
            <RefreshCw size={20} className={isLoadingList ? 'animate-spin' : ''} />
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="text-xs font-bold uppercase tracking-wider text-slate-500 bg-slate-50/50 border-b border-slate-100">
              <tr>
                <th className="px-6 py-4">Usuario</th>
                <th className="px-6 py-4">Identificación</th>
                <th className="px-6 py-4">Contacto</th>
                <th className="px-6 py-4">Rol / Tipo</th>
                <th className="px-6 py-4">Estado</th>
                <th className="px-6 py-4 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {isLoadingList ? (
                <tr><td colSpan={6} className="px-6 py-10 text-center text-slate-400">Cargando base de datos...</td></tr>
              ) : paginatedUsers.length === 0 ? (
                <tr><td colSpan={6} className="px-6 py-10 text-center text-slate-400">No se encontraron resultados.</td></tr>
              ) : paginatedUsers.map((user) => (
                <tr key={user.n_id_usuario} className="hover:bg-slate-50/80 transition-colors">
                  <td className="px-6 py-4">
                    <div className="font-bold text-slate-800">{`${user.x_nombres} ${user.x_ape_paterno}`}</div>
                    <div className="text-[10px] text-slate-400 font-mono">ID: {user.n_id_usuario}</div>
                  </td>
                  <td className="px-6 py-4 font-medium text-slate-600">{user.c_dni}</td>
                  <td className="px-6 py-4 text-slate-500">{user.x_correo || '-'}</td>
                  <td className="px-6 py-4">
                    <span className={`px-3 py-1 rounded-full text-[11px] font-bold border ${USER_TYPES[user.n_id_tipo]?.badgeClass || ''}`}>
                      {USER_TYPES[user.n_id_tipo]?.name || 'Invitado'}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`h-2.5 w-2.5 rounded-full inline-block mr-2 ${user.l_activo === 'S' ? 'bg-emerald-500' : 'bg-slate-300'}`} />
                    <span className="text-xs font-medium uppercase">{user.l_activo === 'S' ? 'Activo' : 'Inactivo'}</span>
                  </td>
                  <td className="px-6 py-4 text-center">
                    <button
                      onClick={() => startEditUser(user)}
                      className="p-2 rounded-xl text-slate-400 hover:text-[#820000] hover:bg-[#820000]/5 transition-all"
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
        <div className="p-6 border-t border-slate-100 flex items-center justify-between bg-slate-50/30">
          <div className="text-xs font-bold text-slate-400 uppercase tracking-widest">
            Pág {currentPage} de {totalPages}
          </div>
          <div className="flex items-center gap-2">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(p => p - 1)}
              className="p-2 rounded-xl border border-slate-200 disabled:opacity-30 hover:bg-white transition-all"
            >
              <ChevronLeft size={18} />
            </button>
            <input
              type="number"
              value={goToPageInput}
              onChange={(e) => setGoToPageInput(e.target.value)}
              onBlur={() => setCurrentPage(Math.min(totalPages, Math.max(1, Number(goToPageInput))))}
              className="w-12 text-center text-xs font-bold py-2 rounded-xl border border-slate-200 outline-none focus:border-[#820000]"
            />
            <button
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(p => p + 1)}
              className="p-2 rounded-xl border border-slate-200 disabled:opacity-30 hover:bg-white transition-all"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </section>

      {/* MODAL CREAR / EDITAR */}
      {(isCreateModalOpen || editingUserId !== null) && (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
          <div 
            className="absolute inset-0 bg-slate-900/60 backdrop-blur-md animate-in fade-in duration-300" 
            onClick={() => { setIsCreateModalOpen(false); setEditingUserId(null); }} 
          />
          <section className="relative w-full max-w-2xl bg-white rounded-[2.5rem] shadow-2xl overflow-hidden animate-in zoom-in-95 duration-300">
            <div className="p-8 border-b border-slate-100 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="p-3 bg-[#820000] rounded-2xl text-white shadow-lg shadow-[#820000]/30">
                  {isCreateModalOpen ? <UserPlus size={24} /> : <Edit3 size={24} />}
                </div>
                <div>
                  <h3 className="text-xl font-black text-slate-900">
                    {isCreateModalOpen ? 'Nuevo Registro' : `Editar Usuario #${editingUserId}`}
                  </h3>
                  <p className="text-xs text-slate-500 font-medium uppercase tracking-tighter">Sistema de Gestión Judicial</p>
                </div>
              </div>
              <button 
                onClick={() => { setIsCreateModalOpen(false); setEditingUserId(null); }}
                className="p-2 rounded-full hover:bg-slate-100 text-slate-400 transition-colors"
              >
                <X size={24} />
              </button>
            </div>

            <form 
              onSubmit={isCreateModalOpen ? handleCreate : handleUpdateUser} 
              className="p-8 grid grid-cols-1 md:grid-cols-2 gap-6"
            >
              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Rol del Usuario</label>
                <select
                  value={isCreateModalOpen ? form.n_id_tipo : editForm.n_id_tipo}
                  onChange={(e) => {
                    const val = Number(e.target.value);
                    isCreateModalOpen ? setForm(p => ({...p, n_id_tipo: val})) : setEditForm(p => ({...p, n_id_tipo: val}));
                  }}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm font-medium focus:ring-4 focus:ring-[#820000]/5 outline-none"
                  required
                >
                  {USER_TYPE_OPTIONS.map(o => <option key={o.id} value={o.id}>{o.name}</option>)}
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Documento (DNI)</label>
                <input
                  type="text"
                  maxLength={8}
                  value={isCreateModalOpen ? form.c_dni : editForm.c_dni}
                  onChange={(e) => {
                    const val = e.target.value.replace(/\D/g, '');
                    isCreateModalOpen ? setForm(p => ({...p, c_dni: val})) : setEditForm(p => ({...p, c_dni: val}));
                  }}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm focus:ring-4 focus:ring-[#820000]/5 outline-none"
                  placeholder="8 dígitos"
                  required
                />
              </div>

              <input
                type="text"
                placeholder="Nombres"
                value={isCreateModalOpen ? form.x_nombres : editForm.x_nombres}
                onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, x_nombres: e.target.value})) : setEditForm(p => ({...p, x_nombres: e.target.value}))}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
                required
              />
              
              <div className="grid grid-cols-2 gap-4">
                <input
                  type="text"
                  placeholder="Ap. Paterno"
                  value={isCreateModalOpen ? form.x_ape_paterno : editForm.x_ape_paterno}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, x_ape_paterno: e.target.value})) : setEditForm(p => ({...p, x_ape_paterno: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
                  required
                />
                <input
                  type="text"
                  placeholder="Ap. Materno"
                  value={isCreateModalOpen ? form.x_ape_materno : editForm.x_ape_materno}
                  onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, x_ape_materno: e.target.value})) : setEditForm(p => ({...p, x_ape_materno: e.target.value}))}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
                  required
                />
              </div>

              <input
                type="email"
                placeholder="Correo Electrónico"
                value={isCreateModalOpen ? form.x_correo : editForm.x_correo}
                onChange={(e) => isCreateModalOpen ? setForm(p => ({...p, x_correo: e.target.value})) : setEditForm(p => ({...p, x_correo: e.target.value}))}
                className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm md:col-span-2"
                required
              />

              <div className="space-y-1">
                <label className="text-[10px] font-black uppercase text-slate-400 ml-1">Estado de Cuenta</label>
                <select
                  value={isCreateModalOpen ? form.l_activo : editForm.l_activo}
                  onChange={(e) => {
                    const val = e.target.value as 'S' | 'N';
                    isCreateModalOpen ? setForm(p => ({...p, l_activo: val})) : setEditForm(p => ({...p, l_activo: val}));
                  }}
                  className="w-full rounded-2xl border border-slate-200 px-4 py-3 text-sm"
                >
                  <option value="S">ACTIVO</option>
                  <option value="N">INACTIVO</option>
                </select>
              </div>

              <div className="md:col-span-2 flex gap-4 mt-4">
                <button
                  type="submit"
                  disabled={isCreating || isUpdating}
                  className="flex-1 bg-[#820000] text-white font-bold py-4 rounded-2xl hover:bg-slate-900 transition-all shadow-xl shadow-[#820000]/20 disabled:opacity-50"
                >
                  {isCreating || isUpdating ? 'Procesando...' : isCreateModalOpen ? 'Guardar Usuario' : 'Actualizar Datos'}
                </button>
                <button
                  type="button"
                  onClick={() => { setIsCreateModalOpen(false); setEditingUserId(null); }}
                  className="px-8 text-slate-500 font-bold hover:bg-slate-50 rounded-2xl transition-all"
                >
                  Cerrar
                </button>
              </div>

              {(createError || updateError) && (
                <div className="md:col-span-2 p-4 rounded-2xl bg-rose-50 border border-rose-100 text-rose-600 text-xs font-bold text-center">
                  {createError || updateError}
                </div>
              )}
            </form>
          </section>
        </div>
      )}
    </div>
  );
}