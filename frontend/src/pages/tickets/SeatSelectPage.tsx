import { useState, useEffect, useRef, useCallback, memo } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { ZoomIn, ZoomOut, RotateCcw } from "lucide-react";
import { getAuthUser } from "../../utils/auth";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../../components/ui/card";
import { Button } from "../../components/ui/button";
import { Badge } from "../../components/ui/badge";
import axios from "axios";

// 타입 정의
type SeatDto = {
  id: number;
  rowIndex: number;
  columnIndex: number;
};

type TicketStatus = "AVAILABLE" | "RESERVED" | "BLOCKED" | "CANCEL_PENDING";

type TicketStatusDto = {
  ticketId: number;
  seatId: number;
  status: TicketStatus;
};

type SeatState = "AVAILABLE" | "SELECTED" | "DISABLED";

type SeatInfo = SeatDto & {
  status: TicketStatus;
  state: SeatState;
};

// 개별 좌석 컴포넌트 (메모이제이션으로 성능 최적화)
const SeatButton = memo(
  ({
    seat,
    isSelected,
    seatSize,
    x,
    y,
    onClick,
    getRowLabel,
  }: {
    seat: SeatInfo;
    isSelected: boolean;
    seatSize: number;
    x: number;
    y: number;
    onClick: (seatId: number) => void;
    getRowLabel: (rowNumber: number) => string;
  }) => {
    const getSeatClassName = useCallback(() => {
      const baseClass =
        "absolute rounded border cursor-pointer transition-all duration-200 flex items-center justify-center text-xs font-medium";

      if (isSelected) {
        return `${baseClass} bg-blue-500 border-blue-600 text-white hover:bg-blue-600`;
      }

      switch (seat.state) {
        case "AVAILABLE":
          return `${baseClass} bg-green-100 border-green-300 text-green-800 hover:bg-green-200 hover:border-green-400`;
        case "DISABLED":
          return `${baseClass} bg-gray-100 border-gray-300 text-gray-500 cursor-not-allowed`;
        default:
          return baseClass;
      }
    }, [isSelected, seat.state]);

    return (
      <button
        onClick={() => onClick(seat.id)}
        className={getSeatClassName()}
        disabled={seat.state === "DISABLED"}
        style={{
          left: x,
          top: y,
          width: seatSize,
          height: seatSize,
          fontSize: Math.max(8, seatSize / 3),
        }}
        title={`${getRowLabel(seat.rowIndex)}-${seat.columnIndex} - ${
          seat.status === "AVAILABLE" ? "예약 가능" : "예약 불가"
        }`}
      >
        {seatSize > 16 ? seat.columnIndex : ""}
      </button>
    );
  }
);

type VenueConfig = {
  rows: number;
  columns: number;
  seatSize: number;
  gap: number;
};

export default function SeatSelectPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [seats, setSeats] = useState<SeatInfo[]>([]);
  const [selectedSeats, setSelectedSeats] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [zoom, setZoom] = useState(1);
  const [venueConfig, setVenueConfig] = useState<VenueConfig>({
    rows: 20,
    columns: 30,
    seatSize: 24,
    gap: 2,
  });
  const [scheduleId, setScheduleId] = useState<number | null>(null);
  const [ticketOpenId, setTicketOpenId] = useState<number | null>(null);
  // 드래그 관련 state 제거 (스크롤로 대체)
  const [error, setError] = useState<string | null>(null);
  const [reserving, setReserving] = useState(false);

  const containerRef = useRef<HTMLDivElement>(null);
  const viewportRef = useRef<HTMLDivElement>(null);

  // 실제 좌석 데이터 가져오기
  const fetchSeats = useCallback(async () => {
    setError(null); // 에러 초기화

    if (!ticketOpenId) {
      // ticketOpenId가 없으면 mock 데이터 생성 (2000개 좌석)
      const mockSeats: SeatDto[] = [];
      const totalSeats = 2000;
      const optimalCols = Math.ceil(Math.sqrt(totalSeats * 1.5)); // 가로를 좀 더 길게
      const optimalRows = Math.ceil(totalSeats / optimalCols);

      for (let row = 1; row <= optimalRows; row++) {
        for (let col = 1; col <= optimalCols; col++) {
          if (mockSeats.length < totalSeats) {
            mockSeats.push({
              id: (row - 1) * optimalCols + col,
              rowIndex: row,
              columnIndex: col,
            });
          }
        }
      }

      // venueConfig 업데이트
      setVenueConfig((prev) => ({
        ...prev,
        rows: optimalRows,
        columns: optimalCols,
      }));
      const mockTicketStatus: TicketStatusDto[] = mockSeats.map((seat) => ({
        ticketId: seat.id * 100,
        seatId: seat.id,
        status:
          Math.random() > 0.7
            ? Math.random() > 0.5
              ? "RESERVED"
              : "BLOCKED"
            : "AVAILABLE",
      }));
      const seatInfos: SeatInfo[] = mockSeats.map((seat) => {
        const ticketStatus = mockTicketStatus.find(
          (ts) => ts.seatId === seat.id
        );
        const status = ticketStatus?.status || "AVAILABLE";
        return {
          ...seat,
          status,
          state: status === "AVAILABLE" ? "AVAILABLE" : "DISABLED",
        };
      });
      setSeats(seatInfos);
      return;
    }

    try {
      let currentScheduleId = scheduleId;

      // scheduleId가 없다면 TicketOpen 정보를 조회하여 획득
      if (!currentScheduleId) {
        const ticketOpenResponse = await axios.get(
          `/api/tickets/ticket-open?ticketOpenId=${ticketOpenId}`
        );
        const ticketOpenData = ticketOpenResponse.data;
        currentScheduleId = ticketOpenData.scheduleId;
      }

      // 좌석 정보와 티켓 상태를 병렬로 조회
      const [seatsResponse, ticketStatusResponse] = await Promise.all([
        axios.get(`/api/performances/schedules/${currentScheduleId}/seats`),
        axios.get(`/api/tickets/status?ticketOpenId=${ticketOpenId}`),
      ]);

      const seatList: SeatDto[] = seatsResponse.data;
      const ticketStatusList: TicketStatusDto[] = ticketStatusResponse.data;

      // 좌석 정보와 티켓 상태를 조합
      const seatInfos: SeatInfo[] = seatList.map((seat) => {
        const ticketStatus = ticketStatusList.find(
          (ticket) => ticket.seatId === seat.id
        );
        const status = ticketStatus?.status || "AVAILABLE";
        return {
          ...seat,
          status,
          state: status === "AVAILABLE" ? "AVAILABLE" : "DISABLED",
        };
      });

      // 실제 좌석 데이터에서 최대 행/열 계산
      if (seatInfos.length > 0) {
        const maxRow = Math.max(...seatInfos.map((seat) => seat.rowIndex));
        const maxCol = Math.max(...seatInfos.map((seat) => seat.columnIndex));

        setVenueConfig((prev) => ({
          ...prev,
          rows: maxRow,
          columns: maxCol,
        }));
      }

      setSeats(seatInfos);
    } catch (error) {
      console.error("Failed to fetch seats:", error);
      setSeats([]);

      // Axios 에러 처리
      if ((error as any).response) {
        // 서버 응답이 있는 경우 (4xx, 5xx)
        const status = (error as any).response.status;
        if (status === 404) {
          setError("요청한 좌석 정보를 찾을 수 없습니다.");
        } else if (status === 500) {
          setError("서버 내부 오류가 발생했습니다.");
        } else if (status === 403) {
          setError("좌석 정보에 접근할 권한이 없습니다.");
        } else if (status >= 400 && status < 500) {
          setError(
            "잘못된 요청입니다. 페이지를 새로고침 후 다시 시도해주세요."
          );
        } else {
          setError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
      } else if ((error as any).request) {
        // 요청은 보냈지만 응답이 없는 경우
        setError("네트워크 연결을 확인해주세요.");
      } else {
        // 요청 설정 중 에러
        setError("좌석 정보를 불러오는 중 오류가 발생했습니다.");
      }
    }
  }, [ticketOpenId, scheduleId, venueConfig.columns, venueConfig.rows]);

  useEffect(() => {
    // React Router state에서 데이터 가져오기
    const stateData = location.state as {
      scheduleId?: number;
      ticketOpenId?: number;
      eventName?: string;
      venue?: string;
      date?: string;
      time?: string;
    } | null;

    // React Router state에서 데이터 설정
    if (stateData?.scheduleId) setScheduleId(stateData.scheduleId);
    if (stateData?.ticketOpenId) setTicketOpenId(stateData.ticketOpenId);

    // URL 파라미터 확인 (새창으로 열린 경우 또는 직접 접근한 경우)
    const searchParams = new URLSearchParams(location.search);
    const scheduleIdParam = searchParams.get("scheduleId");
    const ticketOpenIdParam = searchParams.get("ticketOpenId");

    // URL 파라미터가 있으면 우선 사용 (새창으로 열린 경우)
    if (scheduleIdParam && !stateData?.scheduleId) {
      setScheduleId(parseInt(scheduleIdParam));
    }
    if (ticketOpenIdParam && !stateData?.ticketOpenId) {
      setTicketOpenId(parseInt(ticketOpenIdParam));
    }
  }, [location.search, location.state]);

  useEffect(() => {
    setLoading(true);
    fetchSeats().finally(() => setLoading(false));
    setSelectedSeats([]);
  }, [fetchSeats]);

  // 예약하기 버튼 클릭 핸들러
  const handleReservation = async () => {
    if (selectedSeats.length === 0 || reserving) return;
    
    setReserving(true);

    try {
      const selectedSeatInfos = seats.filter((seat) =>
        selectedSeats.includes(seat.id)
      );

      if (ticketOpenId) {
        // 선택된 좌석들의 ticketId 수집 (이미 가져온 티켓 상태 정보 활용)
        const ticketIds: number[] = [];
        
        // 최신 티켓 상태 정보 한 번만 조회
        const ticketStatusResponse = await axios.get(`/api/tickets/status?ticketOpenId=${ticketOpenId}`);
        const ticketStatusList: TicketStatusDto[] = ticketStatusResponse.data;
        
        for (const seatId of selectedSeats) {
          const ticketStatus = ticketStatusList.find(ts => ts.seatId === seatId);
          
          if (ticketStatus) {
            if (ticketStatus.status === "AVAILABLE") {
              ticketIds.push(ticketStatus.ticketId);
            } else {
              alert(`좌석 ${seatId}번이 이미 예약되었습니다. 페이지를 새로고침 후 다시 선택해주세요.`);
              return;
            }
          } else {
            alert(`좌석 ${seatId}번의 티켓 정보를 찾을 수 없습니다.`);
            return;
          }
        }

        const user = getAuthUser();
        const userId = user?.userId || 1; // 로그인된 사용자 ID 사용

        const response = await axios.post("/api/reservations/reserve", {
          userId,
          ticketOpenId,
          ticketIds,
        });

        // 예약 성공 데이터 구성
        const reservationData = {
          reservationId: response.data.id,
          seats: selectedSeatInfos.map(seat => ({
            seatId: seat.id,
            rowIndex: seat.rowIndex,
            columnIndex: seat.columnIndex,
            seatLabel: `${getRowLabel(seat.rowIndex)}-${seat.columnIndex}`
          })),
          ticketIds,
          scheduleId,
          ticketOpenId,
          userId,
          status: response.data.status,
          timestamp: Date.now(),
          reservationTime: new Date().toISOString(),
        };

        localStorage.setItem(
          "reservationData",
          JSON.stringify(reservationData)
        );

        // 예약 성공 알림
        alert(`${selectedSeats.length}석 예약이 완료되었습니다! 결제 페이지로 이동합니다.`);
      } else {
        // Mock 데이터용 (개발/테스트 환경)
        const mockReservationData = {
          reservationId: Math.floor(Math.random() * 100000),
          seats: selectedSeatInfos.map(seat => ({
            seatId: seat.id,
            rowIndex: seat.rowIndex,
            columnIndex: seat.columnIndex,
            seatLabel: `${getRowLabel(seat.rowIndex)}-${seat.columnIndex}`
          })),
          ticketIds: selectedSeats.map(seatId => seatId * 100), // Mock ticket IDs
          scheduleId: null,
          ticketOpenId: null,
          userId: 1,
          status: "PENDING",
          timestamp: Date.now(),
          reservationTime: new Date().toISOString(),
          // Mock 이벤트 정보
          eventInfo: {
            eventName: "콘서트 2024",
            venue: "올림픽공원 체조경기장",
            date: "2024년 12월 25일",
            time: "19:30",
          }
        };
        
        localStorage.setItem("reservationData", JSON.stringify(mockReservationData));
        alert(`${selectedSeats.length}석 예약이 완료되었습니다! (테스트 모드)`);
      }

      navigate("/payment");
    } catch (error) {
      console.error("Failed to reserve seats:", error);

      // 예약 실패 시 상세한 에러 메시지 표시
      let errorMessage = "좌석 예약에 실패했습니다.";
      let shouldRefreshSeats = false;
      
      if ((error as any).response) {
        const status = (error as any).response.status;
        const responseData = (error as any).response.data;
        
        if (status === 409) {
          errorMessage = "선택한 좌석이 이미 예약되었습니다. 좌석 상태를 새로고침하고 다른 좌석을 선택해주세요.";
          shouldRefreshSeats = true;
        } else if (status === 400) {
          errorMessage = responseData?.message || "잘못된 예약 요청입니다. 다시 시도해주세요.";
        } else if (status === 403) {
          errorMessage = "예약 권한이 없습니다. 로그인 상태를 확인해주세요.";
        } else if (status === 422) {
          errorMessage = "티켓 정보가 유효하지 않습니다. 페이지를 새로고침해주세요.";
          shouldRefreshSeats = true;
        } else if (status >= 500) {
          errorMessage = "서버 오류로 예약에 실패했습니다. 잠시 후 다시 시도해주세요.";
        }
      } else if ((error as any).request) {
        errorMessage = "네트워크 연결을 확인하고 다시 시도해주세요.";
      }

      alert(errorMessage);
      
      // 좌석 상태 자동 새로고침 (동시성 문제인 경우)
      if (shouldRefreshSeats) {
        setSelectedSeats([]); // 선택 초기화
        setLoading(true);
        fetchSeats().finally(() => setLoading(false));
      }
    } finally {
      setReserving(false);
    }
  };

  // 좌석 클릭 핸들러
  const handleSeatClick = (seatId: number) => {
    const seat = seats.find((s) => s.id === seatId);
    if (!seat || seat.state === "DISABLED") return;

    setSelectedSeats((prev) => {
      if (prev.includes(seatId)) {
        return prev.filter((id) => id !== seatId);
      } else {
        return [...prev, seatId];
      }
    });
  };

  // 스크롤 관련 핸들러는 기본 브라우저 스크롤 사용

  // 줌 핸들러
  const handleZoomIn = () => setZoom((prev) => Math.min(prev + 0.2, 3));
  const handleZoomOut = () => setZoom((prev) => Math.max(prev - 0.2, 0.3));
  const handleZoomReset = () => {
    setZoom(1);
    // 스크롤 위치 리셋
    if (containerRef.current) {
      containerRef.current.scrollTop = 0;
      containerRef.current.scrollLeft = 0;
    }
  };

  // 행 라벨 생성
  const getRowLabel = (rowNumber: number) => {
    return rowNumber.toString();
  };

  // 스크롤 기반이므로 모든 좌석을 렌더링 (가상화 제거)
  const visibleSeats = seats;

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">좌석 정보를 불러오는 중...</div>
      </div>
    );
  }

  // 에러 상태 표시
  if (error) {
    return (
      <div className="container mx-auto p-6 max-w-7xl">
        <Card>
          <CardHeader>
            <CardTitle className="text-center text-2xl text-red-600">
              오류 발생
            </CardTitle>
          </CardHeader>
          <CardContent className="text-center space-y-4">
            <div className="text-red-500 text-lg">❌ {error}</div>
            <div className="space-y-2">
              <Button
                onClick={() => {
                  setError(null);
                  setLoading(true);
                  fetchSeats().finally(() => setLoading(false));
                }}
                className="mr-2"
              >
                다시 시도
              </Button>
              <Button variant="outline" onClick={() => navigate(-1)}>
                이전 페이지로
              </Button>
            </div>
            <div className="text-sm text-gray-500 mt-4">
              문제가 지속되면 관리자에게 문의하시기 바랍니다.
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  const totalWidth =
    venueConfig.columns * (venueConfig.seatSize + venueConfig.gap) * zoom + 60; // 좌석 번호 열 공간 추가
  const totalHeight =
    venueConfig.rows * (venueConfig.seatSize + venueConfig.gap) * zoom + 40; // 헤더 공간 추가

  return (
    <div className="container mx-auto p-6 max-w-7xl">
      <Card>
        <CardHeader>
          <CardTitle className="text-center text-2xl">좌석 선택</CardTitle>

          {/* 컨트롤 패널 */}
          <div className="flex flex-wrap gap-4 items-center justify-center">
            {/* 새로고침 버튼 */}
            <div className="flex gap-2 items-center">
              <Button
                variant="outline"
                size="sm"
                onClick={() => {
                  setError(null);
                  setLoading(true);
                  fetchSeats().finally(() => setLoading(false));
                }}
              >
                좌석 상태 새로고침
              </Button>
            </div>

            {/* 줌 컨트롤 */}
            <div className="flex gap-2 items-center">
              <Button variant="outline" size="sm" onClick={handleZoomOut}>
                <ZoomOut className="w-4 h-4" />
              </Button>
              <span className="text-sm w-12 text-center">
                {Math.round(zoom * 100)}%
              </span>
              <Button variant="outline" size="sm" onClick={handleZoomIn}>
                <ZoomIn className="w-4 h-4" />
              </Button>
              <Button variant="outline" size="sm" onClick={handleZoomReset}>
                <RotateCcw className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </CardHeader>

        <CardContent className="space-y-4">
          {/* 스크린 표시 */}
          <div className="text-center">
            <div className="inline-block bg-gray-800 text-white px-8 py-2 rounded-t-lg">
              SCREEN
            </div>
          </div>

          {/* 좌석 뷰포트 - 스크롤 방식 */}
          <div
            ref={containerRef}
            className="relative border-2 border-gray-200 rounded-lg overflow-auto bg-gray-50 custom-scrollbar"
            style={{
              height: "600px",
              scrollbarWidth: "thin", // Firefox
              scrollbarColor: "#cbd5e1 #f1f5f9", // Firefox
            }}
          >
            <div
              ref={viewportRef}
              className="relative"
              style={{
                width: totalWidth,
                height: totalHeight,
                minWidth: "100%",
                minHeight: "100%",
                padding: "20px",
              }}
            >
              {/* 좌석 번호 열 (좌측) */}
              {Array.from({ length: venueConfig.rows }, (_, i) => i + 1).map(
                (rowNumber) => {
                  const seatSize = venueConfig.seatSize * zoom;
                  const gap = venueConfig.gap * zoom;
                  const y = (rowNumber - 1) * (seatSize + gap) + 40; // 헤더 공간 고려

                  return (
                    <div
                      key={`col-header-${rowNumber}`}
                      className="absolute text-gray-700 font-bold pointer-events-none flex items-center justify-center bg-gray-100 border border-gray-300 rounded"
                      style={{
                        left: 0,
                        top: y,
                        width: 50,
                        height: seatSize,
                        fontSize: Math.max(10, seatSize / 2.5),
                        zIndex: 10,
                      }}
                    >
                      {getRowLabel(rowNumber)}
                    </div>
                  );
                }
              )}

              {/* 좌석 번호 행 (상단) */}
              {Array.from({ length: venueConfig.columns }, (_, i) => i + 1).map(
                (colNumber) => {
                  const seatSize = venueConfig.seatSize * zoom;
                  const gap = venueConfig.gap * zoom;
                  const x = (colNumber - 1) * (seatSize + gap) + 60; // 좌석 번호 열 공간 고려

                  return (
                    <div
                      key={`row-header-${colNumber}`}
                      className="absolute text-gray-700 font-bold pointer-events-none flex items-center justify-center bg-gray-100 border border-gray-300 rounded"
                      style={{
                        left: x,
                        top: 0,
                        width: seatSize,
                        height: 30,
                        fontSize: Math.max(8, seatSize / 3),
                        zIndex: 10,
                      }}
                    >
                      {colNumber}
                    </div>
                  );
                }
              )}

              {/* 좌석 렌더링 */}
              {visibleSeats.map((seat) => {
                const seatSize = venueConfig.seatSize * zoom;
                const gap = venueConfig.gap * zoom;
                const x = (seat.columnIndex - 1) * (seatSize + gap) + 60; // 좌석 번호 열 공간 고려
                const y = (seat.rowIndex - 1) * (seatSize + gap) + 40; // 헤더 공간 고려

                return (
                  <SeatButton
                    key={seat.id}
                    seat={seat}
                    isSelected={selectedSeats.includes(seat.id)}
                    seatSize={seatSize}
                    x={x}
                    y={y}
                    onClick={handleSeatClick}
                    getRowLabel={getRowLabel}
                  />
                );
              })}

              {/* 기존 행 라벨은 제거 (위에서 좌석 번호 열/행으로 대체) */}
            </div>
          </div>

          {/* 범례 */}
          <div className="flex justify-center gap-6 pt-4 border-t">
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-green-100 border-2 border-green-300 rounded"></div>
              <span className="text-sm">선택 가능</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-blue-500 border-2 border-blue-600 rounded"></div>
              <span className="text-sm">선택됨</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="w-4 h-4 bg-gray-100 border-2 border-gray-300 rounded"></div>
              <span className="text-sm">선택 불가</span>
            </div>
          </div>

          {/* 선택된 좌석 정보 */}
          {selectedSeats.length > 0 && (
            <div className="bg-blue-50 p-4 rounded-lg">
              <h3 className="font-semibold mb-2">선택된 좌석</h3>
              <div className="flex flex-wrap gap-2 max-h-20 overflow-y-auto">
                {selectedSeats.slice(0, 20).map((seatId) => {
                  const seat = seats.find((s) => s.id === seatId);
                  if (!seat) return null;
                  return (
                    <Badge key={seatId} variant="secondary">
                      {getRowLabel(seat.rowIndex)}-{seat.columnIndex}
                    </Badge>
                  );
                })}
                {selectedSeats.length > 20 && (
                  <Badge variant="outline">
                    +{selectedSeats.length - 20}석 더
                  </Badge>
                )}
              </div>
              <div className="mt-3 text-sm text-gray-600">
                총 {selectedSeats.length}석 선택
              </div>
            </div>
          )}

          {/* 전체 좌석 정보 */}
          <div className="bg-gray-50 p-4 rounded-lg">
            <div className="text-sm text-gray-600 space-y-1">
              <div>
                전체 좌석 수: {seats.length}개 | 선택 가능:{" "}
                {seats.filter((s) => s.status === "AVAILABLE").length}개 |
                예약됨: {seats.filter((s) => s.status === "RESERVED").length}개
              </div>
              <div className="text-blue-600">
                전체 좌석 렌더링 (스크롤 방식)
              </div>
              <div className="text-purple-600">
                레이아웃: {venueConfig.rows}행 × {venueConfig.columns}열
              </div>
            </div>
          </div>

          {/* 예약 버튼 */}
          <div className="text-center">
            <Button
              size="lg"
              disabled={selectedSeats.length === 0 || reserving}
              className="px-8"
              onClick={handleReservation}
            >
              {reserving 
                ? "예약 처리 중..."
                : selectedSeats.length > 0
                  ? `${selectedSeats.length}석 예약하기`
                  : "좌석을 선택해주세요"}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
