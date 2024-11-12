export const BASE_URL = "http://localhost:8080";
export const AUTH_TOKEN = "AUTH_TOKEN";

export const ENDPOINTS = {
    REGISTER: "/api/check/register",
    LOGIN: "/api/check/login",
    FORGET_PASSWORD: "/api/check/forgotPassword",
    RESET_PASSWORD: "/api/check/passwordReset",
    VERIFY_OTP: "/api/check/verifyOtp",
    RESEND_OTP:"/api/check/resendOtp",
    CREATE_PERSONAL_EXPENSE:"/user/transaction/logTransaction",//for adding expense
    ALL_PERSONAL_EXPENSE:"/user/transaction/getAllTransactions",// for getting all expenses
    UPDATE_EXPENSE:"/user/transaction/updateTransaction", //for update expense
    DELETE_EXPENSE:"/user/transaction/deleteTransaction",
    ALL_CATEGORY:"/category/getAllCategories",
    CREATE_GROUP:"/group/create",
    ALl_GROUP:"/group/allGroup",
    ADD_NEW_MEMBER:"/group/addNewMember",
    INDIVIDUAL_GROUP:"/group/individual",
    REMOVE_MEMBER:"/group/removeMember",
    ADD_CATEGORY:"/category/addCategory",
    REMOVE_CATEGORY:"/category/deleteCategory",
    TRANSACTIONS_BY_DATE: '/user/transaction/getTransactionsBetweenDate',
    TRANSACTIONS_BY_CATEGORY: '/user/transaction/getTransactionsWithCategory'
}
