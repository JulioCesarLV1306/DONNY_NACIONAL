import { 
  BarChart3, 
  Users, 
  LayoutGrid, 
  ShieldCheck, 
  Database, 
  ArrowRight,
  Info
} from 'lucide-react';

export default function PresentationPage() {
  return (
    <div className="max-w-6xl mx-auto space-y-8 animate-in fade-in duration-500">
      
      {/* Hero Section */}
      <section className="relative overflow-hidden rounded-3xl border border-slate-200 bg-white p-8 shadow-sm">
        <div className="absolute top-0 right-0 -mt-4 -mr-4 h-32 w-32 rounded-full bg-[#820000]/5 blur-3xl" />
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#820000]/10 text-[#820000] text-xs font-bold uppercase tracking-wider">
              Sistema Centralizado
            </div>
            <h2 className="text-3xl font-extrabold text-slate-900 tracking-tight">
              Panel de Administración <span className="text-[#820000]">DONNY</span>
            </h2>
            <p className="max-w-2xl text-slate-600 leading-relaxed">
              Gestión inteligente para la consulta de estadísticas, administración de usuarios y 
              configuración avanzada de módulos operativos.
            </p>
          </div>
          <div className="hidden lg:block p-4 bg-slate-50 rounded-2xl border border-slate-100">
            <Info className="text-slate-400" size={32} />
          </div>
        </div>
      </section>

      {/* Grid de Capacidades */}
      <section>
        <div className="flex items-center gap-3 mb-6 px-2">
          <div className="h-8 w-1 bg-[#820000] rounded-full" />
          <h3 className="text-xl font-bold text-slate-800">Capacidades del Sistema</h3>
        </div>
        
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          <FeatureCard 
            icon={<BarChart3 size={20} />}
            title="Estadísticas"
            items={[
              "Resumen diario de carga documental",
              "Análisis por rangos de fechas personalizados",
              "Detalle por módulo y tipo de registro"
            ]}
          />
          <FeatureCard 
            icon={<Users size={20} />}
            title="Usuarios"
            items={[
              "Gestión de perfiles y nuevos registros",
              "Actualización de credenciales y roles",
              "Búsqueda avanzada con paginación"
            ]}
          />
          <FeatureCard 
            icon={<LayoutGrid size={20} />}
            title="Módulos"
            items={[
              "Registro técnico (IP, Usuario, Ubicación)",
              "Monitoreo de estado operativo real",
              "Configuración de nodos de trabajo"
            ]}
          />
          <FeatureCard 
            icon={<ShieldCheck size={20} />}
            title="Control de Acceso"
            items={[
              "Autenticación segura de nivel judicial",
              "Gestión de sesiones activas",
              "Navegación por módulos funcionales"
            ]}
          />
        </div>
      </section>

      {/* Sección de Datos con diseño de Tags */}
      <section className="rounded-3xl border border-slate-200 bg-slate-50/50 p-8">
        <div className="flex items-center gap-3 mb-8">
          <Database className="text-[#820000]" size={24} />
          <h3 className="text-xl font-bold text-slate-800">Entidades de Datos</h3>
        </div>
        
        <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
          <DataBox 
            label="Usuarios" 
            content="DNI, nombres, teléfono, correo y estado jerárquico." 
          />
          <DataBox 
            label="Módulos" 
            content="IP, credenciales, descripción técnica y ubicación física." 
          />
          <DataBox 
            label="Estadísticas" 
            content="Folios, bytes, videos, actas y categorías judiciales." 
          />
        </div>
      </section>
    </div>
  );
}

// Sub-componentes para mantener el código limpio
function FeatureCard({ icon, title, items }: { icon: React.ReactNode, title: string, items: string[] }) {
  return (
    <article className="group rounded-2xl border border-slate-200 bg-white p-6 transition-all hover:shadow-md hover:border-[#820000]/20">
      <div className="flex items-center gap-4 mb-4">
        <div className="p-2.5 rounded-xl bg-slate-50 text-[#820000] group-hover:bg-[#820000] group-hover:text-white transition-colors">
          {icon}
        </div>
        <h4 className="font-bold text-slate-800">{title}</h4>
      </div>
      <ul className="space-y-3">
        {items.map((item, i) => (
          <li key={i} className="flex items-start gap-3 text-sm text-slate-600">
            <ArrowRight size={14} className="mt-0.5 text-[#820000] opacity-50" />
            {item}
          </li>
        ))}
      </ul>
    </article>
  );
}

function DataBox({ label, content }: { label: string, content: string }) {
  return (
    <div className="bg-white rounded-2xl p-5 border border-slate-200 shadow-sm">
      <span className="text-[10px] font-black uppercase tracking-widest text-[#820000]/60">
        Entidad
      </span>
      <h5 className="text-sm font-bold text-slate-800 mt-1 mb-2">{label}</h5>
      <p className="text-xs text-slate-500 leading-relaxed">
        {content}
      </p>
    </div>
  );
}