import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css'
//import PersonalExpenseContextProvider from './context/personalExpense/PersonalExpenseContextProvider.jsx'
import ApiCallingContextProvider from './context/ApiCallingContextProvider.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
  <ApiCallingContextProvider>


    <App />
  </ApiCallingContextProvider>
  </StrictMode>,
)
