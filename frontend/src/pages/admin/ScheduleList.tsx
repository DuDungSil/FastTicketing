import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";

type Schedule = {
  id: number;
  hallId: number;
  hallName?: string;
  venueName?: string;
  startTime: string;
  endTime: string;
  price: number;
};

type Performance = {
  id: number;
  title: string;
  genre?: string;
};

type Venue = {
  id: number;
  name: string;
};

type Hall = {
  id: number;
  name: string;
};

export default function ScheduleList() {
  const { performanceId } = useParams();
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [performance, setPerformance] = useState<Performance | null>(null);

  const [venues, setVenues] = useState<Venue[]>([]);
  const [halls, setHalls] = useState<Hall[]>([]);
  const [selectedVenueId, setSelectedVenueId] = useState<number | null>(null);
  const [selectedHallId, setSelectedHallId] = useState<number | null>(null);

  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const [price, setPrice] = useState<number>(0);

  useEffect(() => {
    fetchPerformance();
    fetchSchedules();
    fetchVenues();
  }, []);

  const fetchPerformance = async () => {
    try {
      const res = await axios.get(`/api/performances/${performanceId}`);
      setPerformance(res.data);
    } catch (error) {
      console.error("Failed to fetch performance:", error);
    }
  };

  const fetchSchedules = async () => {
    const res = await axios.get(`/api/performances/${performanceId}/schedules`);
    setSchedules(res.data);
  };

  const fetchVenues = async () => {
    const res = await axios.get("/api/venues");
    setVenues(res.data);
  };

  const fetchHalls = async (venueId: number) => {
    const res = await axios.get(`/api/venues/${venueId}/halls`);
    setHalls(res.data);
  };

  const onVenueChange = (venueId: number) => {
    setSelectedVenueId(venueId);
    setSelectedHallId(null);
    fetchHalls(venueId);
  };

  const createSchedule = async () => {
    if (!selectedHallId) {
      alert("홀을 선택하세요.");
      return;
    }
    if (!startTime || !endTime) {
      alert("시작 시간과 종료 시간을 모두 입력해주세요.");
      return;
    }
    if (price <= 0) {
      alert("가격을 입력해주세요.");
      return;
    }
    if (new Date(startTime) >= new Date(endTime)) {
      alert("종료 시간이 시작 시간보다 빠를 수 없습니다.");
      return;
    }

    try {
      await axios.post(`/api/performances/${performanceId}/schedule`, {
        hallId: selectedHallId,
        startTime,
        endTime,
        price,
      });
      setStartTime("");
      setEndTime("");
      setPrice(0);
      setSelectedVenueId(null);
      setSelectedHallId(null);
      setHalls([]);
      fetchSchedules();
    } catch (error) {
      console.error("Failed to create schedule:", error);
      alert("스케줄 등록에 실패했습니다.");
    }
  };

  const deleteSchedule = async (scheduleId: number) => {
    if (!confirm("정말 이 스케줄을 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`/api/performances/schedules/${scheduleId}`);
      fetchSchedules();
    } catch (error) {
      console.error("Failed to delete schedule:", error);
      alert("스케줄 삭제에 실패했습니다.");
    }
  };

  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1000px", margin: "0 auto" }}>
      {/* 네비게이션 */}
      <div style={{ marginBottom: "20px" }}>
        <Link
          to="/admin/performances"
          style={{
            color: "#6b7280",
            textDecoration: "none",
            fontSize: "14px",
          }}
        >
          ← 공연 관리로 돌아가기
        </Link>
      </div>

      <header style={{ marginBottom: "30px" }}>
        <h2 style={{ color: "#1f2937", marginBottom: "10px" }}>
          📅 일정 관리 {performance && `- ${performance.title}`}
        </h2>
        <p style={{ color: "#6b7280" }}>공연 일정을 등록하고 관리하세요</p>
      </header>

      {/* 일정 등록 폼 */}
      <div
        style={{
          backgroundColor: "#f8fafc",
          padding: "20px",
          borderRadius: "8px",
          border: "1px solid #e2e8f0",
          marginBottom: "30px",
        }}
      >
        <h3 style={{ color: "#374151", marginBottom: "20px" }}>
          ➕ 새 일정 등록
        </h3>

        {/* 공연장 및 홀 선택 */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: "15px",
            marginBottom: "20px",
          }}
        >
          <div>
            <label
              style={{
                display: "block",
                marginBottom: "5px",
                fontSize: "14px",
                color: "#374151",
              }}
            >
              🎪 공연장 선택
            </label>
            <select
              onChange={(e) => onVenueChange(+e.target.value)}
              value={selectedVenueId ?? ""}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
                backgroundColor: "white",
              }}
            >
              <option value="">공연장을 선택하세요</option>
              {venues.map((v) => (
                <option key={v.id} value={v.id}>
                  {v.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label
              style={{
                display: "block",
                marginBottom: "5px",
                fontSize: "14px",
                color: "#374151",
              }}
            >
              🏟️ 홀 선택
            </label>
            <select
              onChange={(e) => setSelectedHallId(+e.target.value)}
              value={selectedHallId ?? ""}
              disabled={!selectedVenueId}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
                backgroundColor: selectedVenueId ? "white" : "#f9fafb",
                cursor: selectedVenueId ? "pointer" : "not-allowed",
              }}
            >
              <option value="">홀을 선택하세요</option>
              {halls.map((h) => (
                <option key={h.id} value={h.id}>
                  {h.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* 시간 및 가격 선택 */}
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr 1fr auto",
            gap: "15px",
            alignItems: "end",
          }}
        >
          <div>
            <label
              style={{
                display: "block",
                marginBottom: "5px",
                fontSize: "14px",
                color: "#374151",
              }}
            >
              시작 시간
            </label>
            <input
              type="datetime-local"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
              }}
            />
          </div>

          <div>
            <label
              style={{
                display: "block",
                marginBottom: "5px",
                fontSize: "14px",
                color: "#374151",
              }}
            >
              종료 시간
            </label>
            <input
              type="datetime-local"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
              }}
            />
          </div>

          <div>
            <label
              style={{
                display: "block",
                marginBottom: "5px",
                fontSize: "14px",
                color: "#374151",
              }}
            >
              💰 가격 (원)
            </label>
            <input
              type="number"
              value={price}
              onChange={(e) => setPrice(+e.target.value)}
              min="0"
              step="1000"
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px",
              }}
            />
          </div>

          <button
            onClick={createSchedule}
            disabled={!selectedHallId || !startTime || !endTime || price <= 0}
            style={{
              padding: "10px 20px",
              backgroundColor:
                selectedHallId && startTime && endTime && price > 0
                  ? "#3b82f6"
                  : "#9ca3af",
              color: "white",
              border: "none",
              borderRadius: "6px",
              cursor:
                selectedHallId && startTime && endTime && price > 0
                  ? "pointer"
                  : "not-allowed",
              fontSize: "14px",
              fontWeight: "500",
              whiteSpace: "nowrap",
            }}
          >
            일정 등록
          </button>
        </div>
      </div>

      {/* 등록된 일정 목록 */}
      <div>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>
          📋 등록된 일정 ({schedules.length}개)
        </h3>
        {schedules.length === 0 ? (
          <div
            style={{
              textAlign: "center",
              padding: "40px",
              color: "#6b7280",
              backgroundColor: "#f9fafb",
              borderRadius: "8px",
              border: "1px solid #e5e7eb",
            }}
          >
            등록된 일정이 없습니다. 위에서 첫 번째 일정을 등록해보세요!
          </div>
        ) : (
          <div style={{ display: "grid", gap: "15px" }}>
            {schedules.map((s) => (
              <div
                key={s.id}
                style={{
                  backgroundColor: "white",
                  border: "1px solid #e5e7eb",
                  borderRadius: "8px",
                  padding: "20px",
                  boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)",
                }}
              >
                <div
                  style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "start",
                  }}
                >
                  <div style={{ flex: "1" }}>
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "10px",
                        marginBottom: "10px",
                      }}
                    >
                      <h4
                        style={{
                          margin: "0",
                          color: "#1f2937",
                          fontSize: "16px",
                        }}
                      >
                        {s.hallName || `홀 #${s.hallId}`}
                      </h4>
                      {s.venueName && (
                        <span
                          style={{
                            backgroundColor: "#e0e7ff",
                            color: "#3730a3",
                            padding: "2px 8px",
                            borderRadius: "12px",
                            fontSize: "12px",
                            fontWeight: "500",
                          }}
                        >
                          {s.venueName}
                        </span>
                      )}
                    </div>
                    <div style={{ color: "#6b7280", fontSize: "14px" }}>
                      <div style={{ marginBottom: "4px" }}>
                        🕐 시작: {formatDateTime(s.startTime)}
                      </div>
                      <div style={{ marginBottom: "4px" }}>
                        🕚 종료: {formatDateTime(s.endTime)}
                      </div>
                      <div style={{ fontWeight: "600", color: "#059669" }}>
                        💰 가격: {s.price.toLocaleString()}원
                      </div>
                    </div>
                  </div>
                  <button
                    onClick={() => deleteSchedule(s.id)}
                    style={{
                      padding: "8px 16px",
                      backgroundColor: "#ef4444",
                      color: "white",
                      border: "none",
                      borderRadius: "6px",
                      cursor: "pointer",
                      fontSize: "14px",
                    }}
                    onMouseEnter={(e) =>
                      (e.currentTarget.style.backgroundColor = "#dc2626")
                    }
                    onMouseLeave={(e) =>
                      (e.currentTarget.style.backgroundColor = "#ef4444")
                    }
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
