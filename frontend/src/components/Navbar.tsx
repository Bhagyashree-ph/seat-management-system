import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";

import { logout } from "../redux/slices/authSlice";

const Navbar = () => {

    const dispatch = useDispatch();

    const navigate = useNavigate();

    const handleLogout = () => {

        dispatch(logout());

        navigate("/login");
    };

    return (
        <div
            style={{
                height: "60px",
                background: "#1e293b",
                color: "white",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 20px",
                fontWeight: "bold"
            }}
        >

            <div>
                Seat Management System
            </div>

            <button
                onClick={handleLogout}
                style={{
                    padding: "8px 12px",
                    cursor: "pointer"
                }}
            >
                Logout
            </button>

        </div>
    );
};

export default Navbar;