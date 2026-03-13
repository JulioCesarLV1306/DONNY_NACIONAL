import { useState } from 'react';
import { Bars3Icon } from '@heroicons/react/24/outline';
import { Outlet } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';

export default function DashboardLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <div className="min-h-screen bg-slate-100">
      <div className="flex min-h-screen">
        <Sidebar isOpen={isSidebarOpen} onToggle={() => setIsSidebarOpen(false)} />

        <main className="relative flex-1 p-4 md:p-8">
          {!isSidebarOpen && (
            <button
              type="button"
              onClick={() => setIsSidebarOpen(true)}
              className="mb-4 inline-flex items-center gap-2 rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 shadow-sm hover:bg-slate-50"
            >
              <Bars3Icon className="h-5 w-5" />
              Mostrar menú
            </button>
          )}
          <Outlet />
        </main>
      </div>
    </div>
  );
}
