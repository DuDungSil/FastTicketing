import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

type Venue = {
  id: number;
  name: string;
};

export default function VenueList() {
  const [venues, setVenues] = useState<Venue[]>([]);
  const [name, setName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchVenues();
  }, []);

  const fetchVenues = async () => {
    const res = await axios.get("/api/venues");
    setVenues(res.data);
  };

  const createVenue = async () => {
    await axios.post("/api/venues", { name });
    setName("");
    fetchVenues();
  };

  const deleteVenue = async (venueId: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    await axios.delete(`/api/venues/${venueId}`);
    fetchVenues();
  };

  const goToHalls = (venueId: number) => {
    navigate(`/admin/venues/${venueId}/halls`);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1000px", margin: "0 auto" }}>
      <header style={{ marginBottom: "30px" }}>
        <h2 style={{ color: "#1f2937", marginBottom: "10px" }}>🎪 공연장 관리</h2>
        <p style={{ color: "#6b7280" }}>공연장을 등록하고 관리하세요</p>
      </header>

      {/* 공연장 등록 폼 */}
      <div style={{
        backgroundColor: "#f8fafc",
        padding: "20px",
        borderRadius: "8px",
        border: "1px solid #e2e8f0",
        marginBottom: "30px"
      }}>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>➕ 새 공연장 등록</h3>
        <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="공연장 이름을 입력하세요"
            style={{
              flex: "1",
              padding: "10px",
              border: "1px solid #d1d5db",
              borderRadius: "6px",
              fontSize: "14px"
            }}
          />
          <button 
            onClick={createVenue}
            style={{
              padding: "10px 20px",
              backgroundColor: "#3b82f6",
              color: "white",
              border: "none",
              borderRadius: "6px",
              cursor: "pointer",
              fontSize: "14px",
              fontWeight: "500"
            }}
            onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#2563eb"}
            onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "#3b82f6"}
          >
            등록
          </button>
        </div>
      </div>

      {/* 공연장 목록 */}
      <div>
        <h3 style={{ color: "#374151", marginBottom: "15px" }}>📝 등록된 공연장 ({venues.length}개)</h3>
        {venues.length === 0 ? (
          <div style={{
            textAlign: "center",
            padding: "40px",
            color: "#6b7280",
            backgroundColor: "#f9fafb",
            borderRadius: "8px",
            border: "1px solid #e5e7eb"
          }}>
            등록된 공연장이 없습니다. 위에서 첫 번째 공연장을 등록해보세요!
          </div>
        ) : (
          <div style={{ display: "grid", gap: "15px" }}>
            {venues.map((venue) => (
              <div key={venue.id} style={{
                backgroundColor: "white",
                border: "1px solid #e5e7eb",
                borderRadius: "8px",
                padding: "20px",
                boxShadow: "0 1px 3px rgba(0, 0, 0, 0.1)"
              }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <h4 style={{ margin: "0 0 5px 0", color: "#1f2937", fontSize: "18px" }}>
                      {venue.name}
                    </h4>
                    <p style={{ margin: "0", color: "#6b7280", fontSize: "14px" }}>
                      ID: {venue.id}
                    </p>
                  </div>
                  <div style={{ display: "flex", gap: "10px" }}>
                    <button
                      onClick={() => goToHalls(venue.id)}
                      style={{
                        padding: "8px 16px",
                        backgroundColor: "#10b981",
                        color: "white",
                        border: "none",
                        borderRadius: "6px",
                        cursor: "pointer",
                        fontSize: "14px"
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#059669"}
                      onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "#10b981"}
                    >
                      홀 관리 →
                    </button>
                    <button
                      onClick={() => deleteVenue(venue.id)}
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
