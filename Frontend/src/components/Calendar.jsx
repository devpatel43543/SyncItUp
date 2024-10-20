// // Calendar.jsx
// import React, { useRef, useEffect } from 'react';
// import dayjs from 'dayjs';
//
// // eslint-disable-next-line react/prop-types
// const Calendar = ({ year, month, onDateSelect, onPrevMonth, onNextMonth, onClose }) => {
//     const calendarRef = useRef(null); // Create a ref for the calendar
//
//     const generateCalendar = (year, month) => {
//         const daysInMonth = dayjs(`${year}-${month + 1}`, 'YYYY-MM').daysInMonth();
//         const firstDayOfWeek = dayjs(`${year}-${month + 1}-01`).day();
//
//         const calendarDays = [];
//         for (let i = 0; i < firstDayOfWeek; i++) {
//             calendarDays.push(null);
//         }
//         for (let day = 1; day <= daysInMonth; day++) {
//             calendarDays.push(day);
//         }
//
//         return calendarDays;
//     };
//
//     useEffect(() => {
//         // Function to handle click outside of the calendar
//         const handleClickOutside = (event) => {
//             if (calendarRef.current && !calendarRef.current.contains(event.target)) {
//                 onClose(); // Call the onClose function passed as prop
//             }
//         };
//
//         // Add event listener
//         document.addEventListener('mousedown', handleClickOutside);
//         return () => {
//             // Cleanup event listener on component unmount
//             document.removeEventListener('mousedown', handleClickOutside);
//         };
//     }, [onClose]);
//
//     return (
//         <div className="bg-white shadow-lg rounded-lg overflow-hidden w-80" ref={calendarRef}>
//             <div className="flex items-center justify-between px-4 py-2 bg-gray-700">
//                 <button onClick={onPrevMonth} className="text-white text-sm">Previous</button>
//                 <h2 className="text-white text-lg">{dayjs(`${year}-${month + 1}`).format('MMMM YYYY')}</h2>
//                 <button onClick={onNextMonth} className="text-white text-sm">Next</button>
//             </div>
//             <div className="grid grid-cols-7 gap-1 p-2">
//                 {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day, index) => (
//                     <div key={index} className="text-center font-semibold text-sm">{day}</div>
//                 ))}
//                 {generateCalendar(year, month).map((day, index) => (
//                     <div
//                         key={index}
//                         className={`text-center py-1 border cursor-pointer ${day ? '' : 'invisible'} text-sm`}
//                         onClick={() => {
//                             if (day) {
//                                 const selected = new Date(year, month, day);
//                                 onDateSelect(selected);
//                             }
//                         }}
//                     >
//                         {day}
//                     </div>
//                 ))}
//             </div>
//         </div>
//     );
// };
//
// export default Calendar;


