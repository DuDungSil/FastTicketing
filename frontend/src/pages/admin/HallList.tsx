import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

type Hall = {
  id: number;
  name: string;
};

export default function HallList() {
  const { venueId } = useParams();
  const [halls, setHalls] = useState<Hall[]>([]);
  const [name, setName] = useState("");
  const [row, setRow] = useState(5);
  const [col, setCol] = useState(5);

  useEffect(() => {
    fetchHalls();
  }, []);

  const fetchHalls = async () => {
    const res = await axios.get(`/api/venues/${venueId}/halls`);
    setHalls(res.data);
  };

  const createHall = async () => {
    await axios.post(`/api/venues/${venueId}/hall`, {
      name,
      row,
      column: col,
    });
    setName("");
    fetchHalls();
  };

  const deleteHall = async (hallId: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    await axios.delete(`/api/venues/${venueId}/halls/${hallId}`);
    fetchHalls();
  };

  return (
    <div>
      <h2>🏟️ Hall 목록 (Venue #{venueId})</h2>
      <ul>
        {halls.map((hall) => (
          <li key={hall.id}>
            {hall.name}
            <button
              onClick={() => deleteHall(hall.id)}
              style={{ marginLeft: "10px", color: "red" }}
            >
              삭제
            </button>
          </li>
        ))}
      </ul>

      <h3>➕ Hall 등록</h3>
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="홀 이름"
      />
      <input
        type="number"
        value={row}
        onChange={(e) => setRow(+e.target.value)}
        placeholder="행 수"
      />
      <input
        type="number"
        value={col}
        onChange={(e) => setCol(+e.target.value)}
        placeholder="열 수"
      />
      <button onClick={createHall}>등록</button>
    </div>
  );
}
