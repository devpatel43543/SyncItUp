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
const createGroup = React.lazy(()=>import("./pages/CreateGroup.jsx"))
const groupExpenseDashboard = React.lazy(()=>import("./pages/GroupDashboard.jsx"))
const AddNewMember = React.lazy(()=>import("./pages/AddNewMember.jsx"))
const RemoveMember = React.lazy(()=>import("./pages/RemoveMember.jsx"))
const Requests = React.lazy(()=>import("./pages/Requests.jsx"))
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
            <Route
                path={frontEndRoutes.createGroup}
                element = {<IncludeNavbar Component={createGroup}/>}
            />
            <Route
                path={frontEndRoutes.groupExpenseDashboard}
                element = {<IncludeNavbar Component={groupExpenseDashboard}/>}
            />
            <Route
                path={`${frontEndRoutes.add_new_member}/:groupId`}
                element = {<IncludeNavbar Component={AddNewMember}/>}
            />
            <Route
                path={`${frontEndRoutes.remove_member}/:groupId`}
                element={<IncludeNavbar Component={RemoveMember}/>}
            />
            <Route
                path={`${frontEndRoutes.requests}`}
                element={<IncludeNavbar Component={Requests}/>}
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
