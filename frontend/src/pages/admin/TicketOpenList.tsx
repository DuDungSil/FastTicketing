import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
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
    if (!form.openAt) {
      alert('예매 오픈 시간을 설정해주세요.');
      return;
    }
    if (form.limitPerUser < 1) {
      alert('인당 예매 제한 수는 1 이상이어야 합니다.');
      return;
    }
    
    try {
      await axios.post("/api/ticket-opens", form);
      setForm({ 
        scheduleId: schedules.length > 0 ? schedules[0].id : 1, 
        openAt: "", 
        limitPerUser: 1, 
        openType: "NORMAL" 
      });
      fetchTicketOpens();
    } catch (error) {
      console.error('Failed to create ticket open:', error);
      alert('예매 오픈 등록에 실패했습니다.');
    }
  };

  const deleteTicketOpen = async (id: number) => {
    if (!confirm("정말 삭제할까요?")) return;
    try {
      await axios.delete(`/api/ticket-opens/${id}`);
      fetchTicketOpens();
    } catch (error) {
      console.error('Failed to delete ticket open:', error);
      alert('예매 오픈 삭제에 실패했습니다.');
    }
  };

  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getScheduleInfo = (scheduleId: number) => {
    const schedule = schedules.find(s => s.id === scheduleId);
    return schedule ? schedule.label : `스케줄 #${scheduleId}`;
  };

  const getOpenTypeLabel = (openType: string) => {
    switch (openType) {
      case 'NORMAL': return '일반 오픈';
      case 'FIRST_COME': return '선착순';
      default: return openType;
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1200px", margin: "0 auto" }}>
      {/* 네비게이션 */}
      <div style={{ marginBottom: "20px" }}>
        <Link 
          to="/admin" 
          style={{ 
            color: "#6b7280", 
            textDecoration: "none",
            fontSize: "14px"
          }}
        >
          ← 관리자 대시보드로 돌아가기
        </Link>
      </div>

      <header style={{ marginBottom: "30px" }}>
        <h2 style={{ color: "#1f2937", marginBottom: "10px" }}>🎫 예매 오픈 관리</h2>
        <p style={{ color: "#6b7280" }}>공연 예매 오픈 설정을 관리하세요</p>
      </header>

      {/* 예매 오픈 등록 폼 */}
      <div style={{
        backgroundColor: "#f8fafc",
        padding: "20px",
        borderRadius: "8px",
        border: "1px solid #e2e8f0",
        marginBottom: "30px"
      }}>
        <h3 style={{ color: "#374151", marginBottom: "20px" }}>➕ 새 예매 오픈 등록</h3>
        
        <div style={{ display: "grid", gap: "20px" }}>
          {/* 스케줄 선택 */}
          <div>
            <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", color: "#374151", fontWeight: "500" }}>
              🎆 공연 스케줄 선택
            </label>
            <select
              value={form.scheduleId}
              onChange={(e) => setForm({ ...form, scheduleId: +e.target.value })}
              style={{
                width: "100%",
                padding: "12px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
                backgroundColor: "white"
              }}
            >
              {schedules.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.label}
                </option>
              ))}
            </select>
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr", gap: "15px" }}>
            {/* 오픈 시간 */}
            <div>
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", color: "#374151", fontWeight: "500" }}>
                ⏰ 예매 오픈 시간
              </label>
              <input
                type="datetime-local"
                value={form.openAt}
                onChange={(e) => setForm({ ...form, openAt: e.target.value })}
                style={{
                  width: "100%",
                  padding: "12px",
                  border: "1px solid #d1d5db",
                  borderRadius: "6px",
                  fontSize: "14px"
                }}
              />
            </div>

            {/* 인당 제한 */}
            <div>
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", color: "#374151", fontWeight: "500" }}>
                👤 인당 제한
              </label>
              <input
                type="number"
                value={form.limitPerUser}
                onChange={(e) => setForm({ ...form, limitPerUser: Math.max(1, +e.target.value) })}
                min="1"
                max="10"
                placeholder="1~10"
                style={{
                  width: "100%",
                  padding: "12px",
                  border: "1px solid #d1d5db",
                  borderRadius: "6px",
                  fontSize: "14px"
                }}
              />
            </div>

            {/* 예매 방식 */}
            <div>
              <label style={{ display: "block", marginBottom: "8px", fontSize: "14px", color: "#374151", fontWeight: "500" }}>
                📌 예매 방식
              </label>
              <select
                value={form.openType}
                onChange={(e) => setForm({ ...form, openType: e.target.value })}
                style={{
                  width: "100%",
                  padding: "12px",
                  border: "1px solid #d1d5db",
                  borderRadius: "6px",
                  fontSize: "14px",
                  backgroundColor: "white"
                }}
              >
                <option value="NORMAL">일반 오픈</option>
                <option value="FIRST_COME">선착순</option>
              </select>
            </div>
          </div>

          <div style={{ display: "flex", justifyContent: "flex-end" }}>
            <button 
              onClick={createTicketOpen}
              disabled={!form.openAt || form.limitPerUser < 1}
              style={{
                padding: "12px 24px",
                backgroundColor: (form.openAt && form.limitPerUser >= 1) ? "#3b82f6" : "#9ca3af",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: (form.openAt && form.limitPerUser >= 1) ? "pointer" : "not-allowed",
                fontSize: "14px",
                fontWeight: "500"
              }}
            >
              예매 오픈 등록
            </button>
          </div>
        </div>
      </div>

      {/* 등록된 예매 오픈 목록 */}
      <div>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>📋 등록된 예매 오픈 ({ticketOpens.length}개)</h3>
        {ticketOpens.length === 0 ? (
          <div style={{
            textAlign: "center",
            padding: "40px",
            color: "#6b7280",
            backgroundColor: "#f9fafb",
            borderRadius: "8px",
            border: "1px solid #e5e7eb"
          }}>
            등록된 예매 오픈이 없습니다. 위에서 첫 번째 예매 오픈을 등록해보세요!
          </div>
        ) : (
          <div style={{ display: "grid", gap: "15px" }}>
            {ticketOpens.map((t) => (
              <div key={t.id} style={{
                backgroundColor: "white",
                border: "1px solid #e5e7eb",
                borderRadius: "8px",
                padding: "20px",
                boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)"
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
                  <div style={{ flex: "1" }}>
                    <div style={{ marginBottom: "12px" }}>
                      <h4 style={{ margin: "0 0 8px 0", color: "#1f2937", fontSize: "16px", lineHeight: "1.4" }}>
                        {getScheduleInfo(t.scheduleId)}
                      </h4>
                    </div>
                    
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: "12px" }}>
                      <div>
                        <span style={{ fontSize: "12px", color: "#6b7280", fontWeight: "500" }}>오픈 시간</span>
                        <div style={{ color: "#374151", fontSize: "14px", marginTop: "2px" }}>
                          🕐 {formatDateTime(t.openAt)}
                        </div>
                      </div>
                      
                      <div>
                        <span style={{ fontSize: "12px", color: "#6b7280", fontWeight: "500" }}>인당 제한</span>
                        <div style={{ color: "#374151", fontSize: "14px", marginTop: "2px" }}>
                          👤 {t.limitPerUser}매
                        </div>
                      </div>
                      
                      <div>
                        <span style={{ fontSize: "12px", color: "#6b7280", fontWeight: "500" }}>예매 방식</span>
                        <div style={{ marginTop: "2px" }}>
                          <span style={{
                            backgroundColor: t.openType === 'FIRST_COME' ? "#fef3c7" : "#dbeafe",
                            color: t.openType === 'FIRST_COME' ? "#92400e" : "#1e40af",
                            padding: "2px 8px",
                            borderRadius: "12px",
                            fontSize: "12px",
                            fontWeight: "500"
                          }}>
                            {getOpenTypeLabel(t.openType)}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <button
                    onClick={() => deleteTicketOpen(t.id)}
                    style={{
                      padding: "8px 16px",
                      backgroundColor: "#ef4444",
                      color: "white",
                      border: "none",
                      borderRadius: "6px",
                      cursor: "pointer",
                      fontSize: "14px",
                      marginLeft: "15px"
                    }}
                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#dc2626"}
                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "#ef4444"}
                  >
                    삭제
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
