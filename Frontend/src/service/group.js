import { useContext } from "react"
import ApiCallingContext from "../context/ApiCallingContext"

const {postRequest} = useContext(ApiCallingContext)
export const createGroup = (data)=>{
    console.log("from createGroup ",data);
}