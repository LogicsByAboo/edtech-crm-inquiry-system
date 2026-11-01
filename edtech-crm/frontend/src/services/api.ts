import { ApiError } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

// In-memory token storage (stateless security per requirement)
let inMemoryToken: string | null = null;
let onUnauthorizedCallback: (() => void) | null = null;

export const setAuthToken = (token: string | null): void => {
  inMemoryToken = token;
};

export const getAuthToken = (): string | null => {
  return inMemoryToken;
};

export const registerUnauthorizedHandler = (callback: () => void): void => {
  onUnauthorizedCallback = callback;
};

export class RequestException extends Error {
  status: number;
  error: string;
  details?: Record<string, string>;

  constructor(apiError: ApiError) {
    super(apiError.message || 'An unexpected API error occurred');
    this.name = 'RequestException';
    this.status = apiError.status;
    this.error = apiError.error;
    this.details = apiError.details;
  }
}

export async function apiClient<T>(endpoint: string, init: RequestInit = {}): Promise<T> {
  const url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;

  const headers: Record<string, string> = {
    'Accept': 'application/json',
    ...(init.headers as Record<string, string> || {}),
  };

  if (init.body && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }

  if (inMemoryToken) {
    headers['Authorization'] = `Bearer ${inMemoryToken}`;
  }

  let response: Response;
  try {
    response = await fetch(url, {
      ...init,
      headers,
    });
  } catch (err: unknown) {
    throw new RequestException({
      status: 0,
      error: 'Network Error',
      message: 'Unable to connect to the EdTech CRM server. Please check your network connection or verify the backend is running.',
    });
  }

  if (response.status === 401) {
    setAuthToken(null);
    if (onUnauthorizedCallback) {
      onUnauthorizedCallback();
    }
  }

  if (!response.ok) {
    let errorPayload: ApiError;
    try {
      errorPayload = await response.json();
    } catch {
      errorPayload = {
        status: response.status,
        error: response.statusText,
        message: `HTTP ${response.status}: Request failed`,
      };
    }
    throw new RequestException(errorPayload);
  }

  // Handle empty bodies (e.g. 204 No Content)
  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}
