import { createContext, useState, useEffect } from 'react';
import api from '../services/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user) {
            setCurrentUser(user);
            // Set authorization header for all future requests
            api.defaults.headers.common['Authorization'] = `Bearer ${user.token}`;
        }
        setLoading(false);
    }, []);

    const login = async (credentials) => {
        try {
            setError('');
            const response = await api.post('/auth/login', credentials);
            const user = response.data;
            console.log("User data received:", user);
            localStorage.setItem('user', JSON.stringify(user));
            api.defaults.headers.common['Authorization'] = `Bearer ${user.token}`;
            setCurrentUser(user);
            return true;
        } catch (err) {
            setError('Invalid credentials');
            return false;
        }
    };

    const register = async (userData) => {
        try {
            setError('');
            const response = await api.post('/auth/register', userData);
            const user = response.data;
            localStorage.setItem('user', JSON.stringify(user));
            api.defaults.headers.common['Authorization'] = `Bearer ${user.token}`;
            setCurrentUser(user);
            return true;
        } catch (err) {
            setError('Registration failed - user may already exist');
            return false;
        }
    };

    const logout = () => {
        localStorage.removeItem('user');
        delete api.defaults.headers.common['Authorization'];
        setCurrentUser(null);
    };

    const value = {
        currentUser,
        login,
        register,
        logout,
        loading,
        error
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};