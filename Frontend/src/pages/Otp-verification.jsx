import React, { useState } from 'react';
import { BASE_URL, ENDPOINTS } from '../utils/Constants.js';
import { useLocation, useNavigate } from 'react-router-dom';

export default function VerifyOTP({ onOtpVerificationSuccess }) {
    const [otp, setOtp] = useState('');
    const [error, setError] = useState('');
    const location = useLocation(); // To access the email passed from the sign-up page
    const navigate = useNavigate();

    const handleChange = (e) => {
        setOtp(e.target.value);
        setError('');
    };

    const handleVerifyOTP = async (e) => {
        e.preventDefault();

        const url = BASE_URL + ENDPOINTS.VERIFY_OTP;
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: location.state.email, otp: otp }),
            });
            const result = await response.json();
            if (response.ok) {
                alert('OTP Verified! Your account is now active.');
                onOtpVerificationSuccess();
                navigate('/Dashboard');
            } else {
                setError(result.message || 'Invalid OTP');
            }
        } catch (error) {
            console.error('Error:', error);
            setError('An error occurred. Please try again.');
        }
    };

    return (
        <div className="h-screen flex items-center justify-center bg-gradient-to-r from-indigo-500 to-purple-600">
            <div className="w-full max-w-md bg-white rounded-xl shadow-lg p-8">
                <h1 className="text-2xl font-bold text-center text-gray-800 mb-6">Verify Your OTP</h1>
                <form onSubmit={handleVerifyOTP}>
                    <label htmlFor="otp" className="block text-sm font-medium text-gray-700">
                        Enter OTP:
                    </label>
                    <input
                        type="text"
                        id="otp"
                        value={otp}
                        onChange={handleChange}
                        placeholder="Enter OTP"
                        required
                        className="w-full px-4 py-2 mt-2 mb-4 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
                    />
                    {error && <p className="text-red-500 text-xs mb-4">{error}</p>}
                    <button
                        type="submit"
                        className="w-full bg-indigo-600 text-white py-2 px-4 rounded-lg hover:bg-indigo-700 transition-all duration-300"
                    >
                        Verify OTP
                    </button>
                </form>
            </div>
        </div>
    );
}
