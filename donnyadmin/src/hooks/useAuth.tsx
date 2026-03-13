import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services/auth.service';
import { LoginCredentials, User } from '../types/auth';

interface AuthContextValue {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
}

const USER_STORAGE_KEY = 'donnyadmin_user';
const TOKEN_STORAGE_KEY = 'donnyadmin_token';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const persistedUser = localStorage.getItem(USER_STORAGE_KEY);
    const persistedToken = localStorage.getItem(TOKEN_STORAGE_KEY);

    if (persistedUser && persistedToken) {
      setUser(JSON.parse(persistedUser));
      setToken(persistedToken);
    }

    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginCredentials) => {
    const data = await authService.login(credentials);

    if (!data.autenticado) {
      throw new Error(data.mensaje || 'Credenciales inválidas');
    }

    const sessionToken = data.token || `auth-${data.usuario.c_dni}`;

    setUser(data.usuario);
    setToken(sessionToken);
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(data.usuario));
    localStorage.setItem(TOKEN_STORAGE_KEY, sessionToken);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem(USER_STORAGE_KEY);
    localStorage.removeItem(TOKEN_STORAGE_KEY);
  };

  const value = useMemo(
    () => ({
      user,
      token,
      isAuthenticated: Boolean(token),
      isLoading,
      login,
      logout,
    }),
    [user, token, isLoading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }

  return context;
}
