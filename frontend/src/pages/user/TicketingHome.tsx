import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { isAuthenticated, getAuthUser } from "../../utils/auth";

type TicketOpen = {
  id: number;
  scheduleId: number;
  openAt: string;
  limitPerUser: number;
  openType: string;
};

export default function TicketingHome() {
  const [list, setList] = useState<TicketOpen[]>([]);
  const [loadingId, setLoadingId] = useState<number | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  // 로그인 체크
  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/login");
      return;
    }
  }, [navigate]);

  useEffect(() => {
    if (isAuthenticated()) {
      axios.get("/api/ticket-opens").then((res) => {
        setList(res.data);
      });
    }
  }, []);

  const goToQueuePage = (ticketOpenId: number) => {
    const queueToken = crypto.randomUUID();
    localStorage.setItem("queueToken", queueToken);
    localStorage.setItem("ticketOpenId", ticketOpenId.toString());
    setLoadingId(ticketOpenId);
    
    // 큐 진입 페이지를 새창으로 열기
    const queueWindow = window.open(
      "/queue/status",
      "_blank",
      "width=800,height=900,resizable=yes,scrollbars=yes"
    );
    
    if (!queueWindow) {
      alert("팝업 차단이 되어 있습니다. 팝업을 허용해주세요.");
      setLoadingId(null);
    } else {
      // 새창이 성공적으로 열렸으면 로딩 상태 해제
      setTimeout(() => setLoadingId(null), 1000);
    }
  };

  // React Router state를 사용하여 직접 좌석 선택으로 이동 (데모용)
  const goDirectToSeatSelect = (ticketOpen: TicketOpen) => {
    navigate("/select-seat", {
      state: {
        scheduleId: ticketOpen.scheduleId,
        ticketOpenId: ticketOpen.id,
        eventName: `공연 #${ticketOpen.scheduleId}`,
        venue: "FastTicketing 콘서트홀",
        date: formatDateTime(ticketOpen.openAt).date,
        time: formatDateTime(ticketOpen.openAt).time
      }
    });
  };

  const isBeforeOpen = (openAt: string) => {
    return new Date() < new Date(openAt);
  };

  const formatDateTime = (dateStr: string) => {
    const date = new Date(dateStr);
    return {
      date: date.toLocaleDateString('ko-KR'),
      time: date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
    };
  };

  return (
    <div style={{
      minHeight: "100vh",
      background: "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)",
      padding: "40px 0"
    }}>

      {/* Main Content */}
      <div style={{
        maxWidth: "1200px",
        margin: "0 auto",
        padding: "0 20px"
      }}>
        <div style={{
          textAlign: "center",
          marginBottom: "40px"
        }}>
          <h1 style={{
            fontSize: "2.5rem",
            color: "#333",
            margin: "0 0 10px 0",
            fontWeight: "700"
          }}>🎭 현재 예매 가능한 공연</h1>
          <p style={{
            fontSize: "1.2rem",
            color: "#666",
            margin: "0"
          }}>원하는 공연을 선택하고 예매를 시작하세요</p>
        </div>

        {errorMessage && (
          <div style={{
            background: "#ffebee",
            border: "1px solid #e57373",
            borderRadius: "8px",
            padding: "16px",
            marginBottom: "20px",
            color: "#c62828",
            textAlign: "center"
          }}>
            {errorMessage}
          </div>
        )}

        {list.length === 0 ? (
          <div style={{
            textAlign: "center",
            padding: "60px 20px",
            background: "white",
            borderRadius: "12px",
            boxShadow: "0 4px 6px rgba(0,0,0,0.1)"
          }}>
            <div style={{ fontSize: "4rem", marginBottom: "20px" }}>🎪</div>
            <h3 style={{ fontSize: "1.5rem", color: "#666", margin: "0 0 10px 0" }}>
              현재 예매 가능한 공연이 없습니다
            </h3>
            <p style={{ color: "#999", margin: "0" }}>
              새로운 공연이 등록되면 알려드리겠습니다
            </p>
          </div>
        ) : (
          <div style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fill, minmax(400px, 1fr))",
            gap: "24px"
          }}>
            {list.map((item) => {
              const beforeOpen = isBeforeOpen(item.openAt);
              const { date, time } = formatDateTime(item.openAt);

              return (
                <div key={item.id} style={{
                  background: "white",
                  borderRadius: "16px",
                  boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
                  overflow: "hidden",
                  transition: "transform 0.2s, box-shadow 0.2s"
                }} onMouseEnter={(e) => {
                  e.currentTarget.style.transform = "translateY(-4px)";
                  e.currentTarget.style.boxShadow = "0 8px 24px rgba(0,0,0,0.15)";
                }} onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "translateY(0)";
                  e.currentTarget.style.boxShadow = "0 4px 12px rgba(0,0,0,0.1)";
                }}>
                  {/* Performance Header */}
                  <div style={{
                    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                    color: "white",
                    padding: "24px",
                    textAlign: "center"
                  }}>
                    <div style={{ fontSize: "2.5rem", marginBottom: "8px" }}>🎭</div>
                    <h3 style={{
                      fontSize: "1.5rem",
                      margin: "0 0 8px 0",
                      fontWeight: "600"
                    }}>
                      공연 #{item.scheduleId}
                    </h3>
                    <div style={{
                      fontSize: "0.9rem",
                      opacity: 0.9
                    }}>
                      {item.openType === "FCFS" ? "선착순" : "일반"} 예매
                    </div>
                  </div>

                  {/* Performance Details */}
                  <div style={{ padding: "24px" }}>
                    <div style={{
                      display: "flex",
                      justifyContent: "space-between",
                      marginBottom: "20px"
                    }}>
                      <div>
                        <div style={{
                          fontSize: "0.9rem",
                          color: "#666",
                          marginBottom: "4px"
                        }}>예매 시작</div>
                        <div style={{
                          fontSize: "1.1rem",
                          fontWeight: "600",
                          color: "#333"
                        }}>
                          {date}
                        </div>
                        <div style={{
                          fontSize: "1.1rem",
                          fontWeight: "600",
                          color: "#667eea"
                        }}>
                          {time}
                        </div>
                      </div>
                      <div style={{ textAlign: "right" }}>
                        <div style={{
                          fontSize: "0.9rem",
                          color: "#666",
                          marginBottom: "4px"
                        }}>예매 한도</div>
                        <div style={{
                          fontSize: "1.5rem",
                          fontWeight: "700",
                          color: "#333"
                        }}>
                          {item.limitPerUser}매
                        </div>
                      </div>
                    </div>

                    {/* Status Badge */}
                    <div style={{
                      display: "flex",
                      justifyContent: "center",
                      marginBottom: "20px"
                    }}>
                      <span style={{
                        padding: "8px 16px",
                        borderRadius: "20px",
                        fontSize: "0.9rem",
                        fontWeight: "500",
                        background: beforeOpen ? "#fff3e0" : "#e8f5e9",
                        color: beforeOpen ? "#f57c00" : "#2e7d32",
                        border: `1px solid ${beforeOpen ? "#ffcc02" : "#4caf50"}`
                      }}>
                        {beforeOpen ? "⏰ 예매 대기 중" : "✅ 예매 가능"}
                      </span>
                    </div>

                    {/* Action Buttons */}
                    <div style={{ display: "flex", gap: "12px", flexDirection: "column" }}>
                      <button
                        onClick={() => goToQueuePage(item.id)}
                        disabled={loadingId === item.id || beforeOpen}
                        style={{
                          width: "100%",
                          padding: "16px",
                          fontSize: "1.1rem",
                          fontWeight: "600",
                          border: "none",
                          borderRadius: "12px",
                          cursor: beforeOpen ? "not-allowed" : "pointer",
                          transition: "all 0.2s",
                          background: beforeOpen 
                            ? "#f5f5f5" 
                            : loadingId === item.id 
                              ? "#ddd" 
                              : "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
                          color: beforeOpen ? "#999" : "white"
                        }}
                      >
                        {loadingId === item.id
                          ? "🔄 대기열 창 열기 중..."
                          : beforeOpen
                          ? "예매 시작 전"
                          : "🎫 새창에서 대기열 입장"}
                      </button>
                      
                      <button
                        onClick={() => goDirectToSeatSelect(item)}
                        disabled={beforeOpen}
                        style={{
                          width: "100%",
                          padding: "12px",
                          fontSize: "0.95rem",
                          fontWeight: "500",
                          border: "2px solid #667eea",
                          borderRadius: "12px",
                          cursor: beforeOpen ? "not-allowed" : "pointer",
                          transition: "all 0.2s",
                          background: beforeOpen ? "#f5f5f5" : "white",
                          color: beforeOpen ? "#999" : "#667eea"
                        }}
                      >
                        {beforeOpen ? "예매 시작 전" : "⚡ 바로 좌석 선택 (데모)"}
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
