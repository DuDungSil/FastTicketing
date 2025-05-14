import { Link } from "react-router-dom";

export default function AdminDashboard() {
  return (
    <div style={{ padding: "20px" }}>
      <h1>🛠️ 관리자 페이지</h1>
      <ul>
        <li>
          <Link to="/admin/venues">🎪 장소 등록</Link>
        </li>
        <li>
          <Link to="/admin/performances">🎭 공연 등록</Link>
        </li>
        <li>
          <Link to="/admin/ticket-opens">🎫 예매 정보 등록</Link>
        </li>
      </ul>
    </div>
  );
}
