import React, { useState, useEffect, useContext } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, DollarSign, Loader2, Users } from "lucide-react";
import ApiCallingContext from "../context/ApiCallingContext";
import { frontEndRoutes } from "../utils/FrontendRoutes";
import { ENDPOINTS } from "../utils/Constants";
import { showErrorToast, showSuccessToast } from "../utils/Toast";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const SettleUp = () => {
    const { groupId } = useParams();
    const navigate = useNavigate();
    const { getRequest, postRequest } = useContext(ApiCallingContext);

    const [loading, setLoading] = useState(true);
    const [settling, setSettling] = useState(false);
    const [groupData, setGroupData] = useState({
        groupName: "",
        debts: []
    });
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [selectedDebt, setSelectedDebt] = useState(null);

    useEffect(() => {
        if (groupId) {
            fetchUserDebts();
        }
    }, [groupId]);

    const fetchUserDebts = async () => {
        try {
            setLoading(true);
            const response = await getRequest(`${ENDPOINTS.USER_DEBT_GROUP}?groupId=${groupId}`, true);

            if (response?.data?.result === "SUCCESS") {
                const debts = response.data.data || [];
                setGroupData({
                    groupName: debts[0]?.groupName || "Group",
                    debts: debts
                });
            } else {
                throw new Error(response?.data?.message || "Failed to load user debts");
            }
        } catch (error) {
            console.error("Error fetching debts:", error);
            showErrorToast(error.response?.data?.message || "Failed to load user debts");
        } finally {
            setLoading(false);
        }
    };

    const handleSettleUp = (debt) => {
        setSelectedDebt(debt);
        setShowConfirmModal(true);
    };

    const confirmSettlement = async () => {
        if (!selectedDebt) {
            showErrorToast("No settlement selected");
            return;
        }

        try {
            setSettling(true);
            const settleUpData = {
                groupId: groupId,
                creditorEmail: selectedDebt.creditorEmail,
                amount: selectedDebt.amount
            };

            const response = await postRequest(
                ENDPOINTS.SETTLE_DEBT,
                true,
                settleUpData
            );

            if (response?.data?.result === "SUCCESS") {
                showSuccessToast("Settlement completed successfully!");
                setShowConfirmModal(false);
                setSelectedDebt(null);
                navigate(`${frontEndRoutes.groupDetails}/${groupId}`);
            } else {
                throw new Error(response?.data?.message || "Failed to complete settlement");
            }
        } catch (error) {
            console.error("Settlement error:", error);
            const errorMessage = error.response?.data?.message ||
            error.response?.status === 400
                ? "Invalid settlement request. Please try again."
                : "An unexpected error occurred while settling the debt.";
            showErrorToast(errorMessage);
            setShowConfirmModal(false);
            setSelectedDebt(null);
        } finally {
            setSettling(false);
        }
    };

    const closeConfirmModal = () => {
        if (!settling) {
            setShowConfirmModal(false);
            setSelectedDebt(null);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="h-8 w-8 animate-spin text-gray-500" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 p-4 sm:p-6 lg:p-8">
            <ToastContainer />

            <div className="max-w-4xl mx-auto">
                {/* Header */}
                <div className="mb-8">
                    <Link
                        to={`${frontEndRoutes.groupDetails}/${groupId}`}
                        className="inline-flex items-center text-gray-600 hover:text-gray-900 mb-4"
                    >
                        <ArrowLeft className="h-4 w-4 mr-2" />
                        Back to Group
                    </Link>
                    <h1 className="text-2xl font-bold text-gray-900 mb-2">Settle Up</h1>
                    <p className="text-gray-600">Group: {groupData.groupName}</p>
                </div>

                {/* Debts Section */}
                <div className="bg-white rounded-lg shadow-lg p-6">
                    <h2 className="text-xl font-semibold mb-6 text-gray-800">Pending Settlements</h2>

                    {groupData.debts.length === 0 ? (
                        <div className="text-center py-12">
                            <Users className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                            <p className="text-gray-500">No pending settlements!</p>
                            <p className="text-sm text-gray-400">Everyone is settled up in this group</p>
                        </div>
                    ) : (
                        <div className="space-y-4">
                            {groupData.debts.map((debt, index) => (
                                <div
                                    key={index}
                                    className="flex items-center justify-between p-6 border border-gray-100 rounded-xl bg-white hover:shadow-md transition-all duration-200"
                                >
                                    <div className="flex items-center space-x-5">
                                        <div className="w-12 h-12 bg-green-50 rounded-full flex items-center justify-center">
                                            <DollarSign className="h-6 w-6 text-green-600" />
                                        </div>
                                        <div>
                                            <div className="flex items-center mb-1">
                                                <p className="font-medium text-gray-900">
                                                    {debt.creditorEmail}
                                                </p>
                                            </div>
                                            <div className="flex items-center space-x-2">
                                                <span className="text-lg font-semibold text-green-600">
                                                    ${Number(debt.amount).toFixed(2)}
                                                </span>
                                                <span className="text-sm text-gray-500">
                                                    to be received
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    {/*<button*/}
                                    {/*    onClick={() => handleSettleUp(debt)}*/}
                                    {/*    className="px-6 py-2.5 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-medium shadow-sm hover:shadow focus:ring-2 focus:ring-green-500 focus:ring-offset-2"*/}
                                    {/*>*/}
                                    {/*    Settle Up*/}
                                    {/*</button>*/}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Settlement Confirmation Modal */}
            {showConfirmModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
                    <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md mx-4">
                        <h2 className="text-xl font-semibold mb-4 text-gray-900">Confirm Settlement</h2>
                        <div className="mb-6">
                            <div className="flex items-center space-x-3 mb-4">
                                <div className="w-10 h-10 bg-green-50 rounded-full flex items-center justify-center">
                                    <DollarSign className="h-5 w-5 text-green-600" />
                                </div>
                                <div>
                                    <p className="text-lg font-medium text-gray-900">
                                        ${Number(selectedDebt?.amount).toFixed(2)}
                                    </p>
                                    <p className="text-sm text-gray-500">
                                        to {selectedDebt?.creditorEmail}
                                    </p>
                                </div>
                            </div>
                            <p className="text-sm text-gray-500">
                                This will record the payment and update the group balances.
                            </p>
                        </div>
                        <div className="flex justify-end gap-3">
                            <button
                                onClick={closeConfirmModal}
                                disabled={settling}
                                className="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50 font-medium"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={confirmSettlement}
                                disabled={settling}
                                className="px-4 py-2 text-white bg-green-600 rounded-lg hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center justify-center min-w-[120px] font-medium shadow-sm"
                            >
                                {settling ? (
                                    <>
                                        <Loader2 className="h-4 w-4 animate-spin mr-2" />
                                        Settling...
                                    </>
                                ) : (
                                    'Confirm Settlement'
                                )}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SettleUp;