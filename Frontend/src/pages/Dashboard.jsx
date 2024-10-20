import React, { useState } from 'react';
import { PlusIcon, HomeIcon, ChartPieIcon, UsersIcon, Cog6ToothIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import { Menu } from '@headlessui/react';

export default function Dashboard() {
    const [expenses, setExpenses] = useState([
        { id: 1, date: "2023-04-15", amount: 50, category: "Food", description: "Dinner with friends" },
        { id: 2, date: "2023-04-16", amount: 30, category: "Transportation", description: "Uber ride" },
    ]);

    const [showModal, setShowModal] = useState(false);
    const [editingExpense, setEditingExpense] = useState(null);
    const [newExpense, setNewExpense] = useState({
        date: "",
        amount: "",
        category: "",
        description: "",
    });

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewExpense({ ...newExpense, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (editingExpense) {
            setExpenses(expenses.map(expense =>
                expense.id === editingExpense.id ? { ...newExpense, id: expense.id, amount: parseFloat(newExpense.amount) } : expense
            ));
            setEditingExpense(null);
        } else {
            const expense = {
                id: Date.now(), // Use timestamp as a unique id
                ...newExpense,
                amount: parseFloat(newExpense.amount),
            };
            setExpenses([...expenses, expense]);
        }
        setNewExpense({ date: "", amount: "", category: "", description: "" });
        setShowModal(false);
    };

    const handleEdit = (expense) => {
        setEditingExpense(expense);
        setNewExpense({
            date: expense.date,
            amount: expense.amount.toString(),
            category: expense.category,
            description: expense.description,
        });
        setShowModal(true);
    };

    const handleDelete = (id) => {
        setExpenses(expenses.filter(expense => expense.id !== id));
    };

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
                        <div className="flex items-center">
                            <Menu as="div" className="relative ml-3">
                                <div>
                                    <Menu.Button className="flex text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                        <span className="sr-only">Open user menu</span>
                                        <span className="text-gray-800 font-medium">user@example.com</span>
                                    </Menu.Button>
                                </div>
                            </Menu>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
                <div className="px-4 py-6 sm:px-0">
                    <h1 className="text-3xl font-bold text-gray-900 mb-6">Recent Expenses</h1>
                    <ul className="space-y-4">
                        {expenses.map((expense) => (
                            <li key={expense.id} className="py-4 px-4 bg-white shadow-sm rounded-lg mb-4 hover:shadow-md transition-shadow duration-200">
                                <div className="flex items-center justify-between">
                                    <div className="flex-1 min-w-0">
                                        <div className="flex items-center justify-between mb-1">
                                            <h3 className="text-lg font-semibold text-gray-900 truncate">{expense.category}</h3>
                                            <span className="text-2xl font-bold text-indigo-600">${expense.amount.toFixed(2)}</span>
                                        </div>
                                        <p className="text-sm text-gray-500 truncate">{expense.description}</p>
                                        <p className="text-xs text-gray-400 mt-1">{new Date(expense.date).toLocaleDateString('en-US', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })}</p>
                                    </div>
                                    <div className="flex items-center space-x-2 ml-4">
                                        <button onClick={() => handleEdit(expense)} className="text-indigo-600 hover:text-indigo-900 p-1 rounded-full hover:bg-indigo-100 transition-colors duration-200">
                                            <PencilIcon className="h-5 w-5" />
                                        </button>
                                        <button onClick={() => handleDelete(expense.id)} className="text-red-600 hover:text-red-900 p-1 rounded-full hover:bg-red-100 transition-colors duration-200">
                                            <TrashIcon className="h-5 w-5" />
                                        </button>
                                    </div>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>
            </main>

            <button
                onClick={() => {
                    setEditingExpense(null);
                    setNewExpense({ date: "", amount: "", category: "", description: "" });
                    setShowModal(true);
                }}
                className="fixed bottom-8 right-8 bg-indigo-600 text-white rounded-full p-4 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 shadow-lg hover:shadow-xl transition-all duration-200"
            >
                <PlusIcon className="h-6 w-6" aria-hidden="true" />
            </button>

            {showModal && (
                <div className="fixed z-10 inset-0 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
                    <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                        <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" aria-hidden="true"></div>
                        <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
                        <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
                            <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
                                <h3 className="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                                    {editingExpense ? 'Edit Expense' : 'Add New Expense'}
                                </h3>
                                <form onSubmit={handleSubmit} className="mt-4 space-y-4">
                                    <div>
                                        <label htmlFor="date" className="block text-sm font-medium text-gray-700">
                                            Date
                                        </label>
                                        <input
                                            type="date"
                                            id="date"
                                            name="date"
                                            value={newExpense.date}
                                            onChange={handleInputChange}
                                            required
                                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        />
                                    </div>
                                    <div>
                                        <label htmlFor="amount" className="block text-sm font-medium text-gray-700">
                                            Amount
                                        </label>
                                        <input
                                            type="number"
                                            id="amount"
                                            name="amount"
                                            value={newExpense.amount}
                                            onChange={handleInputChange}
                                            placeholder="0.00"
                                            step="0.01"
                                            min="0"
                                            required
                                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        />
                                    </div>
                                    <div>
                                        <label htmlFor="category" className="block text-sm font-medium text-gray-700">
                                            Category
                                        </label>
                                        <select
                                            id="category"
                                            name="category"
                                            value={newExpense.category}
                                            onChange={handleInputChange}
                                            required
                                            className="mt-1 block w-full bg-white border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        >
                                            <option value="">Select a category</option>
                                            <option value="Food">Food</option>
                                            <option value="Transportation">Transportation</option>
                                            <option value="Entertainment">Entertainment</option>
                                            <option value="Utilities">Utilities</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                                            Description
                                        </label>
                                        <input
                                            type="text"
                                            id="description"
                                            name="description"
                                            value={newExpense.description}
                                            onChange={handleInputChange}
                                            placeholder="Enter a description"
                                            className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                        />
                                    </div>
                                    <div className="mt-5 sm:mt-6 sm:grid sm:grid-cols-2 sm:gap-3 sm:grid-flow-row-dense">
                                        <button
                                            type="submit"
                                            className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:col-start-2 sm:text-sm"
                                        >
                                            {editingExpense ? 'Save Changes' : 'Add Expense'}
                                        </button>
                                        <button
                                            type="button"
                                            className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:mt-0 sm:col-start-1 sm:text-sm"
                                            onClick={() => setShowModal(false)}
                                        >
                                            Cancel
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}