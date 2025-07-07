import { Link } from "react-router-dom";
import UserGeneratorForm from "./UserGeneratorForm";

export default function AdminDashboard() {
  return (
    <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
      <header style={{ marginBottom: "30px", textAlign: "center" }}>
        <h1 style={{ color: "#2563eb", marginBottom: "10px" }}>🛠️ 관리자 대시보드</h1>
        <p style={{ color: "#6b7280" }}>티켓팅 시스템 관리</p>
      </header>

      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
        gap: "20px",
        marginTop: "30px"
      }}>
        <Link to="/admin/venues" style={{ textDecoration: "none" }}>
          <div style={{
            background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            color: "white",
            padding: "30px",
            borderRadius: "12px",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            transition: "transform 0.2s",
            cursor: "pointer"
          }}
          onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-2px)"}
          onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}>
            <div style={{ fontSize: "48px", marginBottom: "15px" }}>🎪</div>
            <h3 style={{ margin: "0 0 10px 0", fontSize: "20px" }}>공연장 관리</h3>
            <p style={{ margin: "0", opacity: "0.9" }}>공연장, 홀, 좌석 등록 및 관리</p>
          </div>
        </Link>

        <Link to="/admin/performances" style={{ textDecoration: "none" }}>
          <div style={{
            background: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
            color: "white",
            padding: "30px",
            borderRadius: "12px",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            transition: "transform 0.2s",
            cursor: "pointer"
          }}
          onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-2px)"}
          onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}>
            <div style={{ fontSize: "48px", marginBottom: "15px" }}>🎭</div>
            <h3 style={{ margin: "0 0 10px 0", fontSize: "20px" }}>공연 관리</h3>
            <p style={{ margin: "0", opacity: "0.9" }}>공연 등록 및 일정 관리</p>
          </div>
        </Link>

        <Link to="/admin/ticket-opens" style={{ textDecoration: "none" }}>
          <div style={{
            background: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
            color: "white",
            padding: "30px",
            borderRadius: "12px",
            boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            transition: "transform 0.2s",
            cursor: "pointer"
          }}
          onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-2px)"}
          onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}>
            <div style={{ fontSize: "48px", marginBottom: "15px" }}>🎫</div>
            <h3 style={{ margin: "0 0 10px 0", fontSize: "20px" }}>예매 관리</h3>
            <p style={{ margin: "0", opacity: "0.9" }}>예매 오픈 설정 및 관리</p>
          </div>
        </Link>

        <div style={{
          background: "linear-gradient(135deg, #a8e6cf 0%, #88c999 100%)",
          color: "white",
          padding: "30px",
          borderRadius: "12px",
          boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
          transition: "transform 0.2s",
        }}
        onMouseEnter={(e) => e.currentTarget.style.transform = "translateY(-2px)"}
        onMouseLeave={(e) => e.currentTarget.style.transform = "translateY(0)"}>
          <div style={{ fontSize: "48px", marginBottom: "15px" }}>👥</div>
          <h3 style={{ margin: "0 0 10px 0", fontSize: "20px" }}>더미 유저 생성</h3>
          <p style={{ margin: "0 0 15px 0", opacity: "0.9" }}>테스트용 더미 유저 데이터 생성</p>
          <UserGeneratorForm />
        </div>
      </div>

      <div style={{
        marginTop: "40px",
        padding: "20px",
        backgroundColor: "#f8fafc",
        borderRadius: "8px",
        border: "1px solid #e2e8f0"
      }}>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>📊 시스템 상태</h3>
        <div style={{ display: "flex", gap: "20px", flexWrap: "wrap" }}>
          <div style={{ flex: "1", minWidth: "150px" }}>
            <p style={{ margin: "5px 0", color: "#6b7280" }}>서버 상태</p>
            <p style={{ margin: "0", color: "#059669", fontWeight: "bold" }}>🟢 정상</p>
          </div>
          <div style={{ flex: "1", minWidth: "150px" }}>
            <p style={{ margin: "5px 0", color: "#6b7280" }}>데이터베이스</p>
            <p style={{ margin: "0", color: "#059669", fontWeight: "bold" }}>🟢 연결됨</p>
          </div>
          <div style={{ flex: "1", minWidth: "150px" }}>
            <p style={{ margin: "5px 0", color: "#6b7280" }}>Redis</p>
            <p style={{ margin: "0", color: "#059669", fontWeight: "bold" }}>🟢 연결됨</p>
          </div>
        </div>
      </div>
    </div>
  );
}
