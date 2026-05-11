import { defineStore } from 'pinia';
import { currentUserApi, loginApi } from '@/api/auth';
const TOKEN_KEY = 'codecollab_token';
const USER_KEY = 'codecollab_user';
function loadUser() {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
        return null;
    }
    try {
        return JSON.parse(raw);
    }
    catch {
        return null;
    }
}
export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem(TOKEN_KEY) || '',
        user: loadUser(),
    }),
    getters: {
        isAuthenticated: (state) => Boolean(state.token),
    },
    actions: {
        async login(payload) {
            const response = await loginApi(payload);
            this.token = response.accessToken;
            this.user = response.user;
            localStorage.setItem(TOKEN_KEY, response.accessToken);
            localStorage.setItem(USER_KEY, JSON.stringify(response.user));
        },
        async fetchCurrentUser() {
            const user = await currentUserApi();
            this.user = user;
            localStorage.setItem(USER_KEY, JSON.stringify(user));
        },
        logout() {
            this.token = '';
            this.user = null;
            localStorage.removeItem(TOKEN_KEY);
            localStorage.removeItem(USER_KEY);
        },
    },
});
