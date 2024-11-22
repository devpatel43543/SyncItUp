import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ApiCallingContext from '../context/ApiCallingContext';
import { ENDPOINTS } from '../utils/Constants';
import { showSuccessToast, showErrorToast } from '../utils/Toast';
import { Trash2 } from 'lucide-react';
import { ToastContainer } from 'react-toastify';

function Requests() {
    const { getRequest, deleteRequest, postRequest } = useContext(ApiCallingContext);
    const [requests, setRequests] = useState([])
    const { groupId } = useParams();
    useEffect(() => {
        const fetchAllRequests = async () => {
            try {
                const response = await getRequest(`${ENDPOINTS.PENDING_REQUEST}`, true);
                setRequests(response.data.data); // Assuming `members` is a list of emails
            } catch (error) {
                console.error("Error fetching group members:", error);
                showErrorToast("Failed to fetch group members.");
            }
        };
        fetchAllRequests();
    }, [getRequest]);

    const handleAcceptRequest = async (groupId, userEmail) => {
        try {
            await postRequest(`${ENDPOINTS.ACCEPT_REQUEST}?groupId=${groupId}`, true);
            showSuccessToast("Request accepted successfully!");
            setRequests((prevRequests) =>
                prevRequests.filter((request) => request.groupId !== groupId)
            );
            } catch (error) {
            console.error("Error accepting request:", error);
            showErrorToast("Failed to accept request.");
        }
    };
    const handleRejectRequest = async (groupId, userEmail) => {
        try {
            await deleteRequest(`${ENDPOINTS.REJECT_REQUEST}?groupId=${groupId}`, true);
            showSuccessToast("Request rejected successfully!");
            setRequests((prevRequests) =>
                prevRequests.filter((request) => request.groupId !== groupId)
            );
            } catch (error) {
            console.error("Error rejecting request:", error);
            showErrorToast("Failed to reject request.");
        }
    };
    return (
<div className="flex items-center justify-center p-12">
    <ToastContainer />
    <div className="mx-auto w-full max-w-[650px]"> {/* Increased max-width */}
        <h2 className="text-2xl font-semibold mb-6 text-gray-800">Group Join Requests</h2>
        <div className="space-y-4">
            {requests.map((request) => (
                <div
                    key={request.groupId}
                    className="flex flex-col sm:flex-row justify-between items-center border border-gray-300 p-6 rounded-lg shadow-sm bg-white space-y-4 sm:space-y-0 space-x-8" /* Added space between elements */
                >
                    <span className="text-gray-700 text-lg font-medium">
                        Hey <span>{request.creatorEmail}</span> has invited you to join the group <span className="font-bold ">{request.groupName}</span>.
                    </span>
                    <div className="flex space-x-4">
                        <button
                            onClick={() => handleAcceptRequest(request.groupId, request.userEmail)}
                            className="rounded-lg bg-green-500 py-2 px-6 font-sans text-sm font-semibold uppercase text-white transition-all hover:bg-green-600 focus:ring focus:ring-green-200"
                        >
                            Accept
                        </button>
                        <button
                            onClick={() => handleRejectRequest(request.groupId, request.userEmail)}
                            className="rounded-lg bg-red-500 py-2 px-6 font-sans text-sm font-semibold uppercase text-white transition-all hover:bg-red-600 focus:ring focus:ring-red-200"
                        >
                            Reject
                        </button>
                    </div>
                </div>
            ))}
        </div>
    </div>
</div>

    )
}

export default Requests