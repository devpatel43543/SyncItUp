import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css'
//import PersonalExpenseContextProvider from './context/personalExpense/PersonalExpenseContextProvider.jsx'
import ApiCallingContextProvider from './context/ApiCallingContextProvider.jsx'
import { BrowserRouter } from 'react-router-dom'
import AuthContextProvider from './context/AuthContextProvider.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
  <BrowserRouter>
<AuthContextProvider>

  <ApiCallingContextProvider>


    <App />
  </ApiCallingContextProvider>
</AuthContextProvider>
  </BrowserRouter>
  </StrictMode>,
)
