import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "./ui/button";
import { getAuthUser, logout, isAuthenticated } from "../utils/auth";

export default function Header() {
  const [user, setUser] = useState(getAuthUser());
  const navigate = useNavigate();

  // 로그인 상태 변경 감지
  useEffect(() => {
    const handleStorageChange = () => {
      setUser(getAuthUser());
    };

    // localStorage 변경 감지
    window.addEventListener('storage', handleStorageChange);
    
    // 컴포넌트 마운트/언마운트 시에도 체크
    const interval = setInterval(() => {
      setUser(getAuthUser());
    }, 1000);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      clearInterval(interval);
    };
  }, []);

  const handleLogout = () => {
    logout();
    setUser(null);
    navigate("/");
  };

  return (
    <div
      style={{
        background: "white",
        boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
        padding: "16px 0",
        position: "sticky",
        top: 0,
        zIndex: 1000,
      }}
    >
      <div
        style={{
          maxWidth: "1200px",
          margin: "0 auto",
          padding: "0 20px",
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
        }}
      >
        {/* Logo */}
        <Link
          to="/"
          style={{
            textDecoration: "none",
            color: "#333",
            fontSize: "1.8rem",
            fontWeight: "bold",
            display: "flex",
            alignItems: "center",
            gap: "8px",
          }}
        >
          🎟️ FastTicketing
        </Link>

        {/* Navigation */}
        <nav style={{ display: "flex", alignItems: "center", gap: "16px" }}>
          <Link
            to="/ticketings"
            style={{
              textDecoration: "none",
              color: "#666",
              fontSize: "1rem",
              padding: "8px 16px",
              borderRadius: "4px",
              transition: "all 0.2s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "#f5f5f5";
              e.currentTarget.style.color = "#333";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "transparent";
              e.currentTarget.style.color = "#666";
            }}
          >
            티켓 예매
          </Link>

          {/* Auth Section */}
          {user ? (
            <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
              <div
                style={{
                  background: "#f8f9ff",
                  padding: "8px 12px",
                  borderRadius: "8px",
                  fontSize: "0.9rem",
                  color: "#667eea",
                  fontWeight: "500",
                }}
              >
                👤 사용자 #{user.userId}
              </div>
              <Button
                variant="outline"
                onClick={handleLogout}
                style={{
                  fontSize: "0.9rem",
                  padding: "8px 16px",
                }}
              >
                로그아웃
              </Button>
            </div>
          ) : (
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Link to="/login">
                <Button
                  style={{
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    color: "white",
                    border: "none",
                    fontSize: "0.9rem",
                    padding: "8px 16px",
                  }}
                >
                  로그인
                </Button>
              </Link>
            </div>
          )}
        </nav>
      </div>
    </div>
  );
}