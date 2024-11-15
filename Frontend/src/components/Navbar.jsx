import { Banknote, LogOut } from "lucide-react";
import React, { useContext, useState } from "react";
import { frontEndRoutes } from "../utils/FrontendRoutes";
import { Link, useNavigate } from "react-router-dom";
import Button from "./Button";
import { toast, ToastContainer, Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css"; // Import the CSS for the toast notifications
import AuthContext from "../context/AuthContext";

function Navbar() {
  const [isLoading, setIsLoading] = useState(false);
  const { deleteAuthToken } = useContext(AuthContext);
  const navigate = useNavigate();
  const handleLogout = async () => {
    setIsLoading(true);
    try {
      deleteAuthToken();
      navigate(frontEndRoutes.login);
    } catch (err) {
      toast.error("An error occurred during logout. Please try again.", {
        position: "top-right",
        autoClose: 3000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        transition: Bounce,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <nav className="bg-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <div className="flex-shrink-0 flex items-center">
              <Banknote className="h-8 w-8 text-blue-500" />
              <span className="ml-2 text-xl font-bold text-gray-800">
                FundFusion
              </span>
            </div>
            <div className="ml-6 flex space-x-8">
              <Link
                to={frontEndRoutes.dashboard}
                className="border-blue-500 text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Personal Expense
              </Link>
              <Link
                to={frontEndRoutes.groupExpenseDashboard}
                className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Group Expense
              </Link>
              <Link
                to={frontEndRoutes.requests}
                className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Join Requests
              </Link>
              <Link
                href="/task-assignment"
                className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium"
              >
                Task Assignment
              </Link>
            </div>
          </div>
          <div className="flex items-center">
            <div className="flex items-center">
              <div
                className={`bg-red-500 text-white hover:bg-red-600 rounded-md px-2 py-1 cursor-pointer flex items-center ${
                  isLoading ? "opacity-50" : ""
                }`}
                onClick={handleLogout}
                disabled={isLoading}
                role="button"
                tabIndex={0}
                aria-disabled={isLoading}
              >
                <LogOut className="h-5 w-5 mr-2" />
                <span>{isLoading ? "Logging out..." : "Logout"}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
