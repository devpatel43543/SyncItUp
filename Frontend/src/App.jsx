// import React from "react";
// import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
// import './App.css'
// import Signup from './Signup';
// import Dashboard from "./Dashboard.jsx";
// import Modal from "./Modal.jsx";
//
// function App() {
//
//   return (
//       <Router>
//           <Routes>
//               <Route path="/" element={<Signup />} />
//               <Route path="/dashboard" element={<Dashboard />} />
//               <Route path="/addexpense" element={<Modal />} />
//           </Routes>
//       </Router>
//   )
// }
//
// export default App

// App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import SignUp from './pages/Signup.jsx';  // The login/sign-up page
import Dashboard from './pages/Dashboard.jsx';  // The dashboard page
import { AUTH_TOKEN } from "./utils/Constants.js";

const App = () => {
    const [isAuthenticated, setIsAuthenticated] = React.useState(localStorage.getItem(AUTH_TOKEN));

    const handleLoginSuccess = (token) => {
        localStorage.setItem(AUTH_TOKEN, token);
        setIsAuthenticated(true);  // Set authentication state to true after successful login
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
                <Route
                    path="/Dashboard"
                    element={isAuthenticated ? <Dashboard /> : <Navigate to="/" />}
                />
            </Routes>
        </Router>
    );
};

export default App;

