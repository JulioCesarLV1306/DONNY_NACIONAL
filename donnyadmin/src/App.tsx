import { AuthProvider } from './hooks/useAuth';
import AppRouter from './routers/AppRouter';

export default function App() {
  return (
    <AuthProvider>
      <AppRouter />
    </AuthProvider>
  );
}
