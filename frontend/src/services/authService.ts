import apiClient from "../api/axios";

export const generateToken = async (
    username: string
) => {

    const response = await apiClient.get(
        `/auth/token?username=${username}`
    );

    return response.data;
};