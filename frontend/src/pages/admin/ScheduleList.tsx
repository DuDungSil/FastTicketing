import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";

type Schedule = {
  id: number;
  hallId: number;
  startTime: string;
  endTime: string;
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

  const [venues, setVenues] = useState<Venue[]>([]);
  const [halls, setHalls] = useState<Hall[]>([]);
  const [selectedVenueId, setSelectedVenueId] = useState<number | null>(null);
  const [selectedHallId, setSelectedHallId] = useState<number | null>(null);

  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  useEffect(() => {
    fetchSchedules();
    fetchVenues();
  }, []);

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
    if (!selectedHallId) return alert("홀을 선택하세요");
    await axios.post(`/api/performances/${performanceId}/schedule`, {
      hallId: selectedHallId,
      startTime,
      endTime,
    });
    setStartTime("");
    setEndTime("");
    setSelectedHallId(null);
    fetchSchedules();
  };

  return (
    <div>
      <h2>📅 스케줄 등록 - 공연 #{performanceId}</h2>

      <div>
        <label>🎪 Venue 선택: </label>
        <select
          onChange={(e) => onVenueChange(+e.target.value)}
          value={selectedVenueId ?? ""}
        >
          <option value="">-- 장소 선택 --</option>
          {venues.map((v) => (
            <option key={v.id} value={v.id}>
              {v.name}
            </option>
          ))}
        </select>
      </div>

      <div>
        <label>🏟️ Hall 선택: </label>
        <select
          onChange={(e) => setSelectedHallId(+e.target.value)}
          value={selectedHallId ?? ""}
          disabled={!selectedVenueId}
        >
          <option value="">-- 홀 선택 --</option>
          {halls.map((h) => (
            <option key={h.id} value={h.id}>
              {h.name}
            </option>
          ))}
        </select>
      </div>

      <input
        type="datetime-local"
        value={startTime}
        onChange={(e) => setStartTime(e.target.value)}
      />
      <input
        type="datetime-local"
        value={endTime}
        onChange={(e) => setEndTime(e.target.value)}
      />
      <button onClick={createSchedule}>등록</button>

      <h3>📋 기존 스케줄</h3>
      <ul>
        {schedules.map((s) => (
          <li key={s.id}>
            Hall #{s.hallId} | {s.startTime} ~ {s.endTime}
            <button
              onClick={() => {
                axios
                  .delete(`/api/performances/schedules/${s.id}`)
                  .then(fetchSchedules);
              }}
              style={{ marginLeft: "10px", color: "red" }}
            >
              삭제
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
