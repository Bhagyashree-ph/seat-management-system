import { ReactNode } from "react";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

type Props = {
    children: ReactNode;
};

const MainLayout = ({ children }: Props) => {

    return (
        <div
            style={{
                height: "100vh",
                display: "flex",
                flexDirection: "column"
            }}
        >
            <Navbar />

            <div
                style={{
                    flex: 1,
                    display: "flex"
                }}
            >
                <Sidebar />

                <div
                    style={{
                        flex: 1,
                        padding: "20px",
                        background: "#F8FAFC"
                    }}
                >
                    {children}
                </div>
            </div>
        </div>
    );
};

export default MainLayout;