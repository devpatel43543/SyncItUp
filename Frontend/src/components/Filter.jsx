import React, { useContext, useState } from 'react';
import { ENDPOINTS } from '../utils/Constants';
import ApiCallingContext from '../context/ApiCallingContext';

const Filter = ({ categories, setExpenses, getAllExpense }) => {
    const { getRequest } = useContext(ApiCallingContext);

    const [filters, setFilters] = useState({
        fromDate: '',
        toDate: '',
        categoryId: ''
    });

    const handleFilterChange = (e) => {
        const { name, value } = e.target;
        setFilters(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const getFilteredByDateAndCategory = async () => {
        try {

            const dateResponse = await getRequest(
                `${ENDPOINTS.TRANSACTIONS_BY_DATE}?fromDate=${filters.fromDate}&toDate=${filters.toDate}`,
                true
            );

            if (dateResponse.data.result === "SUCCESS") {

                const categoryResponse = await getRequest(
                    `${ENDPOINTS.TRANSACTIONS_BY_CATEGORY}?categoryId=${filters.categoryId}`,
                    true
                );

                if (categoryResponse.data.result === "SUCCESS") {
                    const dateFilteredExpenses = dateResponse.data.data;
                    const categoryFilteredExpenses = categoryResponse.data.data;

                    const combinedResults = dateFilteredExpenses.filter(dateExp =>
                        categoryFilteredExpenses.some(catExp => catExp.txnId === dateExp.txnId)
                    );

                    setExpenses(combinedResults);
                }
            }
        } catch (error) {
            console.error("Error fetching filtered expenses:", error);
        }
    };

    const getFilteredByDate = async () => {
        try {
            console.log("Fetching transactions between dates:", {
                fromDate: filters.fromDate,
                toDate: filters.toDate
            });


            const response = await getRequest(
                `${ENDPOINTS.TRANSACTIONS_BY_DATE}?fromDate=${filters.fromDate}&toDate=${filters.toDate}`,
                true
            );

            console.log("Date filter response:", response);

            if (response.data.result === "SUCCESS") {
                setExpenses(response.data.data);
            }
        } catch (error) {
            console.error("Error fetching filtered expenses:", error);
        }
    };

    const getFilteredByCategory = async () => {
        try {
            console.log("Fetching transactions for category:", filters.categoryId);


            const response = await getRequest(
                `${ENDPOINTS.TRANSACTIONS_BY_CATEGORY}?categoryId=${filters.categoryId}`,
                true
            );

            console.log("Category filter response:", response);

            if (response.data.result === "SUCCESS") {
                setExpenses(response.data.data);
            }
        } catch (error) {
            console.error("Error fetching category expenses:", error);
        }
    };

    const applyFilters = () => {
        if (filters.fromDate && filters.toDate && filters.categoryId) {
            getFilteredByDateAndCategory();
        } else if (filters.fromDate && filters.toDate) {
            getFilteredByDate();
        } else if (filters.categoryId) {
            getFilteredByCategory();
        } else {
            getAllExpense();
        }
    };

    const clearFilters = () => {
        setFilters({
            fromDate: '',
            toDate: '',
            categoryId: ''
        });
        getAllExpense();
    };

    return (
        <div className="bg-white p-4 rounded-lg shadow mb-6">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Start Date</label>
                    <input
                        type="date"
                        name="fromDate"
                        value={filters.fromDate}
                        onChange={handleFilterChange}
                        className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">End Date</label>
                    <input
                        type="date"
                        name="toDate"
                        value={filters.toDate}
                        onChange={handleFilterChange}
                        min={filters.fromDate}
                        className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Category</label>
                    <select
                        name="categoryId"
                        value={filters.categoryId}
                        onChange={handleFilterChange}
                        className="mt-1 block w-full rounded-md border border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                    >
                        <option value="">All Categories</option>
                        {categories?.map((category) => (
                            <option key={category.categoryId} value={category.categoryId}>
                                {category.category}
                            </option>
                        ))}
                    </select>
                </div>
                <div className="flex items-end">
                    <button
                        onClick={applyFilters}
                        className="w-full bg-indigo-600 text-white rounded-md px-4 py-2 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                    >
                        Apply Filters
                    </button>
                </div>
            </div>
            <div className="mt-2 text-right">
                <button
                    onClick={clearFilters}
                    className="text-sm text-gray-600 hover:text-gray-900"
                >
                    Clear Filters
                </button>
            </div>
        </div>
    );
};

export default Filter;