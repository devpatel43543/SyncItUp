import React, { useContext } from "react";
import { useForm, useFieldArray } from "react-hook-form";
import ApiCallingContext from "../context/ApiCallingContext";
import { ENDPOINTS } from "../utils/Constants";
import { ToastContainer } from "react-toastify";
import { showErrorToast, showSuccessToast } from "../utils/Toast";

function CreateGroup() {
    const {postRequest} = useContext(ApiCallingContext)
    const { register, handleSubmit, control, reset, setValue, getValues } = useForm({
        defaultValues: {
            groupName: '',
            description: '',
            newMemberEmail: '',
            memberEmails: [],
        },
    });

    const { fields, append, remove } = useFieldArray({
        control,
        name: "memberEmails",
    });

    const onSubmit = async (data) => {
        const formattedData = {
            groupName: data.groupName,
            description: data.description,
            memberEmail: data.memberEmails.length > 0 ? data.memberEmails.map((member) => member.email) : [],
        };

        try {
            console.log(formattedData.memberEmail)
            const response = await postRequest(ENDPOINTS.CREATE_GROUP, true, formattedData);
            console.log(response.data);

            reset({
                groupName: '',
                description: '',
                newMemberEmail: '',
                memberEmails: [],
            });
            showSuccessToast("Group created successfully!"); 
        } catch (error) {
            console.error("Error creating group:", error);

            if (error.response && error.response.status === 409) {
                showErrorToast("Group name already exists. Please choose a different name")
            } else {
                toast.error("Something went wrong. Please try again.", {
                    position: "top-right",
                    autoClose: 3000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    theme: "light",
                });
            }
        }

        setValue("groupName", "");
        setValue("description", "");
    };

    const handleAddMember = () => {
        const email = getValues("newMemberEmail"); 
        if (email) {
            append({ email }); 
            setValue("newMemberEmail", "");
        }
    };

    const handleCancel = () => {
        // Reset the form fields to their default values
        reset({
            groupName: '',
            description: '',
            newMemberEmail: '',
            memberEmails: [], // Reset to an empty array
        });
    };

    return (
        <div className="flex items-center justify-center p-12">
              <ToastContainer />

            <div className="mx-auto w-full max-w-[550px]">
                <h1 className="text-3xl font-bold mb-6">Create New Group</h1>

                <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
                    {/* Group Name */}
                    <div className="mb-5">
                        <label htmlFor="groupName" className="mb-3 block text-base font-medium text-[#07074D]">
                            Group Name
                        </label>
                        <input
                            id="groupName"
                            name="groupName"
                            type="text"
                            placeholder="Enter group name"
                            {...register("groupName", { required: true })}
                            className="w-full rounded-md border border-gray-300 p-3 text-base text-gray-700 focus:border-indigo-500 focus:shadow-md"
                        />
                    </div>

                    {/* Group Description */}
                    <div className="mb-5">
                        <label htmlFor="description" className="mb-3 block text-base font-medium text-[#07074D]">
                            Group Description
                        </label>
                        <textarea
                            id="description"
                            name="description"
                            placeholder="Enter group description"
                            rows={3}
                            {...register("description", { required: true })}
                            className="w-full rounded-md border border-gray-300 p-3 text-base text-gray-700 focus:border-indigo-500 focus:shadow-md"
                        ></textarea>
                    </div>

                    {/* Member Emails Input */}
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

                    {/* List of Added Member Emails */}
                    <div className="mb-5">
                        <label className="mb-3 block text-base font-medium text-[#07074D]">
                            Added Members
                        </label>
                        {fields.map((field, index) => (
                            <div key={field.id} className="flex items-center mt-2">
                                <input
                                    type="text"
                                    value={field.email} // Show the email as non-editable
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

                    {/* Action Buttons */}
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
                            Create Group
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateGroup;