import { Link } from "react-router-dom";

export default function Home() {
  return (
    <div style={{
      minHeight: "100vh",
      background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      color: "white",
      fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif"
    }}>
      {/* Header */}
      <div style={{ textAlign: "center", marginBottom: "60px" }}>
        <h1 style={{
          fontSize: "4rem",
          fontWeight: "bold",
          margin: "0",
          textShadow: "2px 2px 4px rgba(0,0,0,0.3)"
        }}>🎟️ FastTicketing</h1>
        <p style={{
          fontSize: "1.5rem", 
          margin: "20px 0",
          fontWeight: "300",
          opacity: 0.9
        }}>빠르고 안전한 온라인 티켓 예매</p>
      </div>

      {/* Main Navigation Cards */}
      <div style={{
        display: "flex",
        gap: "40px",
        flexWrap: "wrap",
        justifyContent: "center"
      }}>
        <Link to="/ticketings" style={{
          textDecoration: "none",
          color: "inherit"
        }}>
          <div style={{
            background: "rgba(255,255,255,0.2)",
            backdropFilter: "blur(10px)",
            borderRadius: "20px",
            padding: "40px 60px",
            textAlign: "center",
            border: "1px solid rgba(255,255,255,0.3)",
            transition: "all 0.3s ease",
            cursor: "pointer",
            minWidth: "280px"
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = "translateY(-10px)";
            e.currentTarget.style.boxShadow = "0 20px 40px rgba(0,0,0,0.2)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = "translateY(0)";
            e.currentTarget.style.boxShadow = "none";
          }}>
            <div style={{ fontSize: "3rem", marginBottom: "20px" }}>🎭</div>
            <h2 style={{ 
              fontSize: "1.8rem", 
              margin: "0 0 15px 0",
              fontWeight: "600"
            }}>티켓 예매</h2>
            <p style={{
              fontSize: "1.1rem",
              margin: "0",
              opacity: 0.8,
              lineHeight: "1.4"
            }}>공연, 콘서트, 연극<br/>원하는 좌석을 선택하세요</p>
          </div>
        </Link>

        <Link to="/admin" style={{
          textDecoration: "none",
          color: "inherit"
        }}>
          <div style={{
            background: "rgba(255,255,255,0.2)",
            backdropFilter: "blur(10px)",
            borderRadius: "20px",
            padding: "40px 60px",
            textAlign: "center",
            border: "1px solid rgba(255,255,255,0.3)",
            transition: "all 0.3s ease",
            cursor: "pointer",
            minWidth: "280px"
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = "translateY(-10px)";
            e.currentTarget.style.boxShadow = "0 20px 40px rgba(0,0,0,0.2)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = "translateY(0)";
            e.currentTarget.style.boxShadow = "none";
          }}>
            <div style={{ fontSize: "3rem", marginBottom: "20px" }}>⚙️</div>
            <h2 style={{ 
              fontSize: "1.8rem", 
              margin: "0 0 15px 0",
              fontWeight: "600"
            }}>관리자</h2>
            <p style={{
              fontSize: "1.1rem",
              margin: "0",
              opacity: 0.8,
              lineHeight: "1.4"
            }}>공연장, 공연, 일정<br/>티켓 오픈 관리</p>
          </div>
        </Link>
      </div>

      {/* Features Section */}
      <div style={{
        marginTop: "80px",
        display: "flex",
        gap: "30px",
        flexWrap: "wrap",
        justifyContent: "center",
        maxWidth: "800px"
      }}>
        <div style={{ textAlign: "center", flex: "0 0 200px" }}>
          <div style={{ fontSize: "2rem", marginBottom: "10px" }}>⚡</div>
          <h3 style={{ fontSize: "1.2rem", margin: "0 0 10px 0" }}>빠른 예매</h3>
          <p style={{ fontSize: "0.9rem", opacity: 0.8, margin: 0 }}>대기열 시스템으로<br/>공정한 예매</p>
        </div>
        <div style={{ textAlign: "center", flex: "0 0 200px" }}>
          <div style={{ fontSize: "2rem", marginBottom: "10px" }}>🔒</div>
          <h3 style={{ fontSize: "1.2rem", margin: "0 0 10px 0" }}>안전한 결제</h3>
          <p style={{ fontSize: "0.9rem", opacity: 0.8, margin: 0 }}>보안 결제 시스템으로<br/>안심 예매</p>
        </div>
        <div style={{ textAlign: "center", flex: "0 0 200px" }}>
          <div style={{ fontSize: "2rem", marginBottom: "10px" }}>🎯</div>
          <h3 style={{ fontSize: "1.2rem", margin: "0 0 10px 0" }}>좌석 선택</h3>
          <p style={{ fontSize: "0.9rem", opacity: 0.8, margin: 0 }}>실시간 좌석 현황<br/>원하는 자리 선택</p>
        </div>
      </div>
    </div>
  );
}
