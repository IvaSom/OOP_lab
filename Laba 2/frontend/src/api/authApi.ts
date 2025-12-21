import api from './api';
import { UserAuthDTO, UserCreateDTO, UserDTO } from '../types';

export const registerUser = async (userData: UserCreateDTO): Promise<UserDTO> => {
    const registrationData = {
        ...userData,
        role: userData.role || 'USER'
    };
    const response = await api.post('/users/register', registrationData);
    return response.data;
};

export const login = async (credentials: UserAuthDTO): Promise<UserDTO> => {
    const response = await api.post('/users/auth', {
        login: credentials.login,
        password: credentials.password
    });

    const userData = response.data;
    localStorage.setItem('authToken', String(userData.id));
    localStorage.setItem('user', JSON.stringify(userData));

    return userData;
};

export const checkLoginExists = async (login: string): Promise<boolean> => {
    const response = await api.post('/users/check-login', null, {
        params: { login }
    });
    return response.data;
};

export const checkEmailExists = async (email: string): Promise<boolean> => {
    const response = await api.post('/users/check-email', null, {
        params: { email }
    });
    return response.data;
};
