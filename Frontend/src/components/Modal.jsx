import React, { useState, useEffect, useRef } from 'react';

const Modal = ({ isOpen, onClose, onSave, selectedExpense }) => {
    const [date, setDate] = useState('');
    const [amount, setAmount] = useState('');
    const [category, setCategory] = useState('');
    const [note, setNote] = useState('');

    const modalRef = useRef(null); // Track modal content

    useEffect(() => {
        if (selectedExpense) {
            setDate(selectedExpense.date);
            setAmount(selectedExpense.amount);
            setCategory(selectedExpense.category);
            setNote(selectedExpense.note);
        } else {
            setDate('');
            setAmount('');
            setCategory('');
            setNote('');
        }
    }, [selectedExpense]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (modalRef.current && !modalRef.current.contains(event.target)) {
                onClose();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onClose]);

    if (!isOpen) return null;

    const handleSave = () => {
        const newOrUpdatedExpense = {
            id: selectedExpense?.id || new Date().getTime(),  // If it's a new expense, assign a new ID
            date,
            amount: Number(amount),
            category,
            note
        };

        // Pass the new or updated expense to the parent component (Dashboard)
        onSave(newOrUpdatedExpense);

        // Close the modal after saving
        onClose();
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div ref={modalRef} className="bg-white p-8 rounded-lg shadow-xl w-96 transform transition-all duration-300 ease-in-out">
                <h2 className="text-2xl font-bold mb-6 text-[#987554]">
                    {selectedExpense ? 'Edit Expense' : 'Add New Expense'}
                </h2>

                <label className="block mb-2 text-lg font-semibold text-[#987554]">Date</label>
                <input
                    type="date"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    className="w-full mb-4 p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#987554] transition"
                />

                <label className="block mb-2 text-lg font-semibold text-[#987554]">Amount</label>
                <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="w-full mb-4 p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#987554] transition"
                />

                <label className="block mb-2 text-lg font-semibold text-[#987554]">Category</label>
                <input
                    type="text"
                    value={category}
                    onChange={(e) => setCategory(e.target.value)}
                    className="w-full mb-4 p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#987554] transition"
                />

                <label className="block mb-2 text-lg font-semibold text-[#987554]">Note</label>
                <textarea
                    value={note}
                    onChange={(e) => setNote(e.target.value)}
                    className="w-full mb-4 p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#987554] transition resize-none"
                />

                <div className="flex justify-end space-x-4">
                    <button
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSave}
                        className="px-4 py-2 bg-[#987554] text-white rounded-lg hover:bg-[#B89076] transition transform hover:scale-105"
                    >
                        Save
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Modal;
