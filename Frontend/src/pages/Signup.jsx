import React, { useState } from 'react';
import { AUTH_TOKEN, BASE_URL, ENDPOINTS } from '../utils/Constants.js';
import { useNavigate } from 'react-router-dom';
import '../App.css';

export default function Component({ onLoginSuccess }) {
  const [action, setAction] = useState('Login');
  const [formData, setFormData] = useState({
    username: '',
    name: '',
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState({
    username: '',
    name: '',
    email: '',
    password: '',
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setErrors({ ...errors, [name]: '' });
  };

  const handleForgotPasswordClick = () => {
    navigate('/forgot-password');
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const validateForm = () => {
    const newErrors = {
      username: '',
      name: '',
      email: '',
      password: '',
    };
    if (!formData.username && action === 'Sign Up') {
      newErrors.username = 'Username is required';
    }
    if (!formData.name && action === 'Sign Up') {
      newErrors.name = 'Name is required';
    }
    if (!formData.email) {
      newErrors.email = 'Email is required';
    } else if (!validateEmail(formData.email)) {
      newErrors.email = 'Invalid email format';
    }
    if (!formData.password) {
      newErrors.password = 'Password is required';
    } else if (formData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters long';
    }
    return newErrors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validateForm();
    if (Object.values(validationErrors).some((error) => error !== '')) {
      setErrors(validationErrors);
      return;
    }

    const url = BASE_URL + (action === 'Sign Up' ? ENDPOINTS.REGISTER : ENDPOINTS.LOGIN);
    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });
      const result = await response.json();
      if (response.ok) {
        if (action === 'Sign Up') {
          navigate('/verify-otp', { state: { email: formData.email } });
        } else {
          onLoginSuccess(result.data.token);
        }
      } else {
        // alert('Error: ' + result.message);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };


  return (
      <div className="h-screen flex flex-col lg:flex-row">
        {/* Left Image part */}
        <div className="auth_img_section hidden lg:flex w-full lg:w-1/2 justify-center items-center bg-indigo-600">
          <div className="w-full mx-auto px-20 flex-col items-center space-y-6 text-center lg:text-left">
            <h1 className="text-white font-bold text-4xl font-sans">Fund Fusion</h1>
            <p className="text-white mt-1">The simplest app to use</p>
            <div className="flex justify-center lg:justify-start mt-6">
              <a
                  href="#"
                  className="hover:bg-white hover:text-indigo-600 hover:-translate-y-1 transition-all duration-500 bg-transparent border-2 border-white text-white mt-4 px-4 py-2 rounded-2xl font-bold mb-2"
              >
                Get Started
              </a>
            </div>
          </div>
        </div>

        {/* Right Form part */}
        <div className="flex w-full lg:w-1/2 justify-center items-center bg-white space-y-8">
          <div className="w-full px-8 md:px-32 lg:px-24">
            <form className="bg-white rounded-md shadow-2xl p-5" onSubmit={handleSubmit}>
              <h1 className="text-gray-800 font-bold text-2xl mb-1">
                {action === 'Sign Up' ? 'Create an Account' : 'Welcome Back!'}
              </h1>
              <p className="text-sm font-normal text-gray-600 mb-8">
                {action === 'Sign Up' ? 'Please fill the form' : 'Please login to your account'}
              </p>

              {action === 'Sign Up' && (
                  <>
                    <div className="flex items-center border-2 mb-1 py-2 px-3 rounded-2xl">
                      <input
                          id="name"
                          className="pl-2 w-full outline-none border-none"
                          type="text"
                          name="name"
                          value={formData.name}
                          onChange={handleChange}
                          placeholder="Name"
                          aria-label="Name"
                      />
                    </div>
                    {errors.name && <p className="text-red-500 text-xs mb-4" aria-live="polite">{errors.name}</p>}
                  </>
              )}

              <div className="flex items-center border-2 mb-1 py-2 px-3 rounded-2xl">
                <input
                    id="email"
                    className="pl-2 w-full outline-none border-none"
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Email"
                    aria-label="Email"
                />
              </div>
              {errors.email && <p className="text-red-500 text-xs mb-4" aria-live="polite">{errors.email}</p>}

              {action === 'Sign Up' && (
                  <>
                    <div className="flex items-center border-2 mb-1 py-2 px-3 rounded-2xl">
                      <input
                          id="username"
                          className="pl-2 w-full outline-none border-none"
                          type="text"
                          name="username"
                          value={formData.username}
                          onChange={handleChange}
                          placeholder="Username"
                          aria-label="Username"
                      />
                    </div>
                    {errors.username && <p className="text-red-500 text-xs mb-4" aria-live="polite">{errors.username}</p>}
                  </>
              )}

              <div className="flex items-center border-2 mb-1 py-2 px-3 rounded-2xl">
                <input
                    id="password"
                    className="pl-2 w-full outline-none border-none"
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Password"
                    aria-label="Password"
                />
              </div>
              {errors.password && <p className="text-red-500 text-xs mb-4" aria-live="polite">{errors.password}</p>}
              {action === 'Login' && (
                  <div className="flex justify-end">
                    <button
                        type="button"
                        onClick={handleForgotPasswordClick}
                        className="text-indigo-500 hover:text-indigo-600 text-sm"
                    >
                      Forgot Password?
                    </button>
                  </div>
              )}
              <button
                  type="submit"
                  className="block w-full bg-indigo-600 mt-5 py-2 rounded-2xl hover:bg-indigo-700 hover:-translate-y-1 transition-all duration-500 text-white font-semibold mb-2"
              >
                {action === 'Sign Up' ? 'Sign Up' : 'Login'}
              </button>

              <div className="text-center mt-4">
                <p className="text-gray-600">
                  {action === 'Sign Up' ? 'Already have an account?' : "Don't have an account?"}{' '}
                  <button
                      type="button"
                      onClick={() => setAction(action === 'Sign Up' ? 'Login' : 'Sign Up')}
                      className="text-indigo-500 hover:text-indigo-600 font-semibold"
                  >
                    {action === 'Sign Up' ? 'Login' : 'Sign Up'}
                  </button>
                </p>
              </div>
            </form>
          </div>
        </div>
      </div>
  );
}