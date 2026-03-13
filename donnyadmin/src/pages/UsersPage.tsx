import { FormEvent, useEffect, useMemo, useState } from 'react';
import { usuarioService } from '../services/usuario.service';
import { CreateUserPayload, UserResponse } from '../types/user';

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

function getUserTypeName(typeId: number) {
  return USER_TYPES[typeId]?.name || `Tipo ${typeId}`;
}

function getUserTypeBadgeClass(typeId: number) {
  return USER_TYPES[typeId]?.badgeClass || 'bg-slate-100 text-slate-700 border-slate-200';
}

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
  const [form, setForm] = useState<CreateUserPayload>(initialForm);
  const [createdUser, setCreatedUser] = useState<UserResponse | null>(null);
  const [createError, setCreateError] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [listError, setListError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [dniFilter, setDniFilter] = useState('');
  const [correoFilter, setCorreoFilter] = useState('');
  const [goToPageInput, setGoToPageInput] = useState('1');
  const [editingUserId, setEditingUserId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<CreateUserPayload>(initialForm);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState('');
  const [updatedUser, setUpdatedUser] = useState<UserResponse | null>(null);

  useEffect(() => {
    try {
      const persisted = localStorage.getItem(USERS_TABLE_STATE_KEY);
      if (!persisted) {
        return;
      }

      const parsed = JSON.parse(persisted) as {
        dniFilter?: string;
        correoFilter?: string;
        currentPage?: number;
      };

      setDniFilter(parsed.dniFilter || '');
      setCorreoFilter(parsed.correoFilter || '');
      if (parsed.currentPage && parsed.currentPage > 0) {
        setCurrentPage(parsed.currentPage);
      }
    } catch {
      localStorage.removeItem(USERS_TABLE_STATE_KEY);
    }
  }, []);

  const filteredUsers = useMemo(() => {
    const normalizedDni = dniFilter.trim().toLowerCase();
    const normalizedCorreo = correoFilter.trim().toLowerCase();

    return users.filter((user) => {
      const dniOk = user.c_dni.toLowerCase().includes(normalizedDni);
      const correoValue = (user.x_correo || '').toLowerCase();
      const correoOk = correoValue.includes(normalizedCorreo);
      return dniOk && correoOk;
    });
  }, [users, dniFilter, correoFilter]);

  const sortedUsers = useMemo(
    () => [...filteredUsers].sort((a, b) => b.n_id_usuario - a.n_id_usuario),
    [filteredUsers]
  );

  const totalPages = Math.max(1, Math.ceil(sortedUsers.length / PAGE_SIZE));

  const paginatedUsers = useMemo(() => {
    const start = (currentPage - 1) * PAGE_SIZE;
    return sortedUsers.slice(start, start + PAGE_SIZE);
  }, [sortedUsers, currentPage]);

  useEffect(() => {
    setCurrentPage(1);
  }, [dniFilter, correoFilter]);

  useEffect(() => {
    localStorage.setItem(
      USERS_TABLE_STATE_KEY,
      JSON.stringify({
        dniFilter,
        correoFilter,
        currentPage,
      })
    );
  }, [dniFilter, correoFilter, currentPage]);

  useEffect(() => {
    setGoToPageInput(String(currentPage));
  }, [currentPage]);

  const loadUsers = async () => {
    setListError('');
    setIsLoadingList(true);

    try {
      const response = await usuarioService.listar();
      setUsers(response);
      setCurrentPage(1);
    } catch (error) {
      setListError(error instanceof Error ? error.message : 'No se pudo listar usuarios.');
    } finally {
      setIsLoadingList(false);
    }
  };

  useEffect(() => {
    void loadUsers();
  }, []);

  const handleCreate = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setCreateError('');
    setCreatedUser(null);
    setIsCreating(true);

    try {
      const payload: CreateUserPayload = {
        ...form,
        c_dni: form.c_dni.trim(),
        x_ape_paterno: form.x_ape_paterno.trim(),
        x_ape_materno: form.x_ape_materno.trim(),
        x_nombres: form.x_nombres.trim(),
        c_telefono: form.c_telefono.trim(),
        x_correo: form.x_correo.trim(),
      };

      const response = await usuarioService.crear(payload);
      setCreatedUser(response);
      setForm(initialForm);
      await loadUsers();
    } catch (error) {
      setCreateError(error instanceof Error ? error.message : 'No se pudo crear el usuario.');
    } finally {
      setIsCreating(false);
    }
  };

  const startEditUser = (user: UserResponse) => {
    setUpdatedUser(null);
    setUpdateError('');
    setEditingUserId(user.n_id_usuario);
    setEditForm({
      n_id_tipo: user.n_id_tipo,
      c_dni: user.c_dni || '',
      x_ape_paterno: user.x_ape_paterno || '',
      x_ape_materno: user.x_ape_materno || '',
      x_nombres: user.x_nombres || '',
      c_telefono: user.c_telefono || '',
      x_correo: user.x_correo || '',
      l_activo: (user.l_activo === 'N' ? 'N' : 'S') as 'S' | 'N',
    });
  };

  const cancelEditUser = () => {
    setEditingUserId(null);
    setEditForm(initialForm);
    setUpdateError('');
  };

  const handleUpdateUser = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!editingUserId) {
      return;
    }

    setUpdateError('');
    setUpdatedUser(null);
    setIsUpdating(true);

    try {
      const payload: CreateUserPayload = {
        ...editForm,
        c_dni: editForm.c_dni.trim(),
        x_ape_paterno: editForm.x_ape_paterno.trim(),
        x_ape_materno: editForm.x_ape_materno.trim(),
        x_nombres: editForm.x_nombres.trim(),
        c_telefono: editForm.c_telefono.trim(),
        x_correo: editForm.x_correo.trim(),
      };

      const response = await usuarioService.actualizar(editingUserId, payload);
      setUpdatedUser(response);
      await loadUsers();
    } catch (error) {
      setUpdateError(error instanceof Error ? error.message : 'No se pudo actualizar el usuario.');
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-slate-900">Registro de Usuarios</h2>
        <p className="mt-1 text-sm text-slate-500">Crear nuevo usuario</p>

        <form onSubmit={handleCreate} className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
          <select
            value={form.n_id_tipo}
            onChange={(e) => setForm((prev) => ({ ...prev, n_id_tipo: Number(e.target.value) || 1 }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            required
          >
            {USER_TYPE_OPTIONS.map((option) => (
              <option key={option.id} value={option.id}>
                {option.name}
              </option>
            ))}
          </select>
          <input
            type="text"
            value={form.c_dni}
            onChange={(e) => setForm((prev) => ({ ...prev, c_dni: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="c_dni"
            required
          />
          <input
            type="text"
            value={form.x_ape_paterno}
            onChange={(e) => setForm((prev) => ({ ...prev, x_ape_paterno: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Apellido paterno"
            required
          />
          <input
            type="text"
            value={form.x_ape_materno}
            onChange={(e) => setForm((prev) => ({ ...prev, x_ape_materno: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Apellido materno"
            required
          />
          <input
            type="text"
            value={form.x_nombres}
            onChange={(e) => setForm((prev) => ({ ...prev, x_nombres: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Nombres"
            required
          />
          <input
            type="text"
            value={form.c_telefono}
            onChange={(e) => setForm((prev) => ({ ...prev, c_telefono: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Teléfono"
            required
          />
          <input
            type="email"
            value={form.x_correo}
            onChange={(e) => setForm((prev) => ({ ...prev, x_correo: e.target.value }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
            placeholder="Correo"
            required
          />
          <select
            value={form.l_activo}
            onChange={(e) => setForm((prev) => ({ ...prev, l_activo: e.target.value as 'S' | 'N' }))}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          >
            <option value="S">Activo (S)</option>
            <option value="N">Inactivo (N)</option>
          </select>

          <button
            type="submit"
            disabled={isCreating}
            className="rounded-lg bg-[#820000] px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:opacity-70"
          >
            {isCreating ? 'Creando...' : 'Crear Usuario'}
          </button>
        </form>

        {createError && <p className="mt-3 text-sm text-rose-600">{createError}</p>}
        {createdUser && (
          <pre className="mt-4 overflow-auto rounded-lg bg-[#820000] p-3 text-xs text-slate-100">
            {JSON.stringify(createdUser, null, 2)}
          </pre>
        )}
      </section>

    

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <h3 className="text-lg font-semibold text-slate-900">Listado de Usuarios</h3>
            <p className="mt-1 text-sm text-slate-500">
              Ordenado por ID descendente y paginado de {PAGE_SIZE} en {PAGE_SIZE}.
            </p>
          </div>
          <button
            type="button"
            onClick={() => void loadUsers()}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Recargar
          </button>
        </div>

        {listError && <p className="mt-3 text-sm text-rose-600">{listError}</p>}

        <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-2">
          <input
            type="text"
            value={dniFilter}
            onChange={(e) => setDniFilter(e.target.value)}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Filtrar por DNI"
          />
          <input
            type="text"
            value={correoFilter}
            onChange={(e) => setCorreoFilter(e.target.value)}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Filtrar por correo"
          />
        </div>

        <div className="mt-4 overflow-x-auto rounded-xl border border-slate-200">
          <table className="min-w-full divide-y divide-slate-200 bg-white text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">
              <tr>
                <th className="px-3 py-2">ID</th>
                <th className="px-3 py-2">DNI</th>
                <th className="px-3 py-2">Nombres</th>
                <th className="px-3 py-2">Correo</th>
                <th className="px-3 py-2">Tipo</th>
                <th className="px-3 py-2">Estado</th>
                <th className="px-3 py-2">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {!isLoadingList && paginatedUsers.length === 0 && (
                <tr>
                  <td className="px-3 py-4 text-center text-slate-500" colSpan={7}>
                    Sin registros.
                  </td>
                </tr>
              )}
              {isLoadingList && (
                <tr>
                  <td className="px-3 py-4 text-center text-slate-500" colSpan={7}>
                    Cargando listado...
                  </td>
                </tr>
              )}
              {paginatedUsers.map((user) => (
                <tr key={user.n_id_usuario}>
                  <td className="px-3 py-2 font-medium">{user.n_id_usuario}</td>
                  <td className="px-3 py-2">{user.c_dni}</td>
                  <td className="px-3 py-2">{`${user.x_ape_paterno} ${user.x_ape_materno} ${user.x_nombres}`.trim()}</td>
                  <td className="px-3 py-2">{user.x_correo || '-'}</td>
                  <td className="px-3 py-2">
                    <span
                      className={`inline-flex rounded-full border px-2 py-1 text-xs font-semibold ${getUserTypeBadgeClass(
                        user.n_id_tipo
                      )}`}
                      title={`Tipo ${user.n_id_tipo}`}
                    >
                      {getUserTypeName(user.n_id_tipo)}
                    </span>
                  </td>
                  <td className="px-3 py-2">{user.l_activo}</td>
                  <td className="px-3 py-2">
                    <button
                      type="button"
                      onClick={() => startEditUser(user)}
                      className="rounded-lg border border-slate-300 px-2 py-1 text-xs font-semibold text-slate-700 hover:bg-slate-100"
                    >
                      Editar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="mt-4 flex items-center justify-between">
          <p className="text-xs text-slate-500">
            Página {currentPage} de {totalPages}
          </p>
          <div className="flex flex-wrap items-center gap-2">
            <button
              type="button"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 disabled:opacity-50"
            >
              Anterior
            </button>
            <button
              type="button"
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700 disabled:opacity-50"
            >
              Siguiente
            </button>
            <input
              type="number"
              min={1}
              max={totalPages}
              value={goToPageInput}
              onChange={(e) => setGoToPageInput(e.target.value)}
              className="w-20 rounded-lg border border-slate-300 px-2 py-1.5 text-xs"
            />
            <button
              type="button"
              onClick={() => {
                const target = Number(goToPageInput);
                if (Number.isNaN(target)) {
                  return;
                }
                setCurrentPage(Math.min(totalPages, Math.max(1, target)));
              }}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-xs font-semibold text-slate-700"
            >
              Ir a página
            </button>
          </div>
        </div>
      </section>

      {editingUserId !== null && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-[#820000]/30 p-4 backdrop-blur-sm">
          <section className="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">Actualizar Usuario #{editingUserId}</h3>
            <p className="mt-1 text-sm text-slate-500">Editar datos del usuario seleccionado</p>

            <form onSubmit={handleUpdateUser} className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
              <select
                value={editForm.n_id_tipo}
                onChange={(e) => setEditForm((prev) => ({ ...prev, n_id_tipo: Number(e.target.value) || 1 }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                required
              >
                {!USER_TYPES[editForm.n_id_tipo] && <option value={editForm.n_id_tipo}>{`Tipo ${editForm.n_id_tipo}`}</option>}
                {USER_TYPE_OPTIONS.map((option) => (
                  <option key={option.id} value={option.id}>
                    {option.name}
                  </option>
                ))}
              </select>
              <input
                type="text"
                value={editForm.c_dni}
                onChange={(e) => setEditForm((prev) => ({ ...prev, c_dni: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="c_dni"
                required
              />
              <input
                type="text"
                value={editForm.x_ape_paterno}
                onChange={(e) => setEditForm((prev) => ({ ...prev, x_ape_paterno: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Apellido paterno"
                required
              />
              <input
                type="text"
                value={editForm.x_ape_materno}
                onChange={(e) => setEditForm((prev) => ({ ...prev, x_ape_materno: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Apellido materno"
                required
              />
              <input
                type="text"
                value={editForm.x_nombres}
                onChange={(e) => setEditForm((prev) => ({ ...prev, x_nombres: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Nombres"
                required
              />
              <input
                type="text"
                value={editForm.c_telefono}
                onChange={(e) => setEditForm((prev) => ({ ...prev, c_telefono: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Teléfono"
                required
              />
              <input
                type="email"
                value={editForm.x_correo}
                onChange={(e) => setEditForm((prev) => ({ ...prev, x_correo: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
                placeholder="Correo"
                required
              />
              <select
                value={editForm.l_activo}
                onChange={(e) => setEditForm((prev) => ({ ...prev, l_activo: e.target.value as 'S' | 'N' }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
              >
                <option value="S">Activo (S)</option>
                <option value="N">Inactivo (N)</option>
              </select>

              <div className="flex items-center gap-2">
                <button
                  type="submit"
                  disabled={isUpdating}
                  className="rounded-lg bg-[#820000] px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:opacity-70"
                >
                  {isUpdating ? 'Actualizando...' : 'Guardar Cambios'}
                </button>
                <button
                  type="button"
                  onClick={cancelEditUser}
                  className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
                >
                  Cancelar
                </button>
              </div>
            </form>

            {updateError && <p className="mt-3 text-sm text-rose-600">{updateError}</p>}
            {updatedUser && (
              <pre className="mt-4 overflow-auto rounded-lg bg-[#820000] p-3 text-xs text-slate-100">
                {JSON.stringify(updatedUser, null, 2)}
              </pre>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
