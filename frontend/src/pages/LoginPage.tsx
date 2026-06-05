import { useState } from "react";

import { useDispatch } from "react-redux";

import { generateToken } from "../services/authService";

import { setToken } from "../redux/slices/authSlice";

const LoginPage = () => {

    const [loading, setLoading] =
        useState(false);

    const dispatch = useDispatch();

    const handleLogin = async () => {

        try {

            setLoading(true);

            const token =
                await generateToken("admin");

            localStorage.setItem(
                "token",
                token
            );

            dispatch(setToken(token));

        } catch (error) {

            console.error(error);

        } finally {

            setLoading(false);
        }
    };

    return (
        <div style={{ padding: "20px" }}>

            <h1>Login Page</h1>

            <button
                onClick={handleLogin}
            >
                {
                    loading
                        ? "Loading..."
                        : "Login"
                }
            </button>

        </div>
    );
};

export default LoginPage;