import React,{ useState ,useEffect} from "react";
import { useNavigate } from "react-router-dom";
import { AUTH_TOKEN } from "../utils/Constants";
import AuthContext from "./AuthContext";
import { jwtDecode } from "jwt-decode";

const AuthContextProvider = (props) => {
    const now = new Date();
    const [authToken, setAuthToken] = useState(null);
    const [loggedInUserEmail, setLoggedInUserEmail] = useState(null);
    const [loading,setLoading] = useState(true)
    const navigate = useNavigate()

    const storeAuthToken = (token)=>{
        console.log(token)
        localStorage.setItem(AUTH_TOKEN,token)
        setAuthToken(token)
        
    }
    const deleteAuthToken = ()=>{
        localStorage.removeItem(AUTH_TOKEN)
        console.log("check navigation",now.toLocaleString())     
        setAuthToken(null)
        setLoggedInUserEmail(null)
        console.log("check authTokenStatus",authToken,now.toLocaleString())

    }

    useEffect(()=>{
            console.log("i am calling useEffect",now.toLocaleString())
            //better way of doing this to keep errors in mind
            try{
                const authToken = localStorage.getItem(AUTH_TOKEN);
                if (authToken) {
                    // setAuthToken(authToken);
                    console.log("decodign the token",authToken)
                    const decodeToken = jwtDecode(authToken);
                    console.log("decoded token",decodeToken)
                    setLoggedInUserEmail(decodeToken.sub);
                    setAuthToken(authToken);
                    console.log("seted the login user",loggedInUserEmail)
                }
                if(authToken == null){
                    console.log("loggedInUserEmail before",now.toLocaleString())
                    setLoggedInUserEmail(null)
                    console.log("loggedInUserEmail after",loggedInUserEmail,now.toLocaleString())

                }
            }catch(error){
                console.error('Failed to decode token', error);
            }finally{
                console.log("setting loading to false");
                setLoading(false); // Ensure loading is set to false
                console.log("seted loading to false")
            }
            /*

            console.log("i am calling useEffect",now.toLocaleString())
            const authToken = localStorage.getItem(AUTH_TOKEN_KEY);
            if (authToken) {
                // setAuthToken(authToken);
                console.log("decodign the token",authToken)
                const decodeToken = jwtDecode(authToken);
                console.log("decoded token",decodeToken)
                setLoggedInUserEmail(decodeToken.sub);
                console.log("seted the login user")
            }
       
            console.log("setting loading to false");
            setLoading(false); // Ensure loading is set to false
            console.log("seted loading to false")

            */
    },[authToken])
  return (
    <AuthContext.Provider value={{authToken,setAuthToken,loggedInUserEmail,storeAuthToken,deleteAuthToken,loading}}>
        {props.children}
    </AuthContext.Provider>
  )
}

export default AuthContextProvider