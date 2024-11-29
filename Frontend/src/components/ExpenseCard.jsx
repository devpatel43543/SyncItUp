import React, { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import ApiCallingContext from '../context/ApiCallingContext';
import { ENDPOINTS } from '../utils/Constants';
import { Wallet, TrendingUp, TrendingDown } from 'lucide-react';

const ExpenseCard = () => {
    const [loading, setLoading] = useState(true);
    const [summary, setSummary] = useState({
        totalGroupExpense: 0,
        totalCredit: 0,
        totalDebit: 0
    });
    const { groupId } = useParams();
    const { getRequest } = useContext(ApiCallingContext);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                setLoading(true);
                const response = await getRequest(`${ENDPOINTS.DEBIT_CREDIT_SUMMARY}?groupId=${groupId}`, true);

                if (response?.data?.result === "SUCCESS") {
                    setSummary({
                        totalGroupExpense: response.data.data?.totalGroupExpense || 0,
                        totalCredit: response.data.data?.totalCredit || 0,
                        totalDebit: response.data.data?.totalDebit || 0
                    });
                }
            } catch (error) {
                console.error('Error fetching summary:', error);
                setSummary({
                    totalGroupExpense: 0,
                    totalCredit: 0,
                    totalDebit: 0
                });
            } finally {
                setLoading(false);
            }
        };

        if (groupId) {
            fetchSummary();
        }
    }, [groupId, getRequest]);

    const cards = [
        {
            title: 'Total expense',
            subtitle: 'Group total',
            amount: Number(summary.totalGroupExpense || 0),
            icon: <Wallet className="h-6 w-6 text-white" />,
            iconBg: 'bg-blue-500',
            bgColor: 'bg-blue-50',
            textColor: 'text-blue-600'
        },
        {
            title: 'You are owed',
            subtitle: 'To receive',
            amount: Number(summary.totalCredit || 0),
            icon: <TrendingUp className="h-6 w-6 text-white" />,
            iconBg: 'bg-green-500',
            bgColor: 'bg-green-50',
            textColor: 'text-green-600'
        },
        {
            title: 'You owe',
            subtitle: 'To pay',
            amount: Number(summary.totalDebit || 0),
            icon: <TrendingDown className="h-6 w-6 text-white" />,
            iconBg: 'bg-red-500',
            bgColor: 'bg-red-50',
            textColor: 'text-red-600'
        }
    ];

    if (loading) {
        return (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {[1, 2, 3].map((i) => (
                    <div key={i} className="h-32 bg-gray-100 rounded-lg animate-pulse" />
                ))}
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {cards.map((card, index) => (
                <div
                    key={index}
                    className={`${card.bgColor} rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200 p-4 sm:p-6`}
                >
                    <div className="flex justify-between items-start">
                        <div className="space-y-2">
                            <p className={`text-sm ${card.textColor} opacity-75`}>
                                {card.subtitle}
                            </p>
                            <h3 className={`text-lg font-semibold ${card.textColor}`}>
                                {card.title}
                            </h3>
                            <p className={`text-2xl font-bold ${card.textColor}`}>
                                ${(card.amount || 0).toFixed(2)}
                            </p>
                        </div>
                        <div className={`${card.iconBg} p-3 rounded-lg shadow-sm`}>
                            {card.icon}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ExpenseCard;
