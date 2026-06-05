import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";

import { getCurrentUser }
    from "../services/userService";

const DashboardPage = () => {

    const [user, setUser] =
        useState("");

    useEffect(() => {

        fetchUser();

    }, []);

    const fetchUser = async () => {

        try {

            const response =
                await getCurrentUser();

            setUser(response);

        } catch (error) {

            console.error(error);
        }
    };

    return (
        <MainLayout>

            <h1>Dashboard</h1>

            <p>{user}</p>

        </MainLayout>
    );
};

export default DashboardPage;