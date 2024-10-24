import React, { useState, useContext, useEffect } from "react";
import { useForm } from "react-hook-form";
import { ENDPOINTS } from "../utils/Constants";
import ApiCallingContext from "../context/ApiCallingContext";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css"; // Import the CSS for the toast notifications
import { HttpStatusCode } from "axios";
import Button from "../components/Button";
// import { frontEndRoutes } from "../utils/FrontendRoutes";
function ForgotPassword() {
  const { register, handleSubmit } = useForm();
  const { postRequest } = useContext(ApiCallingContext);
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const validate = (formData) => {
    let errors = {};
      if (!formData.email?.trim()) {  // Using optional chaining
        errors.email = "Email is required";
        toast.error("Email is required", {
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
      } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
        errors.email = "Email is invalid";
        toast.error("Email is invalid", {
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
  const onSubmit = async (data) => {

  
    try {
      //somtimes server request consume time so declare loading untill all the code is not execute completly
      setIsLoading(true);

      const response = await postRequest(
        ENDPOINTS.FORGET_PASSWORD,
        false,
        data
      );
      const result = response.data;
      console.log(
        "hear is result",
        result,
        "and response status",
        response.status,
        "and hTTP status",
        HttpStatusCode.Created
      );
      if (response.status == HttpStatusCode.Ok) {
        console.log("inside response if ", result.data.token);
        toast.success("check varification link on email", {
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
      } else {
        toast.error("wrong details", {
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
    } catch (error) {
      console.error("Registration failed:", error);
      toast.error("Registration failed", {
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
    } finally {
      setIsLoading(false);
    }
  };
  return (
    <div class="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
      <ToastContainer />
      <form
        class="relative py-3 sm:max-w-xl sm:mx-auto"
        onSubmit={handleSubmit(onSubmit)}
      >
        <div class="absolute inset-0 bg-gradient-to-r from-blue-300 to-blue-600 shadow-lg transform -skew-y-6 sm:skew-y-0 sm:-rotate-6 sm:rounded-3xl"></div>
        <div class="relative px-4 py-10 bg-white shadow-lg sm:rounded-3xl sm:p-20">
          <div class="max-w-md mx-auto">
            <div>
              <h1 class="text-2xl font-semibold">Forgot Password</h1>
            </div>
            <div class="divide-y divide-gray-200">
              <div class="py-8 text-base leading-6 space-y-4 text-gray-700 sm:text-lg sm:leading-7">
                <div class="relative">
                  <input
                    id="email"
                    name="email"
                    type="email"
                    class="peer placeholder-transparent h-10 w-full border-b-2 border-gray-300 text-gray-900 focus:outline-none focus:borer-rose-600"
                    placeholder="email"
                    aria-required="true"
                    {...register("email", { required: true })}
                  />
                  <label
                  for="email"
                  class="absolute left-0 -top-3.5 text-gray-600 text-sm peer-placeholder-shown:text-base peer-placeholder-shown:text-gray-440 peer-placeholder-shown:top-2 transition-all peer-focus:-top-3.5 peer-focus:text-gray-600 peer-focus:text-sm"
                >
                  user@gmail.com
                </label>
                </div>
                

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
                    submit
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

export default ForgotPassword;
