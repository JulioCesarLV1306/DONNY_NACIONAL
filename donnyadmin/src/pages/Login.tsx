import { FormEvent, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function Login() {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();

  const [x_correo, setCorreo] = useState('');
  const [c_dni, setDni] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setIsSubmitting(true);
    try {
      await login({ x_correo: x_correo.trim(), c_dni: c_dni.trim() });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo autenticar.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100">
      <div className="flex w-full max-w-4xl overflow-hidden rounded-2xl shadow-lg">

        {/* ── Panel izquierdo (formulario) ── */}
        <div className="flex flex-1 flex-col justify-center bg-white px-12 py-14">

          {/* Logo institucional */}
          <div className="mb-6 flex items-center gap-3">
            <img
              src="..//Corte_del_santa_logo.png" 
              alt="Poder Judicial del Perú"
              className="h-11 w-auto"
            />
            {/* <div>
              <p className="text-xs font-semibold uppercase tracking-wide text-[#8B1A1A]">
                Poder Judicial del Perú
              </p>
              <p className="text-[10px] text-slate-500">
                Corte Superior de Justicia del Santa
              </p>
            </div> */}
          </div>

          {/* Logo DONNY */}
          <div className="mb-6">
            <img
              src="..//logodonny.png" 
              alt="Donny"
              className="h-14 w-auto"
            />
            <p className="mt-1 text-xs tracking-[0.2em] text-slate-500">
              ASISTENTE DE VOZ
            </p>
          </div>

          <h1 className="text-2xl font-semibold text-slate-900">¡Bienvenido!</h1>
          <p className="mb-6 mt-1 text-sm text-slate-500">
            Para acceder, ingresa tus credenciales
          </p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="x_correo" className="mb-1 block text-sm font-medium text-slate-700">
                Usuario
              </label>
              <input
                id="x_correo"
                type="email"
                value={x_correo}
                onChange={(e) => setCorreo(e.target.value)}
                required
                placeholder="Ingresa tu usuario"
                className="w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-red-300"
              />
            </div>

            <div>
              <label htmlFor="c_dni" className="mb-1 block text-sm font-medium text-slate-700">
                Contraseña
              </label>
              <input
                id="c_dni"
                type="password"
                value={c_dni}
                onChange={(e) => setDni(e.target.value)}
                required
                placeholder="Ingresa tu contraseña"
                className="w-full rounded-lg border border-slate-300 px-3 py-2.5 text-sm outline-none focus:ring-2 focus:ring-red-300"
              />
            </div>

            {error && <p className="text-sm text-rose-600">{error}</p>}

            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full rounded-lg bg-[#CC1A1A] py-3 text-sm font-semibold text-white hover:bg-[#aa1414] disabled:cursor-not-allowed disabled:opacity-70"
            >
              {isSubmitting ? 'Autenticando...' : 'Iniciar sesión'}
            </button>
          </form>
        </div>

        {/* ── Panel derecho (decorativo) ── */}
        <div className="relative hidden w-72 overflow-hidden bg-gradient-to-b from-red-400 to-red-200 md:flex md:items-center md:justify-center">
          {/* Barras de audio de fondo */}
          <div className="absolute inset-0 flex items-end justify-around px-4 pb-4">
            {[60, 90, 50, 110, 70, 95, 45, 80, 60, 100, 55, 75].map((h, i) => (
              <div
                key={i}
                className="w-2 rounded-full bg-white/30"
                style={{ height: `${h}px` }}
              />
            ))}
          </div>
          {/* Ícono micrófono */}
          <img
            src="..//mic.png" 
            alt="Micrófono"
            className="relative z-10 h-35 w-35 drop-shadow-xl"
          />
        </div>

      </div>
    </div>
  );
}