import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import AdminDashboard from "./pages/admin/AdminDashboard";
import TicketingHome from "./pages/user/TicketingHome";
import VenueList from "./pages/admin/VenueList";
import HallList from "./pages/admin/HallList";
import PerformanceList from "./pages/admin/PerformanceList";
import ScheduleList from "./pages/admin/ScheduleList";
import TicketOpenList from "./pages/admin/TicketOpenList";
// import QueueEnter from "./pages/user/QueueEnter";
// import QueueStatus from "./pages/user/QueueStatus";

export default function App() {
  return (
    <BrowserRouter>
      <div style={{ padding: "20px" }}>
        <h1>🎟️ FastTicketing</h1>
        <nav style={{ marginBottom: "20px" }}>
          <Link to="/admin" style={{ marginRight: "10px" }}>
            🛠️ 관리자 페이지
          </Link>
          <Link to="/ticketings">👤 유저 티켓팅 사이트</Link>
        </nav>

        <Routes>
          {/* 관리자 라우팅 */}
          <Route path="/admin" element={<AdminDashboard />} />
          <Route path="/admin/venues" element={<VenueList />} />
          <Route path="/admin/venues/:venueId/halls" element={<HallList />} />
          <Route path="/admin/performances" element={<PerformanceList />} />
          <Route
            path="/admin/performances/:performanceId/schedules"
            element={<ScheduleList />}
          />
          <Route path="/admin/ticket-opens" element={<TicketOpenList />} />

          {/* 유저 라우팅 */}
          <Route path="/ticketings" element={<TicketingHome />} />
          {/* <Route path="/queue/enter" element={<QueueEnter />} /> */}
          {/* <Route path="/queue/status" element={<QueueStatus />} /> */}
        </Routes>
      </div>
    </BrowserRouter>
  );
}
