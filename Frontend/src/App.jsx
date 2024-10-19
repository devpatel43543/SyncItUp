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
import SignUp from './Signup.jsx';  // The login/sign-up component
import Dashboard from './Dashboard';  // The dashboard component

const App = () => {
    const [isAuthenticated, setIsAuthenticated] = React.useState(false);

    const handleLoginSuccess = () => {
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

