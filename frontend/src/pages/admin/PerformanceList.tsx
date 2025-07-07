import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

type Performance = {
  id: number;
  title: string;
  genre?: string;
};

export default function PerformanceList() {
  const [performances, setPerformances] = useState<Performance[]>([]);
  const [title, setTitle] = useState("");
  const [genre, setGenre] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchPerformances();
  }, []);

  const fetchPerformances = async () => {
    const res = await axios.get("/api/performances");
    setPerformances(res.data);
  };

  const createPerformance = async () => {
    if (!title.trim()) return;
    await axios.post("/api/performances", { title, genre: genre || "기타" });
    setTitle("");
    setGenre("");
    fetchPerformances();
  };

  const deletePerformance = async (id: number) => {
    if (!confirm("정말 이 공연을 삭제할까요?")) return;
    await axios.delete(`/api/performances/${id}`);
    fetchPerformances();
  };

  const goToSchedule = (id: number) => {
    navigate(`/admin/performances/${id}/schedules`);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1000px", margin: "0 auto" }}>
      <header style={{ marginBottom: "30px" }}>
        <h2 style={{ color: "#1f2937", marginBottom: "10px" }}>🎭 공연 관리</h2>
        <p style={{ color: "#6b7280" }}>공연을 등록하고 스케줄을 관리하세요</p>
      </header>

      {/* 공연 등록 폼 */}
      <div style={{
        backgroundColor: "#f8fafc",
        padding: "20px",
        borderRadius: "8px",
        border: "1px solid #e2e8f0",
        marginBottom: "30px"
      }}>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>➕ 새 공연 등록</h3>
        <div style={{ display: "grid", gridTemplateColumns: "2fr 1fr auto", gap: "10px", alignItems: "end" }}>
          <div>
            <label style={{ display: "block", marginBottom: "5px", fontSize: "14px", color: "#374151" }}>
              공연명
            </label>
            <input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="공연 제목을 입력하세요"
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
              장르
            </label>
            <select
              value={genre}
              onChange={(e) => setGenre(e.target.value)}
              style={{
                width: "100%",
                padding: "10px",
                border: "1px solid #d1d5db",
                borderRadius: "6px",
                fontSize: "14px"
              }}
            >
              <option value="">선택</option>
              <option value="콘서트">콘서트</option>
              <option value="뮤지컬">뮤지컬</option>
              <option value="연극">연극</option>
              <option value="클래식">클래식</option>
              <option value="기타">기타</option>
            </select>
          </div>
          <button 
            onClick={createPerformance}
            disabled={!title.trim()}
            style={{
              padding: "10px 20px",
              backgroundColor: title.trim() ? "#3b82f6" : "#9ca3af",
              color: "white",
              border: "none",
              borderRadius: "6px",
              cursor: title.trim() ? "pointer" : "not-allowed",
              fontSize: "14px",
              fontWeight: "500"
            }}
            onMouseEnter={(e) => {
              if (title.trim()) e.currentTarget.style.backgroundColor = "#2563eb";
            }}
            onMouseLeave={(e) => {
              if (title.trim()) e.currentTarget.style.backgroundColor = "#3b82f6";
            }}
          >
            등록
          </button>
        </div>
      </div>

      {/* 공연 목록 */}
      <div>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>🎪 등록된 공연 ({performances.length}개)</h3>
        {performances.length === 0 ? (
          <div style={{
            textAlign: "center",
            padding: "40px",
            color: "#6b7280",
            backgroundColor: "#f9fafb",
            borderRadius: "8px",
            border: "1px solid #e5e7eb"
          }}>
            등록된 공연이 없습니다. 위에서 첫 번째 공연을 등록해보세요!
          </div>
        ) : (
          <div style={{ display: "grid", gap: "15px" }}>
            {performances.map((p) => (
              <div key={p.id} style={{
                backgroundColor: "white",
                border: "1px solid #e5e7eb",
                borderRadius: "8px",
                padding: "20px",
                boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)"
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div style={{ flex: "1" }}>
                    <h4 style={{ margin: "0 0 5px 0", color: "#1f2937", fontSize: "18px" }}>
                      {p.title}
                    </h4>
                    <div style={{ display: "flex", gap: "15px", alignItems: "center" }}>
                      <p style={{ margin: "0", color: "#6b7280", fontSize: "14px" }}>
                        ID: {p.id}
                      </p>
                      {p.genre && (
                        <span style={{
                          backgroundColor: "#dbeafe",
                          color: "#1e40af",
                          padding: "2px 8px",
                          borderRadius: "12px",
                          fontSize: "12px",
                          fontWeight: "500"
                        }}>
                          {p.genre}
                        </span>
                      )}
                    </div>
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button
                      onClick={() => goToSchedule(p.id)}
                      style={{
                        padding: "8px 16px",
                        backgroundColor: "#8b5cf6",
                        color: "white",
                        border: "none",
                        borderRadius: "6px",
                        cursor: "pointer",
                        fontSize: "14px"
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#7c3aed"}
                      onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "#8b5cf6"}
                    >
                      일정 관리 →
                    </button>
                    <button
                      onClick={() => deletePerformance(p.id)}
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
