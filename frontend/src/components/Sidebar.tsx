import { Link } from "react-router-dom";

const Sidebar = () => {
    return (
        <div
            style={{
                width: "220px",
                background: "#d7803d",
                color: "white",
                height: "100%",
                padding: "20px",
                boxSizing: "border-box"
            }}
        >
            <div style={{ marginBottom: "20px" }}>
                <Link
                    to="/dashboard"
                    style={{ color: "white" }}
                >
                    Dashboard
                </Link>
            </div>
        </div>
    );
};

export default Sidebar;