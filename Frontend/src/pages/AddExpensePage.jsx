import React, { useContext, useEffect, useState } from 'react';
import { useForm } from "react-hook-form";
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { frontEndRoutes } from "../utils/FrontendRoutes.js";
import ApiCallingContext from '../context/ApiCallingContext';
import { ENDPOINTS } from '../utils/Constants';
import { ToastContainer } from "react-toastify";
import { showErrorToast, showSuccessToast } from "../utils/Toast";
import { ArrowLeft } from 'lucide-react';
import { catrgory } from "../utils/Category.js";

function AddExpensePage() {
    const { groupId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const { postRequest, putRequest, getRequest } = useContext(ApiCallingContext);
    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(true);

    // Check if we're in update mode
    const isUpdateMode = location.state?.expense !== undefined;
    const expenseToUpdate = location.state?.expense;

    const { register, handleSubmit, setValue, watch, reset } = useForm({
        defaultValues: {
            amount: '',
            paidBy: '',
            splitBetween: [],
            category: '',
            title: '' // Added title field
        }
    });

    useEffect(() => {
        // If we're in update mode, set the form values
        if (isUpdateMode && expenseToUpdate) {
            reset({
                amount: expenseToUpdate.amountPaid.toString(),
                paidBy: expenseToUpdate.paidByEmail,
                splitBetween: expenseToUpdate.involvedMembers || [],
                category: expenseToUpdate.category,
                title: expenseToUpdate.title || ''
            });
        }
    }, [isUpdateMode, expenseToUpdate, reset]);

    useEffect(() => {
        const fetchMembers = async () => {
            try {
                setLoading(true);
                const response = await getRequest(`${ENDPOINTS.INDIVIDUAL_GROUP}?groupId=${groupId}`, true);

                if (response.data && response.data.result === "SUCCESS" && response.data.data) {
                    const groupDetails = response.data.data;
                    setMembers(groupDetails.members || []);
                }
            } catch (error) {
                console.error('Error fetching members:', error);
                showErrorToast('Failed to load members');
            } finally {
                setLoading(false);
            }
        };

        if (groupId) {
            fetchMembers();
        }
    }, [groupId, getRequest]);

    const onSubmit = async (data) => {
        try {
            if (!data.amount || !data.paidBy || !data.category || !data.splitBetween?.length || !data.title) {
                showErrorToast("All fields are required");
                return;
            }

            if (!isUpdateMode) {
                // Create new expense
                const expenseData = {
                    groupId: parseInt(groupId),
                    paidByEmail: data.paidBy,
                    amount: parseFloat(data.amount),
                    title: data.title,
                    category: data.category,
                    involvedMembers: data.splitBetween
                };

                const response = await postRequest(ENDPOINTS.ADD_GROUP_EXPENSE, true, expenseData);

                if (response.data && response.data.result === "SUCCESS") {
                    showSuccessToast("Expense added successfully!");
                    navigate(`${frontEndRoutes.groupDetails}/${groupId}`);
                } else {
                    showErrorToast(response.data?.message || "Failed to create expense");
                }
            } else {
                // Update existing expense
                const updateData = {
                    transactionId: expenseToUpdate.transactionId,
                    amount: parseFloat(data.amount),
                    paidByEmail: data.paidBy,
                    title: data.title,
                    category: data.category,
                    involvedMembers: data.splitBetween
                };

                const response = await putRequest(ENDPOINTS.UPDATE_GROUP_EXPENSE, true, updateData);

                if (response.data && response.data.result === "SUCCESS") {
                    showSuccessToast("Expense updated successfully!");
                    navigate(`${frontEndRoutes.groupDetails}/${groupId}`);
                } else {
                    showErrorToast(response.data?.message || "Failed to update expense");
                }
            }
        } catch (error) {
            console.error('Error submitting expense:', error);
            showErrorToast(error.response?.data?.message || `Failed to ${isUpdateMode ? 'update' : 'create'} expense`);
        }
    };

    const handleSplitSelect = (member) => {
        const currentSplit = watch('splitBetween') || [];
        let newSplit;

        if (currentSplit.includes(member)) {
            newSplit = currentSplit.filter(m => m !== member);
        } else {
            newSplit = [...currentSplit, member];
        }

        setValue('splitBetween', newSplit);
    };

    const handleAmountChange = (e) => {
        let value = e.target.value.replace(/[^\d.]/g, '');

        const parts = value.split('.');
        if (parts.length > 2) {
            value = parts[0] + '.' + parts.slice(1).join('');
        }

        if (parts.length === 2 && parts[1].length > 2) {
            value = parseFloat(value).toFixed(2);
        }

        setValue('amount', value);
    };

    const handleCancel = () => {
        navigate(`${frontEndRoutes.groupDetails}/${groupId}`);
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-gray-900"></div>
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center min-h-screen bg-gray-50 p-4">
            <div className="w-full max-w-[600px]">
                <ToastContainer />

                <div
                    onClick={() => navigate(`${frontEndRoutes.groupDetails}/${groupId}`)}
                    className="flex items-center text-gray-600 hover:text-gray-900 cursor-pointer mb-6"
                >
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Back to Group
                </div>

                <div className="mb-8">
                    <h1 className="text-2xl sm:text-3xl font-bold text-gray-800">
                        {isUpdateMode ? 'Update Expense' : 'Add New Expense'}
                    </h1>
                    <p className="text-gray-600 mt-1">
                        {isUpdateMode ? 'Update the expense details below' : 'Enter the expense details below'}
                    </p>
                </div>

                <div className="bg-white rounded-lg shadow-md p-6">
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                        {/* Title Field */}
                        <div>
                            <label htmlFor="title" className="mb-3 block text-sm font-medium text-gray-700">
                                Title
                            </label>
                            <input
                                type="text"
                                id="title"
                                {...register("title", { required: "Title is required" })}
                                className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2"
                                placeholder="Enter expense title"
                            />
                        </div>

                        {/* Category Field */}
                        <div>
                            <label htmlFor="category" className="mb-3 block text-sm font-medium text-gray-700">
                                Category
                            </label>
                            <select
                                id="category"
                                {...register("category", { required: "Category is required" })}
                                className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2"
                            >
                                <option value="">Select a category</option>
                                {Object.entries(catrgory).map(([id, name]) => (
                                    <option key={id} value={name}>
                                        {name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Amount Field */}
                        <div>
                            <label htmlFor="amount" className="mb-3 block text-sm font-medium text-gray-700">
                                Amount
                            </label>
                            <input
                                id="amount"
                                type="text"
                                inputMode="decimal"
                                placeholder="Enter amount"
                                {...register("amount", {
                                    required: "Amount is required",
                                    onChange: handleAmountChange,
                                })}
                                className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2"
                            />
                        </div>

                        {/* Paid By Field */}
                        <div>
                            <label htmlFor="paidBy" className="mb-3 block text-sm font-medium text-gray-700">
                                Paid By
                            </label>
                            <select
                                id="paidBy"
                                {...register("paidBy", { required: "Paid by is required" })}
                                className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2"
                                disabled={isUpdateMode} // Disable in update mode
                            >
                                <option value="">Select who paid</option>
                                {members.map((member, index) => (
                                    <option key={index} value={member}>
                                        {member}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Split Between Field */}
                        <div>
                            <label className="mb-3 block text-sm font-medium text-gray-700">
                                Split Between
                            </label>
                            <div className="space-y-2 max-h-48 overflow-y-auto p-2 border border-gray-200 rounded-md">
                                {members.map((member, index) => (
                                    <div key={index} className="flex items-center space-x-2 p-2 hover:bg-gray-50 rounded">
                                        <input
                                            type="checkbox"
                                            checked={watch('splitBetween')?.includes(member) || false}
                                            onChange={() => handleSplitSelect(member)}
                                            className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                                        />
                                        <span className="text-sm text-gray-700">{member}</span>
                                    </div>
                                ))}
                            </div>
                            {watch('splitBetween')?.length > 0 && (
                                <p className="mt-2 text-sm text-gray-500">
                                    Selected {watch('splitBetween').length} member(s)
                                </p>
                            )}
                        </div>

                        {/* Action Buttons */}
                        <div className="flex justify-end space-x-4 pt-4">
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                                Cancel
                            </button>
                            <button
                                type="submit"
                                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                            >
                                {isUpdateMode ? 'Update Expense' : 'Add Expense'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default AddExpensePage;
