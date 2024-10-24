import React, { Suspense, useState } from "react";
import {
    Route,
    Routes,
} from "react-router-dom";

import { frontEndRoutes } from "./utils/FrontendRoutes.js";
import LoadAnimation from "./components/LoadAnimation.jsx";
import Navbar from "./components/Navbar.jsx";

const login = React.lazy(() => import("./pages/Login"));
const register = React.lazy(() => import("./pages/Register"));
const verifyOtp = React.lazy(() => import("./pages/VerifyOtp"));
const loadingAnimation = React.lazy(() => import("./components/LoadAnimation"));
const dashboard = React.lazy(() => import("./pages/Dashboard"));
const forgotPassword = React.lazy(()=>import("./pages/ForgotPassword"))
const resetPassword= React.lazy(()=>import("./pages/ResetPassword.jsx"))
function App() {
    return (
        <Routes>
            
            <Route
                path={frontEndRoutes.login}
                element={<ExcludeNavbar Component={login} />}
            />
            <Route
                path={frontEndRoutes.register}
                element={<ExcludeNavbar Component={register} />}
            />
            <Route
                path={frontEndRoutes.verifyOtp}
                element={<ExcludeNavbar Component={verifyOtp} />}
            />
            <Route
                path={frontEndRoutes.dashboard}
                element={<IncludeNavbar Component={dashboard} />}
            />
            <Route
                path={frontEndRoutes.forgotPassword}
                element={<ExcludeNavbar Component={forgotPassword} />}
            />
            <Route
                path={frontEndRoutes.resetPassword}
                element={<ExcludeNavbar Component={resetPassword} />}
            />
        </Routes>
    );
}

const ExcludeNavbar = ({ Component }) => (
    <Suspense fallback={<LoadAnimation />}>
        <Component />
    </Suspense>
);
const IncludeNavbar = ({ Component }) => (
    <>
        <Navbar />
        <Suspense fallback={<LoadAnimation />}>
            <Component />
        </Suspense>
    </>
);

export default App;
