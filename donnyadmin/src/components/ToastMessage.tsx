import { useEffect, useState } from 'react';
import { CheckCircle, X } from 'lucide-react'; // Asumiendo que usas lucide-react, si no, puedes usar SVG simples

type ToastMessageProps = {
  message: string;
  onClose: () => void;
  durationMs?: number;
};

export default function ToastMessage({ message, onClose, durationMs = 4000 }: ToastMessageProps) {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    if (message) {
      setIsVisible(true);
      const timer = setTimeout(() => {
        setIsVisible(false);
        setTimeout(onClose, 300); // Espera a que termine la animación de salida
      }, durationMs);
      
      return () => clearTimeout(timer);
    }
  }, [message, onClose, durationMs]);

  if (!message) return null;

  return (
    <div
      className={`fixed right-6 top-6 z-[100] flex items-center gap-3 min-w-[300px] max-w-md 
        transform transition-all duration-300 ease-out
        ${isVisible ? 'translate-y-0 opacity-100' : '-translate-y-4 opacity-0'}
        bg-white border-l-4 border-[#820000] rounded-xl p-4 
        shadow-[0_10px_40px_-10px_rgba(0,0,0,0.1)]`}
    >
      {/* Icono vibrante */}
      <div className="flex-shrink-0 text-[#820000]">
        <CheckCircle size={24} strokeWidth={2.5} />
      </div>

      {/* Contenido del mensaje */}
      <div className="flex-1 pr-2">
        <p className="text-sm font-bold text-slate-800 leading-tight">
          Operación exitosa
        </p>
        <p className="text-xs text-slate-500 mt-0.5">
          {message}
        </p>
      </div>

      {/* Botón de cierre manual */}
      <button 
        onClick={() => { setIsVisible(false); setTimeout(onClose, 300); }}
        className="text-slate-400 hover:text-slate-600 transition-colors p-1 rounded-lg hover:bg-slate-100"
      >
        <X size={18} />
      </button>

      {/* Barra de progreso visual */}
      <div className="absolute bottom-0 left-0 h-1 bg-emerald-100 w-full rounded-b-xl overflow-hidden">
        <div 
          className="h-full bg-[#820000] transition-all linear"
          style={{ 
            width: isVisible ? '0%' : '100%', 
            transitionDuration: `${durationMs}ms` 
          }}
        />
      </div>
    </div>
  );
}