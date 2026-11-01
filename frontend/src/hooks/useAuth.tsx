import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Admin, AuthResponse } from '../types';
import { authService } from '../services/authService';
import { registerUnauthorizedHandler } from '../services/api';

interface AuthContextType {
  admin: Admin | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<AuthResponse>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [admin, setAdmin] = useState<Admin | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const logout = () => {
    authService.logout();
    setAdmin(null);
  };

  useEffect(() => {
    // Register auto-logout on 401 response from API
    registerUnauthorizedHandler(() => {
      logout();
    });
  }, []);

  const login = async (email: string, pass: string): Promise<AuthResponse> => {
    setIsLoading(true);
    try {
      const response = await authService.login(email, pass);
      setAdmin(response.admin);
      return response;
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        admin,
        isAuthenticated: !!admin,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
