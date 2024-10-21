import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import SignUp from './pages/Signup.jsx';
import VerifyOtp from './pages/Otp-verification.jsx';
import Dashboard from './pages/Dashboard.jsx';
import ForgotPassword from './pages/Forget-password.jsx';
import { AUTH_TOKEN } from './utils/Constants.js';

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem(AUTH_TOKEN));

    const handleLoginSuccess = (token) => {
        localStorage.setItem(AUTH_TOKEN, token);
        setIsAuthenticated(true);
    };

    const handleLogout = () => {
        localStorage.removeItem(AUTH_TOKEN);
        setIsAuthenticated(false);
    };

    return (
        <Router>
            <Routes>
                <Route
                    path="/"
                    element={
                        isAuthenticated ? <Navigate to="/Dashboard" /> : <SignUp onLoginSuccess={handleLoginSuccess} />
                    }
                />
                <Route path="/verify-otp" element={<VerifyOtp />} />
                <Route
                    path="/Dashboard"
                    element={
                        isAuthenticated ? <Dashboard onLogout={handleLogout} /> : <Navigate to="/" />
                    }
                />
                <Route path="/forgot-password" element={<ForgotPassword />} />
            </Routes>
        </Router>
    );
}

export default App;