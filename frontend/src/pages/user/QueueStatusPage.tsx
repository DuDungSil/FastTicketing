import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

type QueueStatus = {
  position: number;
  estimatedWaitSeconds: number;
  canEnter: boolean;
};

export default function QueueStatusPage() {
  const [status, setStatus] = useState<QueueStatus | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [dots, setDots] = useState("");
  const [isClosing, setIsClosing] = useState(false);
  const [countdown, setCountdown] = useState(3);
  const [hasOpenedSeatPage, setHasOpenedSeatPage] = useState(false); // 중복 실행 방지
  const navigate = useNavigate();

  const queueToken = localStorage.getItem("queueToken");
  const ticketOpenId = localStorage.getItem("ticketOpenId");

  // 로딩 애니메이션을 위한 점 애니메이션
  useEffect(() => {
    const interval = setInterval(() => {
      setDots((prev) => {
        if (prev === "...") return "";
        return prev + ".";
      });
    }, 500);

    return () => clearInterval(interval);
  }, []);

  // ✅ 대기열 퇴장 처리 함수
  const handleLeaveQueue = () => {
    if (!queueToken || !ticketOpenId) return;

    axios
      .post("/api/queue/leave", null, {
        params: {
          queueToken,
          ticketOpenId,
        },
      })
      .catch((err) => {
        console.warn("대기열 퇴장 실패", err);
      });
  };

  useEffect(() => {
    if (!queueToken || !ticketOpenId) {
      setErrorMessage("잘못된 접근입니다.");
      return;
    }

    // 대기열 진입 요청
    axios
      .post("/api/queue/enter", null, {
        params: {
          queueToken,
          ticketOpenId,
        },
      })
      .catch(() => {
        setErrorMessage("대기열 진입에 실패했습니다.");
      });

    // ✅ beforeunload 이벤트 등록
    window.addEventListener("beforeunload", handleLeaveQueue);

    // ✅ 언마운트 시에도 leave 처리
    return () => {
      handleLeaveQueue();
      window.removeEventListener("beforeunload", handleLeaveQueue);
    };
  }, [queueToken, ticketOpenId]);

  // 상태 주기적으로 확인
  useEffect(() => {
    if (!queueToken || !ticketOpenId) return;

    const interval = setInterval(() => {
      axios
        .get("/api/queue/status", {
          params: {
            queueToken,
            ticketOpenId,
          },
        })
        .then((res) => {
          const data = res.data;
          setStatus(data);

          if (data.canEnter && !hasOpenedSeatPage) {
            clearInterval(interval);
            setHasOpenedSeatPage(true); // 중복 실행 방지

            // 좌석 선택 페이지를 새창으로 열기
            const seatWindow = window.open(
              `/select-seat?ticketOpenId=${ticketOpenId}`,
              "_blank",
              "width=1400,height=900,resizable=yes,scrollbars=yes"
            );

            if (!seatWindow) {
              alert("팝업 차단이 되어 있습니다. 팝업을 허용해주세요.");
              setHasOpenedSeatPage(false); // 실패 시 다시 시도 가능하도록
            } else {
              // 닫힘 카운트다운 시작
              setIsClosing(true);
              
              const countdownInterval = setInterval(() => {
                setCountdown((prev) => {
                  if (prev <= 1) {
                    clearInterval(countdownInterval);
                    window.close();
                    return 0;
                  }
                  return prev - 1;
                });
              }, 1000);
            }
          }
        })
        .catch(() => {
          setErrorMessage("대기열 상태 조회 실패");
        });
    }, 3000);

    return () => clearInterval(interval);
  }, [queueToken, ticketOpenId]);

  const getProgressPercentage = () => {
    if (!status || status.position <= 0) return 100;
    // 대략적인 진행률 계산 (최대 1000명 기준)
    const maxQueue = Math.max(1000, status.position);
    return Math.max(5, ((maxQueue - status.position) / maxQueue) * 100);
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
      <div
        style={{
          backgroundColor: "white",
          borderRadius: "24px",
          padding: "48px",
          width: "100%",
          maxWidth: "600px",
          textAlign: "center",
          boxShadow: "0 20px 40px rgba(0,0,0,0.2)",
          position: "relative",
          overflow: "hidden",
        }}
      >
        {/* 배경 패턴 */}
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            right: 0,
            height: "120px",
            background: "linear-gradient(135deg, #f8f9ff 0%, #e8f0ff 100%)",
            borderRadius: "24px 24px 0 0",
          }}
        />

        {/* 메인 콘텐츠 */}
        <div style={{ position: "relative", zIndex: 1 }}>
          {/* 아이콘과 타이틀 */}
          <div
            style={{
              fontSize: "4rem",
              marginBottom: "16px",
              animation: "bounce 2s infinite",
            }}
          >
            🎫
          </div>
          <h1
            style={{
              fontSize: "2.2rem",
              color: "#333",
              margin: "0 0 8px 0",
              fontWeight: "700",
            }}
          >
            대기열에서 기다리는 중{dots}
          </h1>
          <p
            style={{
              fontSize: "1.1rem",
              color: "#666",
              margin: "0 0 40px 0",
              lineHeight: "1.5",
            }}
          >
            잠시만 기다려주세요. 곧 좌석 선택 페이지가 새창으로 열리고 이 창은 자동으로 닫힙니다.
          </p>

          {errorMessage ? (
            <div
              style={{
                background: "#ffebee",
                border: "2px solid #e57373",
                borderRadius: "16px",
                padding: "24px",
                color: "#c62828",
                fontSize: "1.1rem",
                fontWeight: "500",
              }}
            >
              ❌ {errorMessage}
            </div>
          ) : status ? (
            <div>
              {/* 진행률 바 */}
              <div
                style={{
                  background: "#f0f0f0",
                  borderRadius: "12px",
                  height: "12px",
                  marginBottom: "32px",
                  overflow: "hidden",
                }}
              >
                <div
                  style={{
                    background:
                      "linear-gradient(90deg, #667eea 0%, #764ba2 100%)",
                    height: "100%",
                    width: `${getProgressPercentage()}%`,
                    borderRadius: "12px",
                    transition: "width 0.5s ease",
                  }}
                />
              </div>

              {/* 대기 정보 */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-around",
                  marginBottom: "32px",
                }}
              >
                <div
                  style={{
                    background: "#f8f9ff",
                    borderRadius: "16px",
                    padding: "24px",
                    flex: 1,
                    margin: "0 8px",
                  }}
                >
                  <div
                    style={{
                      fontSize: "2.5rem",
                      fontWeight: "700",
                      color: "#667eea",
                      marginBottom: "8px",
                    }}
                  >
                    {status.position.toLocaleString()}
                  </div>
                  <div
                    style={{
                      fontSize: "1rem",
                      color: "#666",
                      fontWeight: "500",
                    }}
                  >
                    대기 순번
                  </div>
                </div>
              </div>

              {/* 상태 메시지 */}
              <div
                style={{
                  background: status.canEnter ? "#e8f5e9" : "#fff3e0",
                  border: `2px solid ${
                    status.canEnter ? "#4caf50" : "#ff9800"
                  }`,
                  borderRadius: "16px",
                  padding: "20px",
                  marginBottom: "24px",
                }}
              >
                <div
                  style={{
                    fontSize: "1.2rem",
                    fontWeight: "600",
                    color: status.canEnter ? "#2e7d32" : "#f57c00",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "8px",
                  }}
                >
                  {status.canEnter ? (
                    isClosing ? (
                      <>🎫 좌석 선택 페이지가 열렸습니다! {countdown}초 후 이 창이 자동으로 닫힙니다.</>
                    ) : (
                      <>✅ 입장 준비 완료! 좌석 선택 페이지를 새창으로 열고 있습니다...</>
                    )
                  ) : (
                    <>⏳ 대기 중... 조금만 더 기다려주세요.</>
                  )}
                </div>
              </div>

              {/* 팁 섹션 */}
              <div
                style={{
                  background: "#f8f9fa",
                  borderRadius: "12px",
                  padding: "16px",
                  fontSize: "0.9rem",
                  color: "#666",
                  lineHeight: "1.5",
                }}
              >
                💡 <strong>팁:</strong> 이 페이지를 새로고침하거나 닫지 마세요.
                대기열에서 제외될 수 있습니다.
              </div>
            </div>
          ) : (
            <div>
              {/* 로딩 상태 */}
              <div
                style={{
                  fontSize: "3rem",
                  marginBottom: "24px",
                }}
              >
                ⏳
              </div>
              <div
                style={{
                  fontSize: "1.3rem",
                  color: "#666",
                  fontWeight: "500",
                }}
              >
                대기열 정보를 불러오는 중{dots}
              </div>
              <div
                style={{
                  marginTop: "24px",
                  display: "flex",
                  justifyContent: "center",
                  gap: "8px",
                }}
              >
                {[0, 1, 2].map((i) => (
                  <div
                    key={i}
                    style={{
                      width: "12px",
                      height: "12px",
                      background: "#667eea",
                      borderRadius: "50%",
                      animation: `pulse 1.5s ease-in-out ${i * 0.2}s infinite`,
                    }}
                  />
                ))}
              </div>
            </div>
          )}
        </div>

        {/* CSS 애니메이션을 위한 스타일 태그 */}
        <style>{`
          @keyframes bounce {
            0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
            40% { transform: translateY(-10px); }
            60% { transform: translateY(-5px); }
          }
          @keyframes pulse {
            0%, 100% { opacity: 0.3; transform: scale(0.8); }
            50% { opacity: 1; transform: scale(1.2); }
          }
        `}</style>
      </div>
    </div>
  );
}
