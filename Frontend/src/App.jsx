import React from "react";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import './App.css'
import Signup from './Signup';
import Dashboard from "./Dashboard.jsx";
import Modal from "./Modal.jsx";

function App() {

  return (
      <Router>
          <Routes>
              <Route path="/" element={<Signup />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/addexpense" element={<Modal />} />
          </Routes>
      </Router>
  )
}

export default App
