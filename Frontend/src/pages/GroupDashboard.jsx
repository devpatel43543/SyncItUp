import React, { useContext, useEffect, useState } from 'react';
import ApiCallingContext from '../context/ApiCallingContext';
import { ENDPOINTS } from '../utils/Constants';
import { Users, MoreVertical, Edit, UserPlus, Trash2, Plus } from 'lucide-react';
import { frontEndRoutes } from '../utils/FrontendRoutes';
import { useNavigate, Link } from 'react-router-dom';

function GroupDashboard() {
    const { getRequest } = useContext(ApiCallingContext);
    const [groups, setGroups] = useState([]); 
    const [menuOpenGroupId, setMenuOpenGroupId] = useState(null); 
    const navigate = useNavigate()
    useEffect(() => {
        const fetchGroups = async () => {
            try {
                const response = await getRequest(ENDPOINTS.ALl_GROUP, true);
                console.log('Fetched groups response:', response.data);

                setGroups(Array.isArray(response.data.data) ? response.data.data : []);
            } catch (error) {
                console.error('Error fetching groups:', error);
            }
        };

        fetchGroups();
    }, [getRequest]);

    const toggleMenu = (groupId) => {
        setMenuOpenGroupId(menuOpenGroupId === groupId ? null : groupId);
    };
    const handleAddMembersClick = (groupId) => {
        navigate(`${frontEndRoutes.add_new_member}/${groupId}`);
    };
    const handleRemoveMemberClick = (groupId) => {
        navigate(`${frontEndRoutes.remove_member}/${groupId}`)
    }
    return (
        <div className="flex flex-col items-center p-4 sm:p-8 lg:p-12 min-h-screen">
        <div className="mx-auto w-full max-w-[1200px]">
            <div className="flex flex-col sm:flex-row justify-between items-center mb-8">
                <div className="text-center sm:text-left mb-4 sm:mb-0">
                    <h1 className="text-2xl sm:text-3xl font-bold text-gray-800">Group Expenses</h1>
                    <p className="text-gray-600 mt-1">Manage your shared expenses and groups</p>
                </div>
                <Link to={frontEndRoutes.createGroup} className="flex items-center bg-black text-white px-4 py-2 rounded-md hover:bg-gray-900">
                    <Plus className="mr-2 h-5 w-5" />
                    Create New Group
                </Link>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                {groups.map((group) => (
                    <div key={group.groupId} className="relative w-full rounded-lg border border-gray-200 bg-white p-6 shadow-sm hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-center mb-4">
                            <div>
                                <h2 className="text-xl font-semibold text-gray-900">{group.groupName}</h2>
                                <div className="flex items-center text-gray-500 mt-1">
                                    <Users className="mr-2 h-5 w-5" />
                                    {group.members.length} members
                                </div>
                            </div>
                            <div className="relative">
                                <button onClick={() => toggleMenu(group.groupId)} className="text-gray-500 hover:text-gray-700">
                                    <MoreVertical className="h-5 w-5" />
                                </button>
                                {menuOpenGroupId === group.groupId && (
                                    <div className="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-md shadow-lg z-10">
                                        <ul>
                                            
                                            <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer flex items-center gap-2"
                                                onClick={() => handleAddMembersClick(group.groupId)}
                                            >
                                                <UserPlus className="text-gray-500" /> Add Members
                                            </li>
                                            <li className="px-4 py-2 hover:bg-gray-100 cursor-pointer flex items-center gap-2 text-red-600"
                                                onClick={() => handleRemoveMemberClick(group.groupId)}
                                            >
                                                <Trash2 className="text-red-500" /> Remove Member
                                            </li>
                                        </ul>
                                    </div>
                                )}
                            </div>
                        </div>
                        <div>
                            <button className="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2 font-semibold text-gray-800 hover:bg-gray-100" onClick={() => navigate(`${frontEndRoutes.groupDetails}/${group.groupId}`)}>
                                View Details
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    </div>
    );
}

export default GroupDashboard;
