// Dashboard.jsx
import React, { useState } from 'react';
import Drawer from './Drawer';
import Modal from './Modal';
import dayjs from 'dayjs';
import Calendar from './Calendar'; // Import the new Calendar component

const Dashboard = () => {
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const [expenses, setExpenses] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedExpense, setSelectedExpense] = useState(null);
    const [currentYear, setCurrentYear] = useState(dayjs().year());
    const [currentMonth, setCurrentMonth] = useState(dayjs().month());
    const [selectedDate, setSelectedDate] = useState(null);
    const [isCalendarVisible, setIsCalendarVisible] = useState(false);

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
            setExpenses((prev) =>
                prev.map((item) => (item.id === expense.id ? expense : item))
            );
        } else {
            setExpenses((prev) => [expense, ...prev]);
        }
        closeModal();
    };

    const handleDeleteExpense = (id) => {
        setExpenses((prev) => prev.filter((expense) => expense.id !== id));
    };

    const filterByMonthAndYear = (expense) => {
        const expenseMonthYear = dayjs(expense.date).format('YYYY-MM');
        return expenseMonthYear === dayjs(new Date(currentYear, currentMonth)).format('YYYY-MM');
    };

    const filteredExpenses = expenses.filter(filterByMonthAndYear);
    const groupedExpenses = filteredExpenses.reduce((acc, expense) => {
        const date = expense.date;
        if (!acc[date]) {
            acc[date] = [];
        }
        acc[date].push(expense);
        return acc;
    }, {});

    const sortedDates = Object.keys(groupedExpenses).sort((a, b) => new Date(a) - new Date(b));

    // Move to the previous month
    const handlePrevMonth = () => {
        if (currentMonth === 0) {
            setCurrentMonth(11);
            setCurrentYear((prev) => prev - 1);
        } else {
            setCurrentMonth((prev) => prev - 1);
        }
    };

    // Move to the next month
    const handleNextMonth = () => {
        if (currentMonth === 11) {
            setCurrentMonth(0);
            setCurrentYear((prev) => prev + 1);
        } else {
            setCurrentMonth((prev) => prev + 1);
        }
    };

    return (
        <div className="h-screen flex bg-[#3B2F2F]">
            <button
                onClick={toggleDrawer}
                className="fixed top-4 left-4 w-12 h-12 bg-[#987554] rounded-full p-2 shadow-lg transition hover:scale-110"
            >
                <span className="text-3xl leading-[1.5rem] text-white">☰</span>
            </button>

            <div className="flex-1 flex flex-col p-6 space-y-6">
                <h1 className="text-4xl font-bold mb-4 text-center text-[#987554]">Trans.</h1>

                <button
                    onClick={() => setIsCalendarVisible(!isCalendarVisible)}
                    className="bg-[#987554] text-white px-4 py-2 rounded-md mx-auto mb-4">
                    {dayjs(new Date(currentYear, currentMonth)).format('MMMM YYYY')}
                </button>

                {isCalendarVisible && (
                    <Calendar
                        year={currentYear}
                        month={currentMonth}
                        onDateSelect={(selected) => {
                            setSelectedDate(selected);
                            openModal(); // Trigger modal for the selected date
                        }}
                        onPrevMonth={handlePrevMonth}
                        onNextMonth={handleNextMonth}
                        onClose={() => setIsCalendarVisible(false)} // Close calendar when clicking outside
                    />
                )}

                <div className="space-y-6">
                    {sortedDates.length === 0 ? (
                        <p className="text-center text-gray-400">No expenses for this month/year.</p>
                    ) : (
                        sortedDates.map((date) => (
                            <div key={date} className="space-y-4">
                                <h2 className="text-2xl font-bold text-[#987554]">{date}</h2>
                                <div className="space-y-4">
                                    {groupedExpenses[date].map((expense) => (
                                        <div
                                            key={expense.id}
                                            className="bg-white p-4 rounded-lg shadow-md flex justify-between items-center"
                                            onClick={() => openModal(expense)}
                                        >
                                            <div>
                                                <h2 className="text-lg font-semibold text-[#987554]">{expense.category}</h2>
                                                <p className="text-gray-500">${expense.amount}</p>
                                                <p className="text-sm text-gray-400">{expense.note}</p>
                                            </div>
                                            <div className="flex space-x-4">
                                                <button
                                                    onClick={() => openModal(expense)}
                                                    className="text-[#987554] hover:text-[#B89076]"
                                                >
                                                    Edit
                                                </button>
                                                <button
                                                    onClick={(event) => {
                                                        event.stopPropagation();
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
                            </div>
                        ))
                    )}
                </div>

                <button
                    onClick={() => openModal()}
                    className="fixed bottom-10 right-10 bg-[#987554] text-white w-16 h-16 flex items-center justify-center rounded-full p-2 shadow-lg transition hover:scale-110"
                >
                    <span className="text-3xl">+</span>
                </button>
            </div>

            <Drawer isOpen={isDrawerOpen} onClose={() => setIsDrawerOpen(false)} />
            <Modal
                isOpen={isModalOpen}
                onClose={closeModal}
                onSave={handleSaveExpense}
                expense={selectedExpense}
                selectedDate={selectedDate}
            />
        </div>
    );
};

export default Dashboard;
