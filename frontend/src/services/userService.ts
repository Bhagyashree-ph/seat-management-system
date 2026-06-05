import apiClient from "../api/axios";

export const getCurrentUser = async () => {

    const response =
        await apiClient.get("/user/me");

    return response.data;
};