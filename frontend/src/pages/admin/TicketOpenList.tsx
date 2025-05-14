import { useEffect, useState } from "react";
import axios from "axios";

type TicketOpen = {
  id: number;
  scheduleId: number;
  openAt: string;
  limitPerUser: number;
  openType: string;
};

type ScheduleOption = {
  id: number;
  label: string;
};

type ScheduleDetailDto = {
  id: number;
  performanceTitle: string;
  venueName: string;
  hallName: string;
  startTime: string;
  endTime: string;
};

export default function TicketOpenList() {
  const [ticketOpens, setTicketOpens] = useState<TicketOpen[]>([]);
  const [schedules, setSchedules] = useState<ScheduleOption[]>([]);

  const [form, setForm] = useState({
    scheduleId: 1,
    openAt: "",
    limitPerUser: 1,
    openType: "NORMAL",
  });

  useEffect(() => {
    fetchTicketOpens();
    fetchSchedules();
  }, []);

  const fetchTicketOpens = async () => {
    const res = await axios.get("/api/ticket-opens");
    setTicketOpens(res.data);
  };

  const fetchSchedules = async () => {
    const res = await axios.get<ScheduleDetailDto[]>(
      "/api/performances/schedules"
    );

    const mapped = res.data.map((s) => ({
      id: s.id,
      label: `[${s.performanceTitle}] ${s.venueName} - ${s.hallName} | ${s.startTime}`,
    }));

    setSchedules(mapped);
  };

  const createTicketOpen = async () => {
    await axios.post("/api/ticket-opens", form);
    setForm({ ...form, openAt: "" });
    fetchTicketOpens();
  };

  const deleteTicketOpen = async (id: number) => {
    if (!confirm("정말 삭제할까요?")) return;
    await axios.delete(`/api/ticket-opens/${id}`);
    fetchTicketOpens();
  };

  return (
    <div>
      <h2>🎫 티켓 예매 정보 목록</h2>
      <ul>
        {ticketOpens.map((t) => (
          <li key={t.id}>
            Schedule #{t.scheduleId} | {t.openAt} | 제한: {t.limitPerUser} |{" "}
            {t.openType}
            <button
              onClick={() => deleteTicketOpen(t.id)}
              style={{ marginLeft: "10px", color: "red" }}
            >
              삭제
            </button>
          </li>
        ))}
      </ul>

      <h3>➕ 예매 정보 등록</h3>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "10px",
          maxWidth: "500px",
        }}
      >
        <label>
          🎬 스케줄 선택:
          <select
            value={form.scheduleId}
            onChange={(e) => setForm({ ...form, scheduleId: +e.target.value })}
          >
            {schedules.map((s) => (
              <option key={s.id} value={s.id}>
                {s.label}
              </option>
            ))}
          </select>
        </label>

        <label>
          ⏰ 티켓 오픈 시간:
          <input
            type="datetime-local"
            value={form.openAt}
            onChange={(e) => setForm({ ...form, openAt: e.target.value })}
          />
        </label>

        <label>
          👤 인당 예매 제한 수:
          <input
            type="number"
            value={form.limitPerUser}
            onChange={(e) =>
              setForm({ ...form, limitPerUser: +e.target.value })
            }
            placeholder="ex) 1, 2"
          />
        </label>

        <label>
          📌 예매 방식 (OpenType):
          <select
            value={form.openType}
            onChange={(e) => setForm({ ...form, openType: e.target.value })}
          >
            <option value="NORMAL">NORMAL (일반 오픈)</option>
            <option value="FIRST_COME">FIRST_COME (선착순)</option>
          </select>
        </label>

        <button onClick={createTicketOpen}>등록</button>
      </div>
    </div>
  );
}
