// import React, { useContext, useState ,useEffect} from 'react'
// import PersonalExpenseContext from './PersonalExpenseContext'
// import AxiosContext from '../AxiosContext'
// import { ENDPOINTS } from '../../utils/Constants'
// import { HttpStatusCode } from 'axios'

// function PersonalExpenseContextProvider(props) {
//     const { getRequest,postRequest } = useContext(AxiosContext)
//     const[expenses,setExpenses] = useState([])


//     const getAllExpense = async () => {
//         console.log("Fetching expenses..."); // Debugging statement
//         try {
//           const response = await getRequest(ENDPOINTS.ALL_PERSONAL_EXPENSE,true);
//           if (response.status === HttpStatusCode.Accepted) {
//             console.log("Expenses fetched:", response.data); // Debugging statement
//             setExpenses(response.data);
//             return response.data;
//           }else{
//             console.log("somthing went wrong")
//           }
//         } catch (error) {
//           console.error("Error fetching expenses:", error);
//         }
//       };

//       useEffect(() => {

//         console.log("PersonalExpenseContextProvider mounted"); // Debugging statement
//         getAllExpense();
//       }, []);

//   return (
//     <PersonalExpenseContext.Provider value={{getAllExpense,expenses}}>
//         {props.children}
//     </PersonalExpenseContext.Provider>
//   )
// }

// export default PersonalExpenseContextProvider