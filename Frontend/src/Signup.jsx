import { useState } from 'react';

const SignUp = () => {
  const [action, setAction] = useState("Login"); // Toggles between Login and Sign Up
  const [formData, setFormData] = useState({
    username: '',
    name: '',
    email: '',
    password: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name === 'name') {

      const regex = /^[A-Za-z]*$/;
      if (!regex.test(value)) {
        return;
      }
    }

    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (action === "Sign Up") {
      console.log("Signing Up:", formData);

    } else {
      console.log("Logging In:", formData);

    }
  };

  const handleForgotPassword = () => {
    console.log("Forgot password for email:", formData.email);
    // Add forgot password logic here
  };

  return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="w-full max-w-md p-8 space-y-6 bg-white rounded-lg shadow-md">
          <h2 className="text-2xl font-bold text-center text-gray-800">{action}</h2>

          <form className="space-y-4" onSubmit={handleSubmit}>
            {action === "Sign Up" && (
                <div>
                  <label htmlFor="name" className="text-left block text-sm font-medium text-gray-700">
                    Name
                  </label>
                  <input
                      type="text"
                      name="name"
                      id="name"
                      value={formData.name}
                      onChange={handleChange}
                      className="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      required
                  />
                </div>
            )}

            {action === "Sign Up" && (
                <div>
                  <label htmlFor="email" className="text-left block text-sm font-medium text-gray-700">
                    Email
                  </label>
                  <input
                      type="email"
                      name="email"
                      id="email"
                      value={formData.email}
                      onChange={handleChange}
                      className="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      required
                  />
                </div>
            )}

            <div>
              <label htmlFor="username" className="text-left block text-sm font-medium text-gray-700">
                Username
              </label>
              <input
                  type="text"
                  name="username"
                  id="username"
                  value={formData.username}
                  onChange={handleChange}
                  className="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  required
              />
            </div>

            <div>
              <label htmlFor="password" className="text-left block text-sm font-medium text-gray-700">
                Password
              </label>
              <input
                  type="password"
                  name="password"
                  id="password"
                  value={formData.password}
                  onChange={handleChange}
                  className="w-full px-3 py-2 mt-1 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  required
              />
            </div>

            <button
                type="submit"
                className="w-full px-4 py-2 font-medium text-white bg-indigo-600 rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              {action === "Sign Up" ? "Sign Up" : "Login"}
            </button>
          </form>

          {action === "Login" && (
              <div className="text-right mt-2">
                <button
                    onClick={handleForgotPassword}
                    className="text-sm text-indigo-600 hover:text-indigo-800 focus:outline-none"
                >
                  Forgot Password?
                </button>
              </div>
          )}

          <div className="text-center mt-4">
            {action === "Login" ? (
                <p>
                   Don't have an account?{' '}
                  <button
                      className="text-indigo-600 hover:text-indigo-800"
                      onClick={() => setAction("Sign Up")}
                  >
                    Sign Up
                  </button>
                </p>
            ) : (
                <p>
                  Already have an account?{' '}
                  <button
                      className="text-indigo-600 hover:text-indigo-800"
                      onClick={() => setAction("Login")}
                  >
                    Login
                  </button>
                </p>
            )}
          </div>
        </div>
      </div>
  );
};

export default SignUp;

