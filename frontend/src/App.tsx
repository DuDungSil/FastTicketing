import { BrowserRouter, Routes, Route } from "react-router-dom";
import AdminDashboard from "./pages/admin/AdminDashboard";
import TicketingHome from "./pages/user/TicketingHome";
import VenueList from "./pages/admin/VenueList";
import HallList from "./pages/admin/HallList";
import PerformanceList from "./pages/admin/PerformanceList";
import ScheduleList from "./pages/admin/ScheduleList";
import TicketOpenList from "./pages/admin/TicketOpenList";
import QueueStatusPage from "./pages/user/QueueStatusPage";
import SelectSeatPage from "./pages/tickets/SeatSelectPage";
import PaymentPage from "./pages/tickets/PaymentPage";
import Home from "./pages/root/Home";
import ConfirmPage from "./pages/tickets/PaymentCompletePage";
import LoginPage from "./pages/auth/LoginPage";
import Header from "./components/Header";

export default function App() {
  return (
    <div className="min-h-screen">
      <BrowserRouter>
        <Header />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginPage />} />

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
          <Route path="/queue/status" element={<QueueStatusPage />} />

          {/* 티켓팅 플로우 */}
          <Route path="/select-seat" element={<SelectSeatPage />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/payment/complete" element={<ConfirmPage />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}
