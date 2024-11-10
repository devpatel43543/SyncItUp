import React, { useContext } from "react";
import { useForm, useFieldArray } from "react-hook-form";
import ApiCallingContext from "../context/ApiCallingContext";
import { ENDPOINTS } from "../utils/Constants";
import { useNavigate, useParams } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import { showSuccessToast, showErrorToast } from "../utils/Toast";
import { frontEndRoutes } from "../utils/FrontendRoutes";

function AddNewMember() {
    const { postRequest } = useContext(ApiCallingContext);
    const { groupId } = useParams();
    const navigate = useNavigate()
    const { register, handleSubmit, control, setValue, getValues, reset } = useForm({
        defaultValues: {
            newMemberEmail: '', 
            memberEmails: [],  
        },
    });

    const { fields, append, remove } = useFieldArray({
        control,
        name: "memberEmails",
    });


    const handleAddMember = () => {
        const email = getValues("newMemberEmail");
        if (email) {
            append({ email });
            setValue("newMemberEmail", "");
        }
    };

    const onSubmit = async (data) => {
        console.log("Form submitted with data:", data);
        console.log("hello",groupId)
         const memberEmails = data.memberEmails.map((member) => member.email)
         if (memberEmails.length === 0) {
             showErrorToast("Please add at least one member before submitting.");
             return;
         }
         const formattedData = {
            newMemberEmails:memberEmails
         }
         try {
             const response = await postRequest(`${ENDPOINTS.ADD_NEW_MEMBER}?groupId=${groupId}`, true, formattedData);
             console.log(response.data);
             showSuccessToast("Members added successfully!");
             reset({
                 newMemberEmail: '',
                 memberEmails: [], 
             });
             navigate(frontEndRoutes.groupExpenseDashboard)
         } catch (error) {
             console.error("Error adding members:", error);
             showErrorToast("Failed to add members. Please try again.");
         }
    };
    const handleCancel = () => {
        reset({
            newMemberEmail: '',
            memberEmails: [],
        });
    };
    return (
        <div className="flex items-center justify-center p-12">
            <ToastContainer />
            <div className="mx-auto w-full max-w-[550px]">
                <h1 className="text-3xl font-bold mb-6">Add new Members</h1>

                <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
                    <div className="mb-5">
                        <label className="mb-3 block text-base font-medium text-[#07074D]">
                            Member Email
                        </label>
                        <div className="flex items-center mt-2">
                            <input
                                type="email"
                                placeholder="Enter member email"
                                {...register("newMemberEmail")}
                                className="w-full rounded-md border border-gray-300 p-3 text-base text-gray-700 focus:border-indigo-500 focus:shadow-md"
                            />
                            <button
                                type="button"
                                onClick={handleAddMember}
                                className="ml-2 bg-gray-200 text-gray-700 px-5 py-2 rounded-md hover:bg-gray-300"
                            >
                                Add
                            </button>
                        </div>
                    </div>
                    <div className="mb-5">
                        <label className="mb-3 block text-base font-medium text-[#07074D]">
                            Added Members
                        </label>
                        {fields.map((field, index) => (
                            <div key={field.id} className="flex items-center mt-2">
                                <input
                                    type="text"
                                    value={field.email}
                                    readOnly
                                    className="w-full rounded-md border border-gray-300 p-3 text-base text-gray-700 bg-gray-100"
                                />
                                <button
                                    type="button"
                                    onClick={() => remove(index)}
                                    className="ml-2 p-2 text-gray-500 hover:text-gray-700"
                                >
                                    X
                                </button>
                            </div>
                        ))}
                    </div>


                    <div className="flex justify-end space-x-4">
                        <button
                            type="button"
                            onClick={handleCancel} // Clears all fields
                            className="bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300"
                        >
                            Clear
                        </button>
                        <button
                            type="submit"
                            className="bg-black text-white px-4 py-2 rounded-md hover:bg-gray-800"
                        >
                            Add Members
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default AddNewMember;
