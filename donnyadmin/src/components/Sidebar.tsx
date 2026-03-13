import {
  ArrowRightOnRectangleIcon,
  ChartBarIcon,
  ChevronDoubleLeftIcon,
  CircleStackIcon,
  HomeIcon,
  UserIcon,
} from '@heroicons/react/24/outline';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface SidebarProps {
  isOpen: boolean;
  onToggle: () => void;
}

interface NavItemProps {
  icon: React.ReactNode;
  label: string;
  active?: boolean;
  badge?: string;
  onClick?: () => void;
}

const NavItem: React.FC<NavItemProps> = ({ icon, label, active, badge, onClick }) => {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-all ${
        active ? 'bg-red-50 text-[#a71100] font-semibold' : 'text-gray-700 hover:bg-gray-50'
      }`}
    >
      <span className="h-6 w-6">{icon}</span>
      <span className="flex-1 text-left">{label}</span>
      {badge && <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-full font-bold">{badge}</span>}
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
      className={`sticky top-0 h-screen bg-white border-r border-gray-200 shadow-lg transition-all duration-300 ease-in-out shrink-0 ${
        isOpen ? 'w-80' : 'w-0'
      } overflow-hidden`}
    >
      <div className="flex flex-col h-full">
        <div className="p-6 border-b border-gray-200 bg-gradient-to-r from-red-50 to-white">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-xl font-bold text-gray-800">Navegación</h2>
            <button
              type="button"
              onClick={onToggle}
              className="p-1 text-red-400 hover:text-gray-600 hover:bg-gray-100 rounded-md transition-colors"
              title="Ocultar menú"
            >
              <ChevronDoubleLeftIcon className="w-5 h-5" />
            </button>
          </div>
          <p className="text-sm text-gray-600">Donny Administrador</p>
        </div>

        <nav className="flex-1 overflow-y-auto p-4">
          <div className="space-y-2">
            <NavItem
              icon={<HomeIcon />}
              label="Inicio"
              active={location.pathname === '/dashboard/inicio'}
              onClick={() => navigate('/dashboard/inicio')}
            />
           
            <NavItem
              icon={<ChartBarIcon />}
              label="Estadísticas"
              active={location.pathname === '/dashboard/estadisticas'}
              onClick={() => navigate('/dashboard/estadisticas')}
            />
       

            <div className="pt-4 mt-4 border-t border-gray-200">
              <p className="text-xs font-semibold text-gray-500 uppercase mb-2 px-3">Administración</p>
              <NavItem
                icon={<UserIcon />}
                label="Usuarios"
                active={location.pathname === '/dashboard/usuarios'}
                onClick={() => navigate('/dashboard/usuarios')}
              />
              <NavItem
                icon={<CircleStackIcon />}
                label="Módulos"
                active={location.pathname === '/dashboard/modulos'}
                onClick={() => navigate('/dashboard/modulos')}
              />
            </div>
          </div>
        </nav>

        <div className="p-4 border-t border-gray-200 bg-gray-50">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-red-700 flex items-center justify-center text-white font-bold">
              {userInitial}
            </div>
            <div className="flex-1">
              <p className="text-sm font-semibold text-gray-800">
                {`${user?.x_nombres || ''} ${user?.x_ape_paterno || ''}`.trim() || 'Usuario'}
              </p>
              <p className="text-xs text-gray-500">{user?.x_correo || 'usuario@sistema.com'}</p>
            </div>
          </div>
          <button
            type="button"
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors text-sm font-medium"
          >
            <ArrowRightOnRectangleIcon className="w-4 h-4" />
            Cerrar Sesión
          </button>
        </div>
      </div>
    </aside>
  );
};
