import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";

type Hall = {
  id: number;
  name: string;
  totalSeats?: number;
};

type Venue = {
  id: number;
  name: string;
};

export default function HallList() {
  const { venueId } = useParams();
  const [halls, setHalls] = useState<Hall[]>([]);
  const [venue, setVenue] = useState<Venue | null>(null);
  const [name, setName] = useState("");
  const [row, setRow] = useState(10);
  const [col, setCol] = useState(20);

  useEffect(() => {
    fetchVenue();
    fetchHalls();
  }, []);

  const fetchVenue = async () => {
    try {
      const res = await axios.get(`/api/venues/${venueId}`);
      setVenue(res.data);
    } catch (error) {
      console.error('Failed to fetch venue:', error);
    }
  };

  const fetchHalls = async () => {
    const res = await axios.get(`/api/venues/${venueId}/halls`);
    setHalls(res.data);
  };

  const createHall = async () => {
    if (!name.trim() || row < 1 || col < 1) {
      alert('모든 필드를 올바르게 입력해주세요.');
      return;
    }
    try {
      await axios.post(`/api/venues/${venueId}/hall`, {
        name,
        row,
        column: col,
      });
      setName("");
      setRow(10);
      setCol(20);
      fetchHalls();
    } catch (error) {
      console.error('Failed to create hall:', error);
      alert('홀 생성에 실패했습니다.');
    }
  };

  const deleteHall = async (hallId: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    await axios.delete(`/api/venues/${venueId}/halls/${hallId}`);
    fetchHalls();
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1000px", margin: "0 auto" }}>
      {/* 네비게이션 */}
      <div style={{ marginBottom: "20px" }}>
        <Link 
          to="/admin/venues" 
          style={{ 
            color: "#6b7280", 
            textDecoration: "none",
            fontSize: "14px"
          }}
        >
          ← 공연장 관리로 돌아가기
        </Link>
      </div>

      <header style={{ marginBottom: "30px" }}>
        <h2 style={{ color: "#1f2937", marginBottom: "10px" }}>
          🏟️ 홀 관리 {venue && `- ${venue.name}`}
        </h2>
        <p style={{ color: "#6b7280" }}>공연장 내 홀을 등록하고 관리하세요</p>
      </header>

      {/* 홀 등록 폼 */}
      <div style={{
        backgroundColor: "#f8fafc",
        padding: "20px",
        borderRadius: "8px",
        border: "1px solid #e2e8f0",
        marginBottom: "30px"
      }}>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>➕ 새 홀 등록</h3>
        <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr auto", gap: "15px", alignItems: "end" }}>
          <div>
            <label style={{ display: "block", marginBottom: "5px", fontSize: "14px", color: "#374151" }}>
              홀 이름
            </label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="홀 이름을 입력하세요"
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px"
              }}
            />
          </div>
          <div>
            <label style={{ display: "block", marginBottom: "5px", fontSize: "14px", color: "#374151" }}>
              행 수
            </label>
            <input
              type="number"
              value={row}
              onChange={(e) => setRow(Math.max(1, +e.target.value))}
              min="1"
              max="50"
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px"
              }}
            />
          </div>
          <div>
            <label style={{ display: "block", marginBottom: "5px", fontSize: "14px", color: "#374151" }}>
              열 수
            </label>
            <input
              type="number"
              value={col}
              onChange={(e) => setCol(Math.max(1, +e.target.value))}
              min="1"
              max="50"
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px"
              }}
            />
          </div>
          <div>
            <div style={{ fontSize: "12px", color: "#6b7280", marginBottom: "5px" }}>
              총 {row * col}석
            </div>
            <button 
              onClick={createHall}
              disabled={!name.trim() || row < 1 || col < 1}
              style={{
                padding: "10px 20px",
                backgroundColor: (name.trim() && row >= 1 && col >= 1) ? "#3b82f6" : "#9ca3af",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: (name.trim() && row >= 1 && col >= 1) ? "pointer" : "not-allowed",
                fontSize: "14px",
                fontWeight: "500"
              }}
            >
              등록
            </button>
          </div>
        </div>
      </div>

      {/* 홀 목록 */}
      <div>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>🎪 등록된 홀 ({halls.length}개)</h3>
        {halls.length === 0 ? (
          <div style={{
            textAlign: "center",
            padding: "40px",
            color: "#6b7280",
            backgroundColor: "#f9fafb",
            borderRadius: "8px",
            border: "1px solid #e5e7eb"
          }}>
            등록된 홀이 없습니다. 위에서 첫 번째 홀을 등록해보세요!
          </div>
        ) : (
          <div style={{ display: "grid", gap: "15px" }}>
            {halls.map((hall) => (
              <div key={hall.id} style={{
                backgroundColor: "white",
                border: "1px solid #e5e7eb",
                borderRadius: "8px",
                padding: "20px",
                boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)"
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div style={{ flex: "1" }}>
                    <h4 style={{ margin: "0 0 5px 0", color: "#1f2937", fontSize: "18px" }}>
                      {hall.name}
                    </h4>
                    <div style={{ display: "flex", gap: "15px", alignItems: "center", flexWrap: "wrap" }}>
                      <p style={{ margin: "0", color: "#6b7280", fontSize: "14px" }}>
                        ID: {hall.id}
                      </p>
                      {hall.totalSeats && (
                        <span style={{
                          backgroundColor: "#dcfce7",
                          color: "#166534",
                          padding: "2px 8px",
                          borderRadius: "12px",
                          fontSize: "12px",
                          fontWeight: "500"
                        }}>
                          {hall.totalSeats}석
                        </span>
                      )}
                    </div>
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button
                      onClick={() => deleteHall(hall.id)}
                      style={{
                        padding: "8px 16px",
                        backgroundColor: "#ef4444",
                        color: "white",
                        border: "none",
                        borderRadius: "6px",
                        cursor: "pointer",
                        fontSize: "14px"
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#dc2626"}
                      onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "#ef4444"}
                    >
                      삭제
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
