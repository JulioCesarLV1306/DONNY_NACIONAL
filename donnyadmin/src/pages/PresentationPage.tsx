export default function PresentationPage() {
  return (
    <div className="space-y-6">
      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h2 className="text-2xl font-bold text-[#820000]">Panel de Administración DONNY</h2>
        <p className="mt-2 text-sm text-slate-600">
          Este panel centraliza la operación del sistema DONNY para consulta de estadísticas, administración de usuarios y
          configuración de módulos.
        </p>
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold text-[#820000]">¿Qué se puede hacer?</h3>
        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-2">
          <article className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <h4 className="text-sm font-semibold text-[#820000]">Estadísticas</h4>
            <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-slate-600">
              <li>Consultar resumen diario de carga documental.</li>
              <li>Consultar estadísticas por rango de fechas.</li>
              <li>Revisar detalle por módulo y por tipo de registro.</li>
            </ul>
          </article>

          <article className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <h4 className="text-sm font-semibold text-[#820000]">Usuarios</h4>
            <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-slate-600">
              <li>Crear usuarios del sistema.</li>
              <li>Actualizar datos de usuarios existentes.</li>
              <li>Filtrar y paginar el listado para búsqueda rápida.</li>
            </ul>
          </article>

          <article className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <h4 className="text-sm font-semibold text-[#820000]">Módulos</h4>
            <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-slate-600">
              <li>Registrar módulos de trabajo (IP, usuario, ubicación).</li>
              <li>Actualizar configuración y estado operativo del módulo.</li>
              <li>Consultar listado con filtros y paginación.</li>
            </ul>
          </article>

          <article className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <h4 className="text-sm font-semibold text-[#820000]">Control de acceso</h4>
            <ul className="mt-2 list-disc space-y-1 pl-5 text-sm text-slate-600">
              <li>Ingreso autenticado al panel.</li>
              <li>Gestión de sesión de usuario actual.</li>
              <li>Navegación centralizada por módulos funcionales.</li>
            </ul>
          </article>
        </div>
      </section>

      <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <h3 className="text-lg font-semibold text-[#820000]">¿Qué datos se manejan?</h3>
        <div className="mt-4 grid grid-cols-1 gap-4 md:grid-cols-3">
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Usuarios</p>
            <p className="mt-2 text-sm text-slate-700">
              DNI, nombres, apellidos, teléfono, correo, tipo de usuario y estado activo/inactivo.
            </p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Módulos</p>
            <p className="mt-2 text-sm text-slate-700">
              IP, credenciales del módulo, descripción, ubicación física y estado de funcionamiento.
            </p>
          </div>
          <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-slate-500">Estadísticas</p>
            <p className="mt-2 text-sm text-slate-700">
              Fecha, número de hojas, tamaño en bytes, videos, actas y categorías judiciales (civil, familia, penal,
              laboral, resoluciones).
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
