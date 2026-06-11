import { create } from 'zustand';
import type { LoginResponse } from '../types';

interface AuthState {
  token: string | null;
  username: string | null;
  displayName: string | null;
  role: string | null;
  setAuth: (data: LoginResponse) => void;
  clearAuth: () => void;
  isLoggedIn: () => boolean;
}

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';

function loadToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

function loadUser(): { username: string; displayName: string; role: string } | null {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

const savedUser = loadUser();

export const useAuthStore = create<AuthState>((set, get) => ({
  token: loadToken(),
  username: savedUser?.username ?? null,
  displayName: savedUser?.displayName ?? null,
  role: savedUser?.role ?? null,

  setAuth: (data: LoginResponse) => {
    localStorage.setItem(TOKEN_KEY, data.token);
    localStorage.setItem(USER_KEY, JSON.stringify({
      username: data.username,
      displayName: data.displayName,
      role: data.role,
    }));
    set({
      token: data.token,
      username: data.username,
      displayName: data.displayName,
      role: data.role,
    });
  },

  clearAuth: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    set({ token: null, username: null, displayName: null, role: null });
  },

  isLoggedIn: () => !!get().token,
}));
