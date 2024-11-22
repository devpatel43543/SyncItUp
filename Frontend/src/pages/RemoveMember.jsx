import React, { useContext, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ApiCallingContext from '../context/ApiCallingContext';
import { ENDPOINTS } from '../utils/Constants';
import { showSuccessToast, showErrorToast } from '../utils/Toast';
import { Trash2 } from 'lucide-react';
import { ToastContainer } from 'react-toastify';

function RemoveMember() {
    const { getRequest, deleteRequest  } = useContext(ApiCallingContext);
    const { groupId } = useParams();
    const [members, setMembers] = useState([]);

    useEffect(() => {
        const fetchGroupMembers = async () => {
            try {
                const response = await getRequest(`${ENDPOINTS.ALL_MEMBERS}?groupId=${groupId}`, true);
                // setMembers(response.data.data.members); // Assuming `members` is a list of emails

                //
                const fetchedMembers = response.data?.data?.members || response.data?.data || []; // Safely handle undefined
                setMembers(Array.isArray(fetchedMembers) ? fetchedMembers : []); 
            } catch (error) {
                console.error("Error fetching group members:", error);
                showErrorToast("Failed to fetch group members.");
            }
        };
        fetchGroupMembers();
    }, [getRequest, groupId]);

    const handleRemoveMember = async (memberEmail) => {
        try {
            const response = await deleteRequest(`${ENDPOINTS.REMOVE_MEMBER}?groupId=${groupId}&memberEmail=${memberEmail}`, true);
            showSuccessToast(response.data.message);
            setMembers(members.filter((email) => email !== memberEmail)); 
        } catch (error) {
            console.error("Full error details:", error); 
             if (error.response && error.response.status === 401) {
                console.log("hello")
                 showErrorToast("You do not have permission to remove member.");
             } else {
                 showErrorToast("Failed to remove member.");
             }
        }
    };

    return (
        <div className="flex items-center justify-center p-12">
        <ToastContainer/>
        <div className="mx-auto w-full max-w-[550px]">
            <h2 className="text-2xl font-semibold mb-4">Remove Members from Group</h2>
            <div className="space-y-4">
                {members.map((memberEmail) => (
                    <div key={memberEmail} className="flex justify-between items-center border border-gray-200 p-4 rounded-md">
                        <span>{memberEmail}</span>
                        <button
                            onClick={() => handleRemoveMember(memberEmail)}
                            className="text-red-600 hover:underline"
                        >
                            <Trash2 className="text-red-500" /> 
                        </button>
                    </div>
                ))}
            </div>
        </div>
        </div>
    );
}

export default RemoveMember;
