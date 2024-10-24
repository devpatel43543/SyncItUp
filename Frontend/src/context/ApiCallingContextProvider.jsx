import React, { useContext, useEffect } from "react";
import ApiCallingContext from "./ApiCallingContext";
import { AUTH_TOKEN } from "../utils/Constants";
import axios from "axios";
import { data } from "autoprefixer";
//import AuthContext from "./AuthContext";

const BASE_URL = import.meta.env.VITE_BASE_URL
console.log("Base URL: "+BASE_URL)
const ApiCallingContextProvider = (props) => {

  const axiosConfig = {
    baseURL: BASE_URL,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
  };

  const axiosInstance = axios.create(axiosConfig);
  const axiosInstanceWithAuth = axios.create(axiosConfig);

  //use to add authentication token in configuration

  
  axiosInstanceWithAuth.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem(AUTH_TOKEN);

      if (token) {
        console.log("called");
        config.headers.Authorization = `Bearer ${token}`;
        console.log(config.headers.Authorization);
      }
      console.log("line 35 ", config);
      return config;
    },
    (error) => {
      console.error("Request interceptor error:", error);
      return Promise.reject(error);
    }
  );

  const getRequest = async (endpoint, useAuthToken = false) => {
    console.log("getRequest is called")
    try {
      const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;
      return await instance.get(endpoint);
    } catch (error) {
      console.error(`Error in GET request to ${endpoint}:`, error);
      throw error;
    }
  };

  const postRequest = async (endpoint, useAuthToken = false, data) => {
    try {
      const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;

      return await instance.post(endpoint, data);
    } catch (error) {
      console.error(`Error in POST request to ${endpoint}:`, error);
      throw error;
    }
  };

  const putRequest = async (
    endpoint,
    useAuthToken = false,
    data,
    params = {}
  ) => {
    try {
      const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;
      return await instance.put(endpoint, data, { params });
    } catch (error) {
      console.error(`Error in PUT request to ${endpoint}:`, error);
      throw error;
    }
  };

  const deleteRequest = async (endpoint, useAuthToken = false, params = {}) => {
    try {
      const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;
      return await instance.delete(endpoint, { params });
    } catch (error) {
      console.error(`Error in DELETE request to ${endpoint}:`, error);
      throw error;
    }
  };
  return (
    <ApiCallingContext.Provider
      value={{ getRequest, postRequest, putRequest, deleteRequest }}
    >
      {props.children}
    </ApiCallingContext.Provider>
  );
};
export default ApiCallingContextProvider;
