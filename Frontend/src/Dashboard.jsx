import React, { useState } from 'react';
import Drawer from './Drawer';
import Modal from './Modal';

const Dashboard = () => {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [expenses, setExpenses] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedExpense, setSelectedExpense] = useState(null);

    const toggleDrawer = () => {
        setIsDrawerOpen(!isDrawerOpen);
    };

    const openModal = (expense = null) => {
        setSelectedExpense(expense);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setSelectedExpense(null);
    };

    const handleSaveExpense = (expense) => {
        if (selectedExpense) {
            // Edit the existing expense
            setExpenses((prev) =>
                prev.map((item) => (item.id === expense.id ? expense : item))
            );
        } else {
            // Add a new expense to the top of the list
            setExpenses((prev) => [expense, ...prev]);
        }
        closeModal();  // Close the modal after saving
    };

    const handleDeleteExpense = (id) => {
        setExpenses((prev) => prev.filter((expense) => expense.id !== id));
    };

    return (
        <div className="h-screen flex bg-[#3B2F2F]"> {/* Set the background color to white */}
            {/* Sidebar Drawer Button */}
            <button
                onClick={toggleDrawer}
                className="fixed top-4 left-4 w-12 h-12 bg-[#987554] rounded-full p-2 shadow-lg transition hover:scale-110"
            >
                <span className="text-3xl leading-[1.5rem] text-white">☰</span>
            </button>

            {/* Main Dashboard Content */}
            <div className="flex-1 flex flex-col p-6 space-y-6">
                <h1 className="text-4xl font-bold mb-4 text-center text-[#987554]">Dashboard</h1>

                {/* Expense List */}
                <div className="space-y-4 flex flex-col">
                    {expenses.map((expense) => (
                        <div key={expense.id} className="bg-white p-4 rounded-lg shadow-md flex justify-between items-center"   onClick={() => openModal(expense)}>
                            <div>
                                <h2 className="text-lg font-semibold text-[#987554]">{expense.category}</h2>
                                <p className="text-gray-500">${expense.amount} - {expense.date}</p>
                                <p className="text-sm text-gray-400">{expense.note}</p>
                            </div>
                            <div className="flex space-x-4">
                                {/* Edit Button */}
                                <button
                                    onClick={() => openModal(expense)}
                                    className="text-[#987554] hover:text-[#B89076]"
                                >
                                    Edit
                                </button>
                                {/* Delete Button */}
                                <button
                                    onClick={(event) => {
                                        event.stopPropagation();  // Prevent modal from opening
                                        handleDeleteExpense(expense.id);
                                    }}
                                    className="text-red-500"
                                >
                                    Delete
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Add Expense Button */}
                <button
                    onClick={() => openModal()}
                    className="fixed bottom-10 right-10 bg-[#987554] text-white w-16 h-16 flex items-center justify-center rounded-full p-2 shadow-lg transition hover:scale-110"
                >
                    <span className="text-3xl">+</span>
                </button>
            </div>

            {/* Drawer Component */}
            <Drawer isOpen={isDrawerOpen} onClose={() => setIsDrawerOpen(false)} />

            {/* Modal for Add/Edit Expense */}
            <Modal
                isOpen={isModalOpen}
                onClose={closeModal}
                onSave={handleSaveExpense}
                selectedExpense={selectedExpense}
            />
        </div>
    );
};

export default Dashboard;
