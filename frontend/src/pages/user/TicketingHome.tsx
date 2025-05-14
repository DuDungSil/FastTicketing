import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

type TicketOpen = {
  id: number;
  scheduleId: number;
  openAt: string; // ISO 문자열
  limitPerUser: number;
  openType: string;
};

export default function TicketingHome() {
  const [list, setList] = useState<TicketOpen[]>([]);
  const [loadingId, setLoadingId] = useState<number | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    axios.get("/api/ticket-opens").then((res) => {
      setList(res.data);
    });
  }, []);

  const isAlreadyInQueue = (ticketOpenId: number) => {
    const storedToken = localStorage.getItem("queueToken");
    const storedId = localStorage.getItem("ticketOpenId");
    return storedToken && storedId === ticketOpenId.toString();
  };

  const enterQueue = async (ticketOpenId: number) => {
    const queueToken = crypto.randomUUID();
    setLoadingId(ticketOpenId);
    setErrorMessage(null);

    try {
      await axios.post("/api/queue/enter", null, {
        params: { queueToken, ticketOpenId },
      });

      localStorage.setItem("queueToken", queueToken);
      localStorage.setItem("ticketOpenId", ticketOpenId.toString());

      navigate("/queue/status");
    } catch {
      setErrorMessage("대기열 입장 중 오류가 발생했습니다.");
    } finally {
      setLoadingId(null);
    }
  };

  const isBeforeOpen = (openAt: string) => {
    const now = new Date();
    const openTime = new Date(openAt);
    return now < openTime;
  };

  return (
    <div>
      <h2>🎟️ 예매 가능한 공연 목록</h2>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <ul>
        {list.map((item) => {
          const alreadyInQueue = isAlreadyInQueue(item.id);
          const beforeOpen = isBeforeOpen(item.openAt);

          return (
            <li key={item.id} style={{ marginBottom: "10px" }}>
              <strong>공연 #{item.scheduleId}</strong> / 제한:{" "}
              {item.limitPerUser}매 / 시작:{" "}
              {new Date(item.openAt).toLocaleString()}
              <button
                onClick={() => enterQueue(item.id)}
                style={{ marginLeft: "10px" }}
                disabled={
                  loadingId === item.id || !!beforeOpen || !!alreadyInQueue
                }
              >
                {loadingId === item.id
                  ? "입장 중..."
                  : beforeOpen
                  ? "예매 대기 중"
                  : alreadyInQueue
                  ? "입장 완료"
                  : "대기열 입장"}
              </button>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
