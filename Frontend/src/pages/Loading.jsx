import React, { useEffect, useState } from 'react'
import { useContext } from "react";
import { toast, ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { frontEndRoutes } from '../utils/FrontendRoutes';
import LoadAnimation from '../components/LoadAnimation';
function Loading() {
    const time = new Date()
    const { loggedInUserEmail ,loading} = useContext(AuthContext);
    if(loggedInUserEmail == null){
        console.log("i am from line 14 loggedInUserEmail ",loggedInUserEmail," ",time.toLocaleString())
      }
      const navigate = useNavigate();
      const [status,setStatus]= useState(null)
      useEffect(()=>{
        if(loggedInUserEmail){
          setStatus(true)
          console.log("i am from line 22 loggedInUserEmail ",loggedInUserEmail," ",time.toLocaleString())
        }
        if(loading == false && loggedInUserEmail == null){
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
      },[loading]  return (
    <>
      <ToastContainer />
      {status ? (
        <>
          {navigate(frontEndRoutes.dashboard)}
        </>
      ) : (
        <>
          {console.log("loading")}
          <LoadAnimation />
        </>
      )}
    </>  )
export default Loading

// import React, { useEffect, useState, useContext } from 'react';
// import { toast, ToastContainer, Bounce } from "react-toastify";
// import "react-toastify/dist/ReactToastify.css";
// import AuthContext from '../context/AuthContext';
// import { useNavigate } from 'react-router-dom';
// import { frontEndRoutes } from '../utils/FrontendRoutes';
// import LoadAnimation from '../components/LoadAnimation';

// function Loading() {
//     const { loggedInUserEmail, loading } = useContext(AuthContext);
//     const navigate = useNavigate();
//     const [status, setStatus] = useState(false);

//     useEffect(() => {
//         if (!loading && loggedInUserEmail === null) {
//             // If not logged in, show toast and redirect to login page
//             toast.error("You have to log in", {
//                 position: "top-right",
//                 autoClose: 3000,
//                 hideProgressBar: false,
//                 closeOnClick: true,
//                 pauseOnHover: true,
//                 draggable: true,
//                 progress: undefined,
//                 theme: "light",
//                 transition: Bounce,
//             });

//             // Redirect after a delay to allow toast to display
//             setTimeout(() => {
//                 navigate(frontEndRoutes.login);
//             }, 3000);
//         } else if (loggedInUserEmail) {
//             setStatus(true); // User is logged in, set status to true
//         }
//     }, [loading, loggedInUserEmail, navigate]);

//     // Handle redirect to dashboard if logged in
//     useEffect(() => {
//         if (status) {
//             navigate(frontEndRoutes.dashboard); // Redirect to dashboard
//         }
//     }, [status, navigate]);

//     return (
//         <>
//             <ToastContainer />
//             {!status && <LoadAnimation />} {/* Show loading animation if not logged in */}
//         </>
//     );
// }

// export default Loading;
