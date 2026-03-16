import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { 
  Home, 
  BarChart3, 
  Users, 
  Layers, 
  LogOut, 
  ChevronLeft,
  LayoutDashboard,
  Settings
} from 'lucide-react';

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
}

interface NavItemProps {
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  onClick?: () => void;
}

const NavItem: React.FC<NavItemProps> = ({ icon, label, active, onClick }) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-2xl transition-all duration-200 group ${
        active 
          ? 'bg-[#820000] text-white shadow-lg shadow-[#820000]/20' 
          : 'text-slate-500 hover:bg-slate-100 hover:text-slate-900'
      }`}
    >
      <span className={`transition-transform duration-200 ${active ? 'scale-110' : 'group-hover:scale-110'}`}>
        {icon}
      </span>
      <span className="flex-1 text-left font-bold text-sm tracking-tight">{label}</span>
      {active && <div className="w-1.5 h-1.5 rounded-full bg-white/50 animate-pulse" />}
    </button>
  );
};

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onToggle }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  const userInitial = user?.x_nombres?.charAt(0)?.toUpperCase() || 'U';

  return (
    <aside
      className={`sticky top-0 h-screen bg-white border-r border-slate-200 transition-all duration-500 ease-in-out shrink-0 z-[100] ${
        isOpen ? 'w-72' : 'w-0'
      } overflow-hidden`}
    >
      <div className="flex flex-col h-full w-72">
        {/* LOGO / BRANDING */}
        <div className="p-8">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="bg-[#820000] p-2 rounded-xl">
                <LayoutDashboard className="text-white" size={24} />
              </div>
              <h2 className="text-2xl font-black text-slate-900 tracking-tighter italic">
                DONNY
              </h2>
            </div>
            <button
              onClick={onToggle}
              className="p-2 text-slate-400 hover:bg-slate-100 rounded-xl transition-colors"
            >
              <ChevronLeft size={20} />
            </button>
          </div>
          <div className="mt-2 h-1 w-12 bg-[#820000] rounded-full" />
        </div>

        {/* NAVIGATION */}
        <nav className="flex-1 overflow-y-auto px-4 space-y-8">
          <div>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-4 mb-4">
              Menú Principal
            </p>
            <div className="space-y-1">
              <NavItem
                icon={<Home size={20} />}
                label="Inicio"
                active={location.pathname === '/dashboard/inicio'}
                onClick={() => navigate('/dashboard/inicio')}
              />
              <NavItem
                icon={<BarChart3 size={20} />}
                label="Estadísticas"
                active={location.pathname === '/dashboard/estadisticas'}
                onClick={() => navigate('/dashboard/estadisticas')}
              />
            </div>
          </div>

          <div>
            <p className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em] px-4 mb-4">
              Gestión Interna
            </p>
            <div className="space-y-1">
              <NavItem
                icon={<Users size={20} />}
                label="Usuarios"
                active={location.pathname === '/dashboard/usuarios'}
                onClick={() => navigate('/dashboard/usuarios')}
              />
              <NavItem
                icon={<Layers size={20} />}
                label="Módulos"
                active={location.pathname === '/dashboard/modulos'}
                onClick={() => navigate('/dashboard/modulos')}
              />
            </div>
          </div>
        </nav>

        {/* USER PROFILE & LOGOUT */}
        <div className="p-4 mt-auto">
          <div className="bg-slate-50 rounded-[2rem] p-4 border border-slate-100 shadow-inner">
            <div className="flex items-center gap-3 mb-4">
              <div className="relative">
                <div className="w-12 h-12 rounded-2xl bg-[#820000] flex items-center justify-center text-white text-xl font-black shadow-lg shadow-[#820000]/20">
                  {userInitial}
                </div>
                <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-emerald-500 border-2 border-white rounded-full" />
              </div>
              <div className="flex-1 overflow-hidden">
                <p className="text-sm font-black text-slate-800 truncate uppercase tracking-tighter">
                  {`${user?.x_nombres || ''} ${user?.x_ape_paterno || ''}`.trim() || 'Admin'}
                </p>
                <p className="text-[10px] font-bold text-slate-400 truncate tracking-tight">
                  {user?.x_correo || 'admin@donny.gob.pe'}
                </p>
              </div>
            </div>
            
            <button
              onClick={handleLogout}
              className="w-full flex items-center justify-center gap-2 py-3 bg-white text-rose-600 rounded-2xl border border-rose-100 hover:bg-rose-50 hover:border-rose-200 transition-all text-xs font-black uppercase tracking-widest shadow-sm"
            >
              <LogOut size={16} />
              Salir del Sistema
            </button>
          </div>
        </div>
      </div>
    </aside>
  );
};