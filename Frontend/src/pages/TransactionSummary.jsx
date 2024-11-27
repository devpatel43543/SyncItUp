import React, { useState, useEffect, useContext } from "react";
import { useParams, Link } from "react-router-dom";
import { Loader2, ArrowLeft, CreditCard, Calendar, Users, User } from "lucide-react";
import ApiCallingContext from "../context/ApiCallingContext";
import { ENDPOINTS } from "../utils/Constants";
import { frontEndRoutes } from "../utils/FrontendRoutes.js";

const TransactionSummary = () => {
    const { groupId } = useParams();
    const { getRequest } = useContext(ApiCallingContext);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        const fetchTransactionSummary = async () => {
            try {
                setLoading(true);
                const url = `${ENDPOINTS.TRANSACTION_SUMMARY_GROUP}?groupId=${groupId}`;
                const response = await getRequest(url, true);
                const data = response.data?.data || [];
                // Sort transactions by date in descending order
                const sortedData = data.sort((a, b) =>
                    new Date(b.transactionDate) - new Date(a.transactionDate)
                );
                setTransactions(sortedData);
            } catch (error) {
                console.error("Error fetching transaction summary:", error);
                setError(
                    error.response?.data?.message || "Failed to load transaction summary."
                );
            } finally {
                setLoading(false);
            }
        };

        if (groupId) fetchTransactionSummary();
    }, [groupId, getRequest]);

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(yesterday.getDate() - 1);

        // Check if it's today
        if (date.toDateString() === today.toDateString()) {
            return 'Today';
        }
        // Check if it's yesterday
        if (date.toDateString() === yesterday.toDateString()) {
            return 'Yesterday';
        }
        // Otherwise return formatted date
        return date.toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    };

    const formatAmount = (amount) => {
        return typeof amount === 'number'
            ? amount.toFixed(2)
            : '0.00';
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="h-8 w-8 animate-spin text-gray-500" />
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center p-4 sm:p-8 lg:p-12 min-h-screen bg-gray-50">
            <div className="mx-auto w-full max-w-[800px]">
                <Link
                    to={frontEndRoutes.groupExpenseDashboard}
                    className="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6 transition-colors"
                >
                    <ArrowLeft className="h-4 w-4 mr-2" />
                    Back to Group Details
                </Link>

                <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-6">
                    Transaction Summary
                </h1>

                {error ? (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6">
                        {error}
                    </div>
                ) : transactions.length === 0 ? (
                    <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
                        <div className="text-gray-500 mb-2">No transactions found</div>
                        <p className="text-sm text-gray-400">Add an expense to get started</p>
                    </div>
                ) : (
                    <div className="flex flex-col gap-4">
                        {transactions.map((transaction, index) => (
                            <div
                                key={index}
                                className="p-6 bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow"
                            >
                                <div className="flex justify-between items-start mb-4">
                                    <div className="space-y-1">
                                        <h3 className="text-lg font-medium text-gray-900">
                                            {transaction.title || transaction.description || "Untitled Transaction"}
                                        </h3>
                                        <div className="flex items-center text-sm text-gray-500">
                                            <Calendar className="h-4 w-4 mr-1" />
                                            {formatDate(transaction.transactionDate)}
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <div className="flex items-center mb-1">
                                            <CreditCard className="h-4 w-4 mr-1 text-gray-400" />
                                            <span className="font-semibold text-gray-900">
                                                ${formatAmount(transaction.amountPaid)}
                                            </span>
                                        </div>
                                        <div className="flex items-center text-sm text-gray-500 justify-end">
                                            <User className="h-4 w-4 mr-1" />
                                            <span>{transaction.paidByEmail}</span>
                                        </div>
                                    </div>
                                </div>

                                <div className="border-t pt-4">
                                    <div className="flex items-center text-sm text-gray-600 mb-2">
                                        <Users className="h-4 w-4 mr-2" />
                                        <span>Split between {transaction.involvedMembersCount} members:</span>
                                    </div>
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 ml-6">
                                        {transaction.involvedMembers.map((member, memberIndex) => (
                                            <div
                                                key={memberIndex}
                                                className="flex items-center text-sm text-gray-600"
                                            >
                                                <div className="w-6 h-6 rounded-full bg-gray-100 flex items-center justify-center mr-2 text-xs">
                                                    {member.charAt(0).toUpperCase()}
                                                </div>
                                                <span className="truncate">{member}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default TransactionSummary;
