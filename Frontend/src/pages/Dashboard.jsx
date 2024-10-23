import React, { useContext, useEffect, useState } from 'react';
import { PlusIcon, HomeIcon, ChartPieIcon, UsersIcon, Cog6ToothIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import { Menu } from '@headlessui/react';
import { AUTH_TOKEN, ENDPOINTS, BASE_URL } from '../utils/Constants';
import axios from 'axios';

export default function Dashboard() {
    const [expenses, setExpenses] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingExpense, setEditingExpense] = useState(null);
    const [newExpense, setNewExpense] = useState({
        date: "",
        amount: "",
        category: "",
        description: "",
    });

    const axiosConfig = {
        baseURL: BASE_URL,
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
        },
    };

    const axiosInstance = axios.create(axiosConfig);
    const axiosInstanceWithAuth = axios.create(axiosConfig);

    axiosInstanceWithAuth.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem(AUTH_TOKEN);
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => {
            return Promise.reject(error);
        }
    );

    const getAllExpense = async () => {
        try {
            const response = await getRequest(ENDPOINTS.ALL_PERSONAL_EXPENSE, true);
            if (response.data.result === "SUCCESS" && Array.isArray(response.data.data)) {
                setExpenses(response.data.data);
            }
        } catch (error) {
            console.error("Error fetching expenses: ", error);
        }
    };
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

    const updateRequest = async (endpoint, useAuthToken = false, data) => {
        try {
            const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;
            return await instance.put(endpoint, data);
        } catch (error) {
            console.error(`Error in PUT request to ${endpoint}:`, error);
            throw error;
        }
    };
    const deleteRequest = async (endpoint, useAuthToken = false, params = {}) => {
        try {
            const instance = useAuthToken ? axiosInstanceWithAuth : axiosInstance;
            
            // Pass the params to the delete request
            return await instance.delete(endpoint, { params });
        } catch (error) {
            console.error(`Error in DELETE request to ${endpoint}:`, error);
            throw error;
        }
    };
    

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewExpense({ ...newExpense, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const expenseData = {
            txnDate: newExpense.date,
            expense: parseFloat(newExpense.amount), // Ensure this is a number
            categoryId: parseInt(newExpense.category), // Convert to integer
            txnDesc: newExpense.description,
        };

        if (editingExpense) {
            // Update existing expense
            try {
                await updateRequest(ENDPOINTS.UPDATE_EXPENSE, true, { ...expenseData, txnId: editingExpense.txnId });
                setExpenses(expenses.map(expense => (expense.txnId === editingExpense.txnId ? { ...expenseData, txnId: expense.txnId } : expense)));
                setEditingExpense(null);
            } catch (error) {
                console.error("Error updating expense:", error);
            }
        } else {
            // Add new expense
            try {
                const response = await postRequest(ENDPOINTS.CREATE_PERSONAL_EXPENSE, true, expenseData);
                if (response.data.result === "SUCCESS") {
                    setExpenses([...expenses, { ...expenseData, txnId: response.data.data.txnId }]); // Assuming API returns txnId
                }
            } catch (error) {
                console.error("Error adding expense:", error);
            }
        }
        setNewExpense({ date: "", amount: "", category: "", description: "" });
        setShowModal(false);
    };

    const handleEdit = (expense) => {
        setEditingExpense(expense);
        setNewExpense({
            date: expense.txnDate,
            amount: expense.expense.toString(),
            category: expense.categoryId.toString(),
            description: expense.txnDesc,
        });
        setShowModal(true);
    };

    // const handleDelete = async (txnId) => {
    //     try {
    //         await axiosInstanceWithAuth.delete(`${ENDPOINTS.DELETE_EXPENSE}/${txnId}`); // Assuming this is the correct delete endpoint
    //         setExpenses(expenses.filter(expense => expense.txnId !== txnId));
    //     } catch (error) {
    //         console.error("Error deleting expense:", error);
    //     }
    // };

    const handleDelete = async (txnId) => {
        try {
            // Call the deleteRequest function, passing the DELETE_EXPENSE endpoint with the txnId as a parameter
            await deleteRequest(ENDPOINTS.DELETE_EXPENSE, true, { txnId });
            
            // Update the state to remove the deleted expense from the UI
            setExpenses(expenses.filter(expense => expense.txnId !== txnId));
        } catch (error) {
            console.error("Error deleting expense:", error);
        }
    };  
    useEffect(() => {
        getAllExpense();
    }, []);

    return (
        <div className="min-h-screen bg-gray-100">
                    <nav className="bg-white shadow-md">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16">
                        <div className="flex">
                            <div className="flex-shrink-0 flex items-center">
                                <span className="text-2xl font-bold text-gray-800">FundFusion</span>
                            </div>
                            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
                                <a href="#" className="border-indigo-500 text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <HomeIcon className="mr-1 h-5 w-5" />
                                    Dashboard
                                </a>
                                <a href="#" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <ChartPieIcon className="mr-1 h-5 w-5" />
                                    Charts
                                </a>
                                <a href="#" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <UsersIcon className="mr-1 h-5 w-5" />
                                    Friends
                                </a>
                                <a href="#" className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                    <Cog6ToothIcon className="mr-1 h-5 w-5" />
                                    Settings
                                </a>
                            </div>
                        </div>
                        {/* <div className="flex items-center">
                            <Menu as="div" className="relative ml-3">
                                <div>
                                    <Menu.Button className="flex text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                        <span className="sr-only">Open user menu</span>
                                        <span className="text-gray-800 font-medium">user@example.com</span>
                                    </Menu.Button>
                                </div>
                            </Menu>
                        </div> */}
                    </div>
                </div>
            </nav>
            <main className="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
                <div className="px-4 py-6 sm:px-0">
                    <h1 className="text-3xl font-bold text-gray-900 mb-6">Recent Expenses</h1>
                    <ul className="space-y-4">
                        {expenses.map((expense) => (
                            <li key={expense.txnId} className="py-4 px-4 bg-white shadow-sm rounded-lg mb-4 hover:shadow-md transition-shadow duration-200">
                                <div className="flex items-center justify-between">
                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <h3 className="text-lg font-semibold text-gray-900 truncate">{expense.categoryId}</h3>
                                            <span className="text-2xl font-bold text-indigo-600">${expense.expense}</span>
                                        </div>
                                        <p className="text-sm text-gray-500 truncate">{expense.txnDesc}</p>
                                        <p className="text-xs text-gray-400 mt-1">{new Date(expense.txnDate).toLocaleDateString('en-US', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })}</p>
                                    </div>
                                    <div className="flex items-center space-x-2 ml-4">
                                        <button onClick={() => handleEdit(expense)} className="text-indigo-600 hover:text-indigo-900 p-1 rounded-full hover:bg-indigo-100 transition-colors duration-200">
                                            <PencilIcon className="h-5 w-5" />
                                        </button>
                                        <button onClick={() => handleDelete(expense.txnId)} className="text-red-600 hover:text-red-900 p-1 rounded-full hover:bg-red-100 transition-colors duration-200">
                                            <TrashIcon className="h-5 w-5" />
                                        </button>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </main>
            <button onClick={() => setShowModal(true)} className="fixed bottom-4 right-4 bg-indigo-600 text-white rounded-full p-3 shadow-lg hover:bg-indigo-700 transition-colors duration-200">
                <PlusIcon className="h-6 w-6" />
            </button>

            {/* Modal for adding/updating expenses */}
            {showModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-white rounded-lg shadow-lg p-6">
                        <h2 className="text-xl font-semibold mb-4">{editingExpense ? 'Edit Expense' : 'Add Expense'}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="date">Date</label>
                                <input
                                    type="date"
                                    id="date"
                                    name="date"
                                    value={newExpense.date}
                                    onChange={handleInputChange}
                                    required
                                    className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                                />
                            </div>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="amount">Amount</label>
                                <input
                                    type="number"
                                    id="amount"
                                    name="amount"
                                    value={newExpense.amount}
                                    onChange={handleInputChange}
                                    required
                                    className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                                />
                            </div>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="category">Category</label>
                                <input
                                    type="text"
                                    id="category"
                                    name="category"
                                    value={newExpense.category}
                                    onChange={handleInputChange}
                                    required
                                    className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                                />
                            </div>
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="description">Description</label>
                                <textarea
                                    id="description"
                                    name="description"
                                    value={newExpense.description}
                                    onChange={handleInputChange}
                                    required
                                    className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                                />
                            </div>
                            <div className="flex justify-end">
                                <button type="button" onClick={() => setShowModal(false)} className="mr-2 bg-gray-300 text-gray-800 rounded-lg px-4 py-2 hover:bg-gray-400">Cancel</button>
                                <button type="submit" className="bg-indigo-600 text-white rounded-lg px-4 py-2 hover:bg-indigo-700">{editingExpense ? 'Update' : 'Add'} Expense</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
