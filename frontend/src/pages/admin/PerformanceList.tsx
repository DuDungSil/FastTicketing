import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

type Performance = {
  id: number;
  title: string;
};

export default function PerformanceList() {
  const [performances, setPerformances] = useState<Performance[]>([]);
  const [title, setTitle] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchPerformances();
  }, []);

  const fetchPerformances = async () => {
    const res = await axios.get("/api/performances");
    setPerformances(res.data);
  };

  const createPerformance = async () => {
    await axios.post("/api/performances", { title });
    setTitle("");
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
    <div>
      <h2>🎭 공연 목록</h2>
      <ul>
        {performances.map((p) => (
          <li key={p.id}>
            {p.title}
            <button
              onClick={() => goToSchedule(p.id)}
              style={{ marginLeft: "10px" }}
            >
              스케줄 등록 →
            </button>
            <button
              onClick={() => deletePerformance(p.id)}
              style={{ marginLeft: "10px", color: "red" }}
            >
              삭제
            </button>
          </li>
        ))}
      </ul>

      <h3>➕ 공연 등록</h3>
      <input
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="공연명"
      />
      <button onClick={createPerformance}>등록</button>
    </div>
  );
}
