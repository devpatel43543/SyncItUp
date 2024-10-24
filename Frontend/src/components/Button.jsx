import React from "react";

function Button({
    children,
    type = "button",
    bgColor = "bg-purple-500",
    hover = "bg-purple-600",
    textColor = "text-white",
    className = "",
    isSubmitting,
    ...props
}) {
    return (
        isSubmitting ? (
            <>
                <div className="flex justify-between items-baseline">
                    <button
                        type="button"
                        className={` ${bgColor}  hover:${hover} ${className} rounded-lg`}
                        {...props}
                        disabled
                    >
                        <div className="flex items-center justify-center m-[10px]">
                            <div className="h-5 w-5 border-t-transparent border-solid animate-spin rounded-xl border-white border-4"></div>
                            <div className="ml-2"> {children}... </div>
                        </div>
                    </button>
                </div>
            </>
        ) : (
            <div className="flex justify-between items-baseline">
                <button
                    type="submit"
                    className={`mt-4 ${bgColor} hover:${hover} ${className} text-white py-2 px-6 rounded-md `}
                    {...props}
                >
                    {children}
                </button>
                {/* <Link to={"/signUp"}>don't have an account</Link> */}
            </div>
        )
    );
}

export default Button;
