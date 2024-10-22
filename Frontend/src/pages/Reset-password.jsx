import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {BASE_URL, ENDPOINTS} from "../utils/Constants.js";

export default function ResetPassword() {
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();  // To access the token from the URL

    // Extract token and email from URL parameters
    const searchParams = new URLSearchParams(location.search);
    const token = searchParams.get('token');
    const email = searchParams.get('email');

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
        setError('');
    };

    const handleConfirmPasswordChange = (e) => {
        setConfirmPassword(e.target.value);
        setError('');
    };

    const validatePassword = () => {
        if (password !== confirmPassword) {
            return 'Passwords do not match';
        }
        if (password.length < 8) {
            return 'Password must be at least 8 characters long';
        }
        return '';
    };

    const handleResetPasswordSubmit = async (e) => {
        e.preventDefault();

        const validationError = validatePassword();
        if (validationError) {
            setError(validationError);
            return;
        }

        const url = BASE_URL + ENDPOINTS.RESET_PASSWORD;

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ token: token, password: password, email: email }),
            });

            const result = await response.json();
            if (response.ok) {
                setMessage('Password reset successfully! Redirecting to login...');
                setTimeout(() => navigate('/'), 3000);
            } else {
                setError(result.message || 'An error occurred. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
            setError('An error occurred. Please try again.');
        }
    };

    return (
        <div className="h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md bg-white rounded-lg shadow-lg p-8">
                <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">Reset Password</h1>
                <form onSubmit={handleResetPasswordSubmit}>
                    <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                        New Password:
                    </label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={handlePasswordChange}
                        placeholder="Enter new password"
                        required
                        className="w-full px-4 py-2 mt-2 mb-4 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                    />
                    <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                        Confirm Password:
                    </label>
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={handleConfirmPasswordChange}
                        placeholder="Confirm new password"
                        required
                        className="w-full px-4 py-2 mt-2 mb-4 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                    />
                    {message && <p className="text-green-500 text-sm mb-4">{message}</p>}
                    {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
                    <button
                        type="submit"
                        className="w-full bg-indigo-600 text-white py-2 px-4 rounded-lg hover:bg-indigo-700 transition-all duration-300"
                    >
                        Reset Password
                    </button>
                </form>
            </div>
        </div>
    );
}