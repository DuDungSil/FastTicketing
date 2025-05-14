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
    <div>
      <h2>🎪 Venue 목록</h2>
      <ul>
        {venues.map((venue) => (
          <li key={venue.id}>
            {venue.name}
            <button
              onClick={() => goToHalls(venue.id)}
              style={{ marginLeft: "10px" }}
            >
              홀 등록 →
            </button>
            <button
              onClick={() => deleteVenue(venue.id)}
              style={{ marginLeft: "10px", color: "red" }}
            >
              삭제
            </button>
          </li>
        ))}
      </ul>

      <h3>➕ Venue 등록</h3>
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="장소 이름"
      />
      <button onClick={createVenue}>등록</button>
    </div>
  );
}
