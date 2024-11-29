import React, { useState, useEffect, useContext } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, UserPlus, Plus, Loader2, Calendar, Users, Pencil, Trash2, DollarSign } from "lucide-react";
import ApiCallingContext from "../context/ApiCallingContext";
import { frontEndRoutes } from "../utils/FrontendRoutes";
import { ENDPOINTS } from "../utils/Constants";
import ExpenseCard from "../components/ExpenseCard";
import { showErrorToast, showSuccessToast } from "../utils/Toast";
import { ToastContainer } from "react-toastify";

const GroupDetails = () => {
    const { groupId } = useParams();
    const navigate = useNavigate();
    const { getRequest, deleteRequest } = useContext(ApiCallingContext);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [groupData, setGroupData] = useState({
        groupName: "",
        members: [],
        expenses: [],
    });
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedExpense, setSelectedExpense] = useState(null);

    useEffect(() => {
        const fetchGroupDetails = async () => {
            try {
                setLoading(true);
                const groupUrl = `${ENDPOINTS.INDIVIDUAL_GROUP}?groupId=${groupId}`;
                const transactionSummaryUrl = `${ENDPOINTS.TRANSACTION_SUMMARY_GROUP}?groupId=${groupId}`;

                const [groupResponse, expenseResponse] = await Promise.all([
                    getRequest(groupUrl, true),
                    getRequest(transactionSummaryUrl, true)
                ]);

                const groupDetails = groupResponse.data?.data || {};
                const expenseDetails = expenseResponse.data?.data || [];

                // Create formatted expense data
                const formattedExpenses = expenseDetails.map(expense => ({
                    transactionId: expense.transactionId,
                    title: expense.title || "",
                    amountPaid: expense.amountPaid,
                    category: expense.category,
                    paidByEmail: expense.paidByEmail,
                    involvedMembers: expense.involvedMembers || [],
                    transactionDate: expense.transactionDate,
                    involvedMembersCount: expense.involvedMembers?.length || 0,
                    description: expense.description || ""
                }));

                const sortedExpenses = [...formattedExpenses].sort((a, b) =>
                    new Date(b.transactionDate) - new Date(a.transactionDate)
                );

                setGroupData({
                    groupName: groupDetails.groupName || "",
                    members: groupDetails.members || [],
                    expenses: sortedExpenses,
                });
            } catch (error) {
                console.error("Error fetching group details:", error);
                setError(
                    error.response?.data?.message || "Failed to load group details."
                );
            } finally {
                setLoading(false);
            }
        };

        if (groupId) fetchGroupDetails();
    }, [groupId, getRequest]);

    const handleUpdateClick = (expense) => {
        // Prepare expense data for update
        const expenseData = {
            transactionId: expense.transactionId,
            title: expense.title,
            amountPaid: expense.amountPaid,
            paidByEmail: expense.paidByEmail,
            category: expense.category,
            involvedMembers: expense.involvedMembers,
            description: expense.description
        };

        navigate(`${frontEndRoutes.addExpense}/${groupId}`, {
            state: { expense: expenseData }
        });
    };

    const handleDeleteClick = (transactionId) => {
        setSelectedExpense(transactionId);
        setShowDeleteModal(true);
    };

    const handleDelete = async () => {
        if (!selectedExpense) return;

        try {
            const url = `${ENDPOINTS.DELETE_GROUP_EXPENSE}?transactionId=${selectedExpense}`;
            const response = await deleteRequest(url, true);

            if (response.data?.result === "SUCCESS") {
                const updatedExpenses = groupData.expenses.filter(
                    expense => expense.transactionId !== selectedExpense
                );
                setGroupData(prev => ({ ...prev, expenses: updatedExpenses }));
                setShowDeleteModal(false);
                setSelectedExpense(null);
                showSuccessToast("Expense deleted successfully!");
            }
        } catch (error) {
            console.error("Error deleting expense:", error);
            showErrorToast(error.response?.data?.message || "Failed to delete expense.");
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);

        if (date.toDateString() === today.toDateString()) return 'Today';
        if (date.toDateString() === yesterday.toDateString()) return 'Yesterday';

        return date.toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    };

    const handleSettleUpClick = () => {
        navigate(`${frontEndRoutes.settleUp}/${groupId}`);
    };

    const handleAddExpenseClick = () =>
        navigate(`${frontEndRoutes.addExpense}/${groupId}`);

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="h-8 w-8 animate-spin text-gray-500" />
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center p-3 sm:p-6 lg:p-8 min-h-screen bg-gray-50">
            <ToastContainer />
            <div className="mx-auto w-full max-w-[900px]">
                <Link
                    to={frontEndRoutes.groupExpenseDashboard}
                    className="inline-flex items-center text-gray-600 hover:text-gray-900 mb-4 sm:mb-6 transition-colors"
                >
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Back to Groups
                </Link>

                {error ? (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6">
                        {error}
                    </div>
                ) : (
                    <>
                        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6 sm:mb-8 space-y-4 sm:space-y-0">
                            <div className="w-full sm:w-auto">
                                <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-2">
                                    {groupData.groupName}
                                </h1>
                                <p className="text-gray-600">
                                    Total members: {groupData.members.length}
                                </p>
                            </div>
                            <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
                                <button
                                    onClick={handleSettleUpClick}
                                    className="inline-flex items-center justify-center bg-emerald-600 text-white px-4 py-2.5 rounded-md hover:bg-emerald-700 transition-colors shadow-sm w-full sm:w-auto"
                                >
                                    <DollarSign className="mr-2 h-5 w-5" />
                                    Settle up
                                </button>
                                <button
                                    onClick={handleAddExpenseClick}
                                    className="inline-flex items-center justify-center bg-black text-white px-4 py-2.5 rounded-md hover:bg-gray-900 transition-colors shadow-sm w-full sm:w-auto"
                                >
                                    <Plus className="mr-2 h-5 w-5" />
                                    Add Expense
                                </button>
                            </div>
                        </div>

                        <div className="mb-8">
                            <ExpenseCard />
                        </div>

                        <div className="mb-8">
                            <h2 className="text-xl font-semibold mb-4">Members</h2>
                            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                                {groupData.members.map((member, index) => (
                                    <div
                                        key={index}
                                        className="flex items-center p-3 bg-white rounded-lg border border-gray-200 hover:shadow-sm transition-shadow"
                                    >
                                        <div className="w-8 h-8 bg-gray-100 rounded-full flex items-center justify-center mr-3 text-gray-600">
                                            {member.charAt(0).toUpperCase()}
                                        </div>
                                        <span className="text-gray-700 truncate">{member}</span>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="w-full">
                            <h2 className="text-xl font-semibold mb-4">Recent Transactions</h2>
                            <div className="space-y-4">
                                {groupData.expenses.length === 0 ? (
                                    <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
                                        <div className="text-gray-500 mb-2">No transactions found</div>
                                        <p className="text-sm text-gray-400">Add an expense to get started</p>
                                    </div>
                                ) : (
                                    groupData.expenses.map((expense) => (
                                        <div
                                            key={expense.transactionId}
                                            className="p-4 bg-white shadow-sm rounded-lg hover:shadow-md transition-shadow duration-200"
                                        >
                                            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between">
                                                <div className="flex-1 min-w-0 mb-4 sm:mb-0">
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between mb-2">
                                                        <div>
                                                            <h3 className="text-lg font-semibold text-gray-900 mb-1">
                                                                {expense.title || "Untitled Expense"}
                                                            </h3>
                                                            <span className="text-sm text-gray-500">
                                                                Category: {expense.category || "Uncategorized"}
                                                            </span>
                                                        </div>
                                                        <span className="text-2xl font-bold text-indigo-600 mt-2 sm:mt-0 sm:ml-4">
                                                            ${expense.amountPaid.toFixed(2)}
                                                        </span>
                                                    </div>
                                                    <p className="text-sm text-gray-500 mb-2">
                                                        Paid by: {expense.paidByEmail}
                                                    </p>
                                                    <div className="flex flex-wrap items-center text-xs text-gray-400 gap-4">
                                                        <div className="flex items-center">
                                                            <Calendar className="h-4 w-4 mr-1" />
                                                            {formatDate(expense.transactionDate)}
                                                        </div>
                                                        <div className="flex items-center">
                                                            <Users className="h-4 w-4 mr-1" />
                                                            Split between {expense.involvedMembersCount || expense.involvedMembers?.length || 0} members
                                                        </div>
                                                    </div>
                                                </div>
                                                <div className="flex items-center space-x-2 w-full sm:w-auto justify-end">
                                                    <button
                                                        onClick={() => handleUpdateClick(expense)}
                                                        className="text-indigo-600 hover:text-indigo-900 p-2 rounded-full hover:bg-indigo-100 transition-colors duration-200"
                                                        title="Edit expense"
                                                    >
                                                        <Pencil className="h-5 w-5" />
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteClick(expense.transactionId)}
                                                        className="text-red-600 hover:text-red-900 p-2 rounded-full hover:bg-red-100 transition-colors duration-200"
                                                        title="Delete expense"
                                                    >
                                                        <Trash2 className="h-5 w-5" />
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    </>
                )}
            </div>

            {/* Delete Modal */}
            {showDeleteModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
                    <div className="bg-white rounded-lg shadow-lg p-6 max-w-md w-full">
                        <h2 className="text-xl font-semibold mb-4">Delete Expense</h2>
                        <p className="text-gray-600 mb-4">
                            Are you sure you want to delete this expense? This action cannot be undone.
                        </p>
                        <div className="flex flex-col sm:flex-row justify-end gap-3">
                            <button
                                onClick={() => {
                                    setShowDeleteModal(false);
                                    setSelectedExpense(null);
                                }}
                                className="w-full sm:w-auto bg-gray-100 text-gray-800 rounded-lg px-4 py-2 hover:bg-gray-200"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleDelete}
                                className="w-full sm:w-auto bg-red-600 text-white rounded-lg px-4 py-2 hover:bg-red-700"
                            >
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default GroupDetails;
