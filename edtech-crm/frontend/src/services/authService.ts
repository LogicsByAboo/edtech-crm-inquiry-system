import { apiClient, setAuthToken } from './api';
import { Admin, AuthResponse } from '../types';

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const data = await apiClient<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
    setAuthToken(data.accessToken);
    return data;
  },

  async getCurrentAdmin(): Promise<Admin> {
    return apiClient<Admin>('/auth/me', {
      method: 'GET',
    });
  },

  logout(): void {
    setAuthToken(null);
  },
};
