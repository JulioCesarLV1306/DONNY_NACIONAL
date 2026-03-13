import { FormEvent, useEffect, useMemo, useState } from 'react';
import { moduloService } from '../services/modulo.service';
import { CreateModuloPayload, Modulo } from '../types/modulo';

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
  const [form, setForm] = useState<CreateModuloPayload>(initialForm);
  const [createdModulo, setCreatedModulo] = useState<Modulo | null>(null);
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [modulos, setModulos] = useState<Modulo[]>([]);
  const [isLoadingList, setIsLoadingList] = useState(false);
  const [listError, setListError] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [ipFilter, setIpFilter] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<'ALL' | '1' | '0'>('ALL');
  const [goToPageInput, setGoToPageInput] = useState('1');
  const [editingModuloId, setEditingModuloId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<CreateModuloPayload>(initialForm);
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState('');
  const [updatedModulo, setUpdatedModulo] = useState<Modulo | null>(null);

  useEffect(() => {
    try {
      const persisted = localStorage.getItem(MODULES_TABLE_STATE_KEY);
      if (!persisted) {
        return;
      }

      const parsed = JSON.parse(persisted) as {
        ipFilter?: string;
        estadoFilter?: 'ALL' | '1' | '0';
        currentPage?: number;
      };

      setIpFilter(parsed.ipFilter || '');
      setEstadoFilter(parsed.estadoFilter || 'ALL');
      if (parsed.currentPage && parsed.currentPage > 0) {
        setCurrentPage(parsed.currentPage);
      }
    } catch {
      localStorage.removeItem(MODULES_TABLE_STATE_KEY);
    }
  }, []);

  const filteredModulos = useMemo(() => {
    const normalizedIp = ipFilter.trim().toLowerCase();

    return modulos.filter((modulo) => {
      const ipOk = modulo.cPcIp.toLowerCase().includes(normalizedIp);
      const estadoOk = estadoFilter === 'ALL' ? true : String(modulo.nEstado) === estadoFilter;
      return ipOk && estadoOk;
    });
  }, [modulos, ipFilter, estadoFilter]);

  const sortedModulos = useMemo(
    () => [...filteredModulos].sort((a, b) => b.nIdModulo - a.nIdModulo),
    [filteredModulos]
  );

  const totalPages = Math.max(1, Math.ceil(sortedModulos.length / PAGE_SIZE));

  const paginatedModulos = useMemo(() => {
    const start = (currentPage - 1) * PAGE_SIZE;
    return sortedModulos.slice(start, start + PAGE_SIZE);
  }, [sortedModulos, currentPage]);

  useEffect(() => {
    setCurrentPage(1);
  }, [ipFilter, estadoFilter]);

  useEffect(() => {
    localStorage.setItem(
      MODULES_TABLE_STATE_KEY,
      JSON.stringify({
        ipFilter,
        estadoFilter,
        currentPage,
      })
    );
  }, [ipFilter, estadoFilter, currentPage]);

  useEffect(() => {
    setGoToPageInput(String(currentPage));
  }, [currentPage]);

  const loadModulos = async () => {
    setListError('');
    setIsLoadingList(true);

    try {
      const response = await moduloService.listar();
      setModulos(response);
      setCurrentPage(1);
    } catch (loadError) {
      setListError(loadError instanceof Error ? loadError.message : 'No se pudo listar módulos.');
    } finally {
      setIsLoadingList(false);
    }
  };

  useEffect(() => {
    void loadModulos();
  }, []);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setCreatedModulo(null);
    setIsSubmitting(true);

    try {
      const payload: CreateModuloPayload = {
        ...form,
        cPcIp: form.cPcIp.trim(),
        cPcUsuario: form.cPcUsuario.trim(),
        cPcClave: form.cPcClave.trim(),
        xDescripcion: form.xDescripcion.trim(),
        cUbicacion: form.cUbicacion.trim(),
      };

      const response = await moduloService.crear(payload);
      setCreatedModulo(response);
      setForm(initialForm);
      await loadModulos();
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : 'No se pudo crear el módulo.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const startEditModulo = (modulo: Modulo) => {
    setUpdatedModulo(null);
    setUpdateError('');
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

  const cancelEditModulo = () => {
    setEditingModuloId(null);
    setEditForm(initialForm);
    setUpdateError('');
  };

  const handleUpdateModulo = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!editingModuloId) {
      return;
    }

    setUpdateError('');
    setUpdatedModulo(null);
    setIsUpdating(true);

    try {
      const payload: CreateModuloPayload = {
        ...editForm,
        cPcIp: editForm.cPcIp.trim(),
        cPcUsuario: editForm.cPcUsuario.trim(),
        cPcClave: editForm.cPcClave.trim(),
        xDescripcion: editForm.xDescripcion.trim(),
        cUbicacion: editForm.cUbicacion.trim(),
      };

      const response = await moduloService.actualizar(editingModuloId, payload);
      setUpdatedModulo(response);
      await loadModulos();
    } catch (submitError) {
      setUpdateError(submitError instanceof Error ? submitError.message : 'No se pudo actualizar el módulo.');
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-xl font-semibold text-slate-900">Módulos</h2>
        <p className="mt-1 text-sm text-slate-500">Crear nuevo módulo</p>

      <form onSubmit={handleSubmit} className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
        <input
          type="text"
          value={form.cPcIp}
          onChange={(e) => setForm((prev) => ({ ...prev, cPcIp: e.target.value }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          placeholder="IP del módulo (cPcIp)"
          required
        />

        <input
          type="text"
          value={form.cPcUsuario}
          onChange={(e) => setForm((prev) => ({ ...prev, cPcUsuario: e.target.value }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          placeholder="Usuario del módulo (cPcUsuario)"
          required
        />

        <input
          type="password"
          value={form.cPcClave}
          onChange={(e) => setForm((prev) => ({ ...prev, cPcClave: e.target.value }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          placeholder="Clave del módulo (cPcClave)"
          required
        />

        <select
          value={form.nEstado}
          onChange={(e) => setForm((prev) => ({ ...prev, nEstado: Number(e.target.value) as 0 | 1 }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
        >
          <option value={1}>Activo (1)</option>
          <option value={0}>Inactivo (0)</option>
        </select>

        <input
          type="text"
          value={form.xDescripcion}
          onChange={(e) => setForm((prev) => ({ ...prev, xDescripcion: e.target.value }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
          placeholder="Descripción (xDescripcion)"
          required
        />

        <input
          type="text"
          value={form.cUbicacion}
          onChange={(e) => setForm((prev) => ({ ...prev, cUbicacion: e.target.value }))}
          className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
          placeholder="Ubicación (cUbicacion)"
          required
        />

        <button
          type="submit"
          disabled={isSubmitting}
          className="rounded-lg bg-[#820000] px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:opacity-70"
        >
          {isSubmitting ? 'Creando...' : 'Crear Módulo'}
        </button>
      </form>

        {error && <p className="mt-3 text-sm text-rose-600">{error}</p>}
        {createdModulo && (
          <pre className="mt-4 overflow-auto rounded-lg bg-[#820000] p-3 text-xs text-slate-100">
            {JSON.stringify(createdModulo, null, 2)}
          </pre>
        )}
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <h3 className="text-lg font-semibold text-slate-900">Listado de Módulos</h3>
            <p className="mt-1 text-sm text-slate-500">
              Ordenado por ID descendente y paginado de {PAGE_SIZE} en {PAGE_SIZE}.
            </p>
          </div>
          <button
            type="button"
            onClick={() => void loadModulos()}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
          >
            Recargar
          </button>
        </div>

        {listError && <p className="mt-3 text-sm text-rose-600">{listError}</p>}

        <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-2">
          <input
            type="text"
            value={ipFilter}
            onChange={(e) => setIpFilter(e.target.value)}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
            placeholder="Filtrar por IP"
          />
          <select
            value={estadoFilter}
            onChange={(e) => setEstadoFilter(e.target.value as 'ALL' | '1' | '0')}
            className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
          >
            <option value="ALL">Todos los estados</option>
            <option value="1">Activos (1)</option>
            <option value="0">Inactivos (0)</option>
          </select>
        </div>

        <div className="mt-4 overflow-x-auto rounded-xl border border-slate-200">
          <table className="min-w-full divide-y divide-slate-200 bg-white text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-600">
              <tr>
                <th className="px-3 py-2">ID</th>
                <th className="px-3 py-2">IP</th>
                <th className="px-3 py-2">Usuario</th>
                <th className="px-3 py-2">Descripción</th>
                <th className="px-3 py-2">Ubicación</th>
                <th className="px-3 py-2">Estado</th>
                <th className="px-3 py-2">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {!isLoadingList && paginatedModulos.length === 0 && (
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
              {paginatedModulos.map((modulo) => (
                <tr key={modulo.nIdModulo}>
                  <td className="px-3 py-2 font-medium">{modulo.nIdModulo}</td>
                  <td className="px-3 py-2">{modulo.cPcIp}</td>
                  <td className="px-3 py-2">{modulo.cPcUsuario}</td>
                  <td className="px-3 py-2">{modulo.xDescripcion}</td>
                  <td className="px-3 py-2">{modulo.cUbicacion}</td>
                  <td className="px-3 py-2">
                    <span
                      className={`inline-flex rounded-full border px-2 py-1 text-xs font-semibold ${getEstadoBadgeClass(
                        modulo.nEstado
                      )}`}
                      title={`Estado ${modulo.nEstado}`}
                    >
                      {getEstadoLabel(modulo.nEstado)}
                    </span>
                  </td>
                  <td className="px-3 py-2">
                    <button
                      type="button"
                      onClick={() => startEditModulo(modulo)}
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

      {editingModuloId !== null && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-[#820000]/30 p-4 backdrop-blur-sm">
          <section className="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">Actualizar Módulo #{editingModuloId}</h3>
            <p className="mt-1 text-sm text-slate-500">Editar datos del módulo seleccionado</p>

            <form onSubmit={handleUpdateModulo} className="mt-5 grid grid-cols-1 gap-4 md:grid-cols-2">
              <input
                type="text"
                value={editForm.cPcIp}
                onChange={(e) => setEditForm((prev) => ({ ...prev, cPcIp: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="IP del módulo (cPcIp)"
                required
              />

              <input
                type="text"
                value={editForm.cPcUsuario}
                onChange={(e) => setEditForm((prev) => ({ ...prev, cPcUsuario: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Usuario del módulo (cPcUsuario)"
                required
              />

              <input
                type="password"
                value={editForm.cPcClave}
                onChange={(e) => setEditForm((prev) => ({ ...prev, cPcClave: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
                placeholder="Clave del módulo (cPcClave)"
                required
              />

              <select
                value={editForm.nEstado}
                onChange={(e) => setEditForm((prev) => ({ ...prev, nEstado: Number(e.target.value) as 0 | 1 }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm"
              >
                <option value={1}>Activo (1)</option>
                <option value={0}>Inactivo (0)</option>
              </select>

              <input
                type="text"
                value={editForm.xDescripcion}
                onChange={(e) => setEditForm((prev) => ({ ...prev, xDescripcion: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
                placeholder="Descripción (xDescripcion)"
                required
              />

              <input
                type="text"
                value={editForm.cUbicacion}
                onChange={(e) => setEditForm((prev) => ({ ...prev, cUbicacion: e.target.value }))}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm md:col-span-2"
                placeholder="Ubicación (cUbicacion)"
                required
              />

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
                  onClick={cancelEditModulo}
                  className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100"
                >
                  Cancelar
                </button>
              </div>
            </form>

            {updateError && <p className="mt-3 text-sm text-rose-600">{updateError}</p>}
            {updatedModulo && (
              <pre className="mt-4 overflow-auto rounded-lg bg-[#820000] p-3 text-xs text-slate-100">
                {JSON.stringify(updatedModulo, null, 2)}
              </pre>
            )}
          </section>
        </div>
      )}
    </div>
  );
}
