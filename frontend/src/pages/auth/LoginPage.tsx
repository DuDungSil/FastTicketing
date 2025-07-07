import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card";
import { Button } from "../../components/ui/button";

export default function LoginPage() {
  const [userId, setUserId] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      // userId 유효성 검사
      const userIdNumber = parseInt(userId.trim());
      
      if (isNaN(userIdNumber) || userIdNumber <= 0) {
        setError("유효한 사용자 ID를 입력해주세요. (양의 정수)");
        return;
      }

      // localStorage에 로그인 정보 저장
      localStorage.setItem("isLoggedIn", "true");
      localStorage.setItem("userId", userIdNumber.toString());
      localStorage.setItem("loginTime", new Date().toISOString());

      // 메인 페이지로 이동
      navigate("/");
    } catch (error) {
      setError("로그인 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        padding: "20px",
      }}
    >
      <Card style={{ width: "100%", maxWidth: "400px" }}>
        <CardHeader style={{ textAlign: "center" }}>
          <div style={{ fontSize: "3rem", marginBottom: "16px" }}>🎟️</div>
          <CardTitle style={{ fontSize: "2rem", color: "#333" }}>
            FastTicketing
          </CardTitle>
          <p style={{ color: "#666", marginTop: "8px" }}>
            로그인하여 티켓 예매를 시작하세요
          </p>
        </CardHeader>

        <CardContent>
          <form onSubmit={handleLogin} style={{ display: "flex", flexDirection: "column", gap: "20px" }}>
            <div>
              <label
                htmlFor="userId"
                style={{
                  display: "block",
                  marginBottom: "8px",
                  fontSize: "0.9rem",
                  fontWeight: "500",
                  color: "#333",
                }}
              >
                사용자 ID
              </label>
              <input
                id="userId"
                type="number"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                placeholder="사용자 ID를 입력하세요 (예: 12345)"
                required
                min="1"
                style={{
                  width: "100%",
                  padding: "12px",
                  border: "2px solid #e1e5e9",
                  borderRadius: "8px",
                  fontSize: "1rem",
                  outline: "none",
                  transition: "border-color 0.2s",
                }}
                onFocus={(e) => (e.target.style.borderColor = "#667eea")}
                onBlur={(e) => (e.target.style.borderColor = "#e1e5e9")}
              />
              <div style={{ fontSize: "0.8rem", color: "#666", marginTop: "4px" }}>
                💡 데모용: 아무 숫자나 입력하세요 (예: 1, 100, 12345)
              </div>
            </div>

            {error && (
              <div
                style={{
                  background: "#ffebee",
                  border: "1px solid #e57373",
                  borderRadius: "8px",
                  padding: "12px",
                  color: "#c62828",
                  fontSize: "0.9rem",
                }}
              >
                ❌ {error}
              </div>
            )}

            <Button
              type="submit"
              disabled={isLoading || !userId.trim()}
              style={{
                width: "100%",
                padding: "12px",
                fontSize: "1rem",
                fontWeight: "600",
                background: isLoading || !userId.trim() 
                  ? "#ccc" 
                  : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                color: "white",
                border: "none",
                borderRadius: "8px",
                cursor: isLoading || !userId.trim() ? "not-allowed" : "pointer",
                transition: "all 0.2s",
              }}
            >
              {isLoading ? "로그인 중..." : "로그인"}
            </Button>
          </form>

          <div style={{ textAlign: "center", marginTop: "24px" }}>
            <Link
              to="/"
              style={{
                color: "#667eea",
                textDecoration: "none",
                fontSize: "0.9rem",
              }}
            >
              ← 홈으로 돌아가기
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}