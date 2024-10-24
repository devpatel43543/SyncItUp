import React, { useEffect, useState } from 'react';
import { useContext } from "react";
import { toast, ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { frontEndRoutes } from '../utils/FrontendRoutes';
import LoadAnimation from '../components/LoadAnimation';

function Loading() {
    const time = new Date();
    const { loggedInUserEmail, loading } = useContext(AuthContext);
    
    const navigate = useNavigate();
    const [status, setStatus] = useState(null);

    useEffect(() => {
        if (loggedInUserEmail) {
            setStatus(true);
            console.log("Logged in user:", loggedInUserEmail, time.toLocaleString());
        }

        if (!loading && loggedInUserEmail == null) {
            toast.error("You have to log in", {
                position: "top-right",
                autoClose: 3000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
                theme: "light",
                transition: Bounce,
            });

            setTimeout(() => {
                navigate(frontEndRoutes.login);
            }, 3000);
        }      
    }, [loading, loggedInUserEmail, navigate, time]);

    return (
        <>
            <ToastContainer />
            {status ? (
                <>
                    {/* Navigate on successful login */}
                    {navigate(frontEndRoutes.dashboard)}
                </>
            ) : (
                <>
                    {console.log("loading")}
                    <LoadAnimation />
                </>
            )}
        </>
    );
}

export default Loading;
