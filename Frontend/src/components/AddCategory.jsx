
import { Trash2 } from 'lucide-react';
import { IoMdClose } from "react-icons/io";
import { ENDPOINTS } from '../utils/Constants';
import ApiCallingContext from '../context/ApiCallingContext';
import { useState, useContext, useEffect} from 'react';
export default function AddCategory({categories, setShowAddCategory}){

    const { postRequest, deleteRequest} = useContext(ApiCallingContext);
    const [categoryName, setCategoryName] = useState('');

    const [userDefinedCategories, setUserDefinedCategories] = useState(Array.isArray(categories) ? categories.filter(category => !category.default) : []);



    const addCategory = async (category) => {
        if (!category.trim()) return; // prevent adding empty category
    
        try {
          const response = await postRequest(ENDPOINTS.ADD_CATEGORY, true, { category });
          if (response.data.result === "SUCCESS") {
            setUserDefinedCategories([...userDefinedCategories, response.data.data]);
            setCategoryName('');
          }
        } catch (error) {
          console.error("Error adding category:", error);
        }
      };

      const deleteCategory = async (categoryId) => {
        try {
            const response = await deleteRequest(`${ENDPOINTS.REMOVE_CATEGORY}?id=${categoryId}`, true);
            if (response.data.result === "SUCCESS") {
                setUserDefinedCategories(userDefinedCategories.filter(category => category.categoryId !== categoryId));
            }
        } catch (error) {
            console.error("Error deleting category:", error);
        }
    };



    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg shadow-lg p-6"> 
                <div className='mx-auto w-full max-w-[550px]'>
                    <div className='flex justify-between items-center'>
                        <h2 className="text-xl font-semibold mb-4">Edit Categories</h2>
                        <button
                            onClick={() => setShowAddCategory(false)} 
                            className="relative bottom-2  text-grey-500 hover:text-grey-700 p-1 rounded-full">
                            <IoMdClose className="w-5 h-4" /> 
                        </button>
                    </div>
                    <form className='space-y-4'>
                        <div className='mb-5'>
                            <label className='mb-3 block text-base font-medium text-[#07074D]'>
                                Category Name
                            </label>
                            <div className="flex items-center mt-2">
                            <input
                                type="string"
                                placeholder="Enter Category Name"
                                value={categoryName}
                                onChange={(e) => setCategoryName(e.target.value)}
                                className="w-full rounded-md border border-gray-300 p-3 text-base text-gray-700 focus:border-indigo-500 focus:shadow-md"/>
                            <button
                                type="button"
                                onClick={() => addCategory(categoryName)}
                                className="ml-2 bg-gray-200 text-gray-700 px-5 py-2 rounded-md hover:bg-gray-300">
                                Add
                            </button>
                        </div>
                        </div>
                    </form>
                </div>
                <div className="space-y-4">
                {Array.isArray(userDefinedCategories) && userDefinedCategories.map(
                            (category) => (
                                <div key={category.category} className="flex justify-between items-center border border-gray-200 p-4 rounded-md">
                                    <span>{category.category}</span>
                                    <button
                                        onClick={() => deleteCategory(category.categoryId)}
                                        className="text-red-600 hover:underline">
                                        <Trash2 className="text-red-500" /> 
                                    </button>
                                </div>
                            )
                        )
                    }
                </div>
            </div>
        </div>

    );
}
