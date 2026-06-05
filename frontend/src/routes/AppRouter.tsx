import {
    BrowserRouter,
    Navigate,
    Route,
    Routes
} from "react-router-dom";

import { useSelector } from "react-redux";

import LoginPage from "../pages/LoginPage";
import DashboardPage from "../pages/DashboardPage";
import UnauthorizedPage from "../pages/UnauthorizedPage";

import { RootState } from "../redux/store";

const AppRouter = () => {

    const isAuthenticated =
        useSelector(
            (state: RootState) =>
                state.auth.isAuthenticated
        );

    return (
        <BrowserRouter>

            <Routes>

                <Route
                    path="/login"
                    element={<LoginPage />}
                />

                <Route
                    path="/dashboard"
                    element={
                        isAuthenticated
                            ? <DashboardPage />
                            : <Navigate to="/login" />
                    }
                />

                <Route
                    path="/unauthorized"
                    element={<UnauthorizedPage />}
                />

                <Route
                    path="*"
                    element={<Navigate to="/dashboard" />}
                />

            </Routes>

        </BrowserRouter>
    );
};

export default AppRouter;