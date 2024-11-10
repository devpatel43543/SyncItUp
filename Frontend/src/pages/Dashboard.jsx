import React, { useContext, useEffect, useState } from 'react';
import { PlusIcon, HomeIcon, ChartPieIcon, UsersIcon, Cog6ToothIcon, PencilIcon, TrashIcon,TagIcon } from '@heroicons/react/24/outline';
import { ENDPOINTS } from '../utils/Constants';
import ApiCallingContext from '../context/ApiCallingContext';
import Navbar from '../components/Navbar';
import { frontEndRoutes } from '../utils/FrontendRoutes';
import AuthContext from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import AddCategory from '../components/AddCategory';
export default function Dashboard() {
    const { getRequest, postRequest, putRequest, deleteRequest } = useContext(ApiCallingContext);
    const [expenses, setExpenses] = useState([]);
    const [categories, setCategories] = useState([]); 
    const [showModal, setShowModal] = useState(false);
    const [showAddCategory, setShowAddCategory] = useState(false);
    const [editingExpense, setEditingExpense] = useState(null);
    const [newExpense, setNewExpense] = useState({
        date: "",
        amount: "",
        category: "", 
        description: "",
    });
    const navigate = useNavigate()
    const {loggedInUserEmail} = useContext(AuthContext)

    useEffect(()=>{
        if(loggedInUserEmail){
            navigate(frontEndRoutes.dashboard)
        }else{
            navigate(frontEndRoutes.login)
        }
    },[loggedInUserEmail])
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

    const getAllCategories = async () => {
        try {
            const response = await getRequest(ENDPOINTS.ALL_CATEGORY, true);
            if (response.data.result === "SUCCESS" && Array.isArray(response.data.data)) {
                setCategories(response.data.data); // Set the fetched categories
            }
        } catch (error) {
            console.log(error);
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
            expense: parseFloat(newExpense.amount),
            categoryId: parseInt(newExpense.category), // Make sure category is an integer
            txnDesc: newExpense.description,
        };

        if (editingExpense) {
            try {
                const response = await putRequest(ENDPOINTS.UPDATE_EXPENSE, true, { ...expenseData, txnId: editingExpense.txnId });

                setExpenses(expenses.map(expense => (expense.txnId === editingExpense.txnId ? { ...response.data.data, txnId: expense.txnId } : expense)));
                setEditingExpense(null);
            } catch (error) {
                console.error("Error updating expense:", error);
            }
        } else {
            try {
                const response = await postRequest(ENDPOINTS.CREATE_PERSONAL_EXPENSE, true, expenseData);
                if (response.data.result === "SUCCESS") {
                    setExpenses([...expenses, { ...response.data.data, txnId: response.data.data.txnId }]);
                    // Fetch categories after adding expense
                    getAllCategories();
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
            category: expense.categoryId?.toString() || "",
            description: expense.txnDesc,
        });
        setShowModal(true);
    };

    const handleDelete = async (txnId) => {
        try {
            await deleteRequest(ENDPOINTS.DELETE_EXPENSE, true, { id: txnId });
            setExpenses(expenses.filter(expense => expense.txnId !== txnId));
        } catch (error) {
            console.error("Error deleting expense:", error);
        }
    };


    useEffect(() => {
        getAllExpense();
        getAllCategories();
    }, []);

    return (
        <div className="min-h-screen bg-gray-100">
            <main className="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
                <div className="px-4 py-6 sm:px-0">
                    <h1 className="text-3xl font-bold text-gray-900 mb-6">Recent Expenses</h1>
                    <ul className="space-y-4">
                        {expenses.map((expense) => (
                            <li key={expense.txnId} className="py-4 px-4 bg-white shadow-sm rounded-lg mb-4 hover:shadow-md transition-shadow duration-200">
                                <div className="flex items-center justify-between">
                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <h3 className="text-lg font-semibold text-gray-900 truncate">{expense.category}</h3>
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
            <button onClick={() => setShowAddCategory(true)} className="fixed bottom-20 right-4 bg-indigo-600 text-white rounded-full p-3 shadow-lg hover:bg-indigo-700 transition-colors duration-200">
                <TagIcon className="h-6 w-6" />
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
                                <select
                                    id="category"
                                    name="category"
                                    value={newExpense.category}
                                    onChange={handleInputChange}
                                    required
                                    className="mt-1 block w-full border border-gray-300 rounded-lg p-2"
                                >
                                    <option value="">Select a category</option>
                                    {categories.map((category) => (
                                        <option key={category.categoryId} value={category.categoryId}>
                                            {category.category}
                                        </option>
                                    ))}
                                </select>
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
                                <button type="submit" className="bg-indigo-600 text-white rounded-lg px-4 py-2 hover:bg-indigo-700">{editingExpense ? 'Update' : 'Add'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {showAddCategory && <AddCategory categories={categories} />}


        </div>
    );
}
