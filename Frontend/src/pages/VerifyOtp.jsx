import React, { useState,useContext,useEffect } from "react";
import { useForm } from "react-hook-form";
import { ENDPOINTS } from "../utils/Constants";
import AuthContext from "../context/AuthContext";
import ApiCallingContext from "../context/ApiCallingContext";
import {  useNavigate } from "react-router-dom";
import { toast, ToastContainer, Bounce } from "react-toastify";
import { HttpStatusCode } from "axios";
import Button from "../components/Button";
import { frontEndRoutes } from "../utils/FrontendRoutes";
import "react-toastify/dist/ReactToastify.css"; // Import the CSS for the toast notifications

function VerifyOtp() {
    const { register, handleSubmit } = useForm();
    const {loggedInUserEmail,storeAuthToken} = useContext(AuthContext)
    const {postRequest} = useContext(ApiCallingContext)
    const navigate = useNavigate()

    const [isLoading,setIsLoading] = useState(false);
    useEffect(()=>{
        if(loggedInUserEmail){
            navigate(frontEndRoutes.dashboard)
        }
    },[loggedInUserEmail])

    const onSubmit = async(data)=>{
  
        console.log(data)
        try {
            //somtimes server request consume time so declare loading untill all the code is not execute completly
            setIsLoading(true); 

            const response = await postRequest(ENDPOINTS.VERIFY_OTP,false,data)
            const result = response.data
            console.log("hear is result",result,"and response status",response.status , "and hTTP status",HttpStatusCode.Created)
            if(result.data.token != null){
              toast.success("Email verification completed!", {
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
                console.log("inside response if ",result.data.token)
                storeAuthToken(result.data.token)

            }

        } catch (error) {
            console.error("wrong otp please try again:", error);
            toast.error("wrong otp please try again", {
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
        }finally{
            setIsLoading(false)
        }
    }
    
    const handleResendOtp = async () => {
      try {
          const response = await postRequest(ENDPOINTS.RESEND_OTP, false);
          
          if (response.status === HttpStatusCode.Created) {
              toast.success("OTP has been resent successfully!", {
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
          } 
        }catch (error) {
          toast.error("An error occurred while resending the OTP.", {
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
        }
      } 
  return (
    <div class="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
     <ToastContainer/>
      <form
        class="relative py-3 sm:max-w-xl sm:mx-auto"
        onSubmit={handleSubmit(onSubmit)}
      >
        <div class="absolute inset-0 bg-gradient-to-r from-blue-300 to-blue-600 shadow-lg transform -skew-y-6 sm:skew-y-0 sm:-rotate-6 sm:rounded-3xl"></div>
        <div class="relative px-4 py-10 bg-white shadow-lg sm:rounded-3xl sm:p-20">
          <div class="max-w-md mx-auto">
            <div>
              <h1 class="text-2xl font-semibold">Verify OTP</h1>
            </div>
            <div class="divide-y divide-gray-200">
              <div class="py-8 text-base leading-6 space-y-4 text-gray-700 sm:text-lg sm:leading-7">
                <div class="relative">
                  <input
                    id="text"
                    name="text"
                    type="text"
                    class="peer placeholder-transparent h-10 w-full border-b-2 border-gray-300 text-gray-900 focus:outline-none focus:borer-rose-600"
                    placeholder="enter otp"
                    aria-required="true"
                    {...register("otp", { required: true })}
                  />
                  <label
                    for="email"
                    class="absolute left-0 -top-3.5 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 peer-placeholder-shown:top-2 transition-all peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
                  >
                    otp verification
                  </label>
                </div>

                <button class="text-blue-500 text-xs underline" onClick={handleResendOtp}>
                  Resend Otp
                </button>

                <div class="relative">
                  {/* <button class="bg-blue-500 text-white rounded-md px-2 py-1" type="submit">
                              
                          </button> */}
                  <Button
                    type="submit"
                    bgColor="bg-blue-500"
                    textColor="text-white"
                    hover="shadow-blue-500/40"
                    isSubmitting={isLoading}
                    className="rounded-md px-2 py-1"
                  >
                    Verify
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
    </div>
  );
}

export default VerifyOtp;