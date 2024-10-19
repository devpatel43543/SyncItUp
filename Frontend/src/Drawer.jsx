import React, { useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';

const Drawer = ({ isOpen, onClose }) => {
    const drawerRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (drawerRef.current && !drawerRef.current.contains(event.target)) {
                onClose();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onClose]);

    return (
        <div
            ref={drawerRef}
            className={`fixed top-0 left-0 h-full w-64 bg-[#3B2F2F] shadow-xl transform ${
                isOpen ? 'translate-x-0' : '-translate-x-full'
            } transition-transform duration-300 ease-in-out z-50 border-r border-gray-200`}
        >
            <div className="relative h-full p-6 text-white">
                <h2 className="text-3xl font-bold mb-6 text-[#987554]">Menu</h2>
                <ul className="grid grid-cols-1 gap-6">
                    <li className="p-4 rounded-lg transition bg-[#987554] text-white hover:bg-[#B89076]">
                        <Link to="/" className="text-xl hover:text-gray-200 transition-colors duration-200" onClick={onClose}>
                            Dashboard
                        </Link>
                    </li>
                    <li className="p-4 rounded-lg transition bg-[#987554] text-white hover:bg-[#B89076]">
                        <Link to="/addexpense" className="text-xl hover:text-gray-200 transition-colors duration-200" onClick={onClose}>
                            Add Expense
                        </Link>
                    </li>
                </ul>

                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 bg-[#987554] text-white hover:text-[#B89076] rounded-full p-2 shadow-lg transition hover:scale-110"
                >
                    ✕
                </button>
            </div>
        </div>
    );
};

export default Drawer;
