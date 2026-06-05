import { createSlice } from "@reduxjs/toolkit";

interface AuthState {
    token: string | null;
    isAuthenticated: boolean;
}

const initialState: AuthState = {
    token: localStorage.getItem("token"),
    isAuthenticated: !!localStorage.getItem("token")
};

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {

        setToken: (state, action) => {
            state.token = action.payload;
            state.isAuthenticated = true;
        },

        logout: (state) => {
            localStorage.removeItem("token");
            state.token = null;
            state.isAuthenticated = false;
        }
    }
});

export const { setToken, logout } =
    authSlice.actions;

export default authSlice.reducer;