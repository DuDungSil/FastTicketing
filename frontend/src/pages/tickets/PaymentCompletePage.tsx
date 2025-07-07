import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  CheckCircle,
  Download,
  Home,
  Calendar,
  MapPin,
  Clock,
  CreditCard,
  Building2,
} from "lucide-react";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../../components/ui/card";
import { Separator } from "../../components/ui/separator";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";

type PaymentResult = {
  reservationId?: number;
  seats: Array<{
    id: number;
    rowIndex: number;
    columnIndex: number;
  }>;
  scheduleId?: number;
  ticketOpenId?: number;
  eventName?: string;
  venue?: string;
  date?: string;
  time?: string;
  customerInfo: {
    name: string;
    phone: string;
    email: string;
  };
  paymentMethod: "card" | "bank";
  paymentAmount: number;
  paymentDate: string;
  paymentId: string;
};

export default function PaymentCompletePage() {
  const navigate = useNavigate();
  const [paymentResult, setPaymentResult] = useState<PaymentResult | null>(
    null
  );

  useEffect(() => {
    const savedResult = localStorage.getItem("paymentResult");
    if (savedResult) {
      setPaymentResult(JSON.parse(savedResult));
    } else {
      navigate("/");
    }
  }, [navigate]);

  const getRowLabel = (rowNumber: number) => {
    if (rowNumber <= 26) {
      return String.fromCharCode(64 + rowNumber);
    } else {
      const firstChar = String.fromCharCode(
        64 + Math.floor((rowNumber - 1) / 26)
      );
      const secondChar = String.fromCharCode(64 + ((rowNumber - 1) % 26) + 1);
      return firstChar + secondChar;
    }
  };

  const handleDownloadTicket = () => {
    // 티켓 다운로드 시뮬레이션
    alert("티켓이 다운로드되었습니다!");
  };

  const handleNewReservation = () => {
    localStorage.removeItem("paymentResult");
    navigate("/");
  };

  if (!paymentResult) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">결제 정보를 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="text-center mb-8">
        <div className="flex justify-center mb-4">
          <CheckCircle className="w-16 h-16 text-green-500" />
        </div>
        <h1 className="text-3xl font-bold text-green-600 mb-2">
          결제가 완료되었습니다!
        </h1>
        <p className="text-gray-600">예약이 성공적으로 처리되었습니다.</p>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        {/* 예약 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>예약 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div>
                <div className="text-sm text-gray-500">예약번호</div>
                <div className="font-mono font-bold text-lg">
                  {paymentResult.reservationId ? `RES${paymentResult.reservationId}` : `RES${Date.now()}`}
                </div>
              </div>

              <Separator />

              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-gray-500" />
                <div>
                  <div className="font-semibold">{paymentResult.eventName || "콘서트 2024"}</div>
                  <div className="text-sm text-gray-600">
                    {paymentResult.date || "2024년 12월 25일"}
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-gray-500" />
                <div className="text-sm">{paymentResult.time || "19:30"}</div>
              </div>

              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4 text-gray-500" />
                <div className="text-sm">{paymentResult.venue || "올림픽공원 체조경기장"}</div>
              </div>
            </div>

            <Separator />

            <div>
              <h3 className="font-semibold mb-2">좌석 정보</h3>
              <div className="flex flex-wrap gap-2 mb-3">
                {paymentResult.seats.map((seat) => (
                  <Badge key={seat.id} variant="secondary">
                    {getRowLabel(seat.rowIndex)}
                    {seat.columnIndex}
                  </Badge>
                ))}
              </div>
              <div className="text-sm text-gray-600">
                총 {paymentResult.seats.length}석
              </div>
            </div>

            <Separator />

            <div>
              <h3 className="font-semibold mb-2">예매자 정보</h3>
              <div className="space-y-1 text-sm">
                <div>이름: {paymentResult.customerInfo.name}</div>
                <div>연락처: {paymentResult.customerInfo.phone}</div>
                <div>이메일: {paymentResult.customerInfo.email}</div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 결제 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>결제 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <div className="text-sm text-gray-500">결제번호</div>
              <div className="font-mono font-bold">
                {paymentResult.paymentId}
              </div>
            </div>

            <Separator />

            <div className="flex items-center gap-2">
              {paymentResult.paymentMethod === "card" ? (
                <CreditCard className="w-4 h-4 text-gray-500" />
              ) : (
                <Building2 className="w-4 h-4 text-gray-500" />
              )}
              <div>
                <div className="font-semibold">
                  {paymentResult.paymentMethod === "card"
                    ? "신용카드"
                    : "무통장 입금"}
                </div>
                <div className="text-sm text-gray-600">
                  {new Date(paymentResult.paymentDate).toLocaleString()}
                </div>
              </div>
            </div>

            <Separator />

            <div className="space-y-2">
              <div className="flex justify-between">
                <span>좌석료 ({paymentResult.seats.length}석)</span>
                <span>{paymentResult.paymentAmount.toLocaleString()}원</span>
              </div>
              <div className="flex justify-between">
                <span>예매수수료</span>
                <span>2,000원</span>
              </div>
              <Separator />
              <div className="flex justify-between font-bold text-lg">
                <span>총 결제금액</span>
                <span>
                  {(paymentResult.paymentAmount + 2000).toLocaleString()}원
                </span>
              </div>
            </div>

            {paymentResult.paymentMethod === "bank" && (
              <div className="bg-yellow-50 p-3 rounded-lg">
                <div className="text-sm">
                  <div className="font-semibold text-yellow-800 mb-1">
                    입금 안내
                  </div>
                  <div className="text-yellow-700">
                    24시간 이내에 아래 계좌로 입금해주세요.
                  </div>
                  <div className="mt-2 space-y-1">
                    <div>은행: 국민은행</div>
                    <div>계좌번호: 123456-78-901234</div>
                    <div>예금주: (주)티켓링크</div>
                  </div>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* 액션 버튼들 */}
      <div className="flex flex-col sm:flex-row gap-4 mt-8 justify-center">
        <Button
          onClick={handleDownloadTicket}
          className="flex items-center gap-2"
        >
          <Download className="w-4 h-4" />
          티켓 다운로드
        </Button>
        <Button
          variant="outline"
          onClick={handleNewReservation}
          className="flex items-center gap-2 bg-transparent"
        >
          <Home className="w-4 h-4" />
          새로운 예약하기
        </Button>
      </div>

      {/* 안내사항 */}
      <Card className="mt-8">
        <CardHeader>
          <CardTitle className="text-lg">안내사항</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2 text-sm text-gray-600">
            <div>• 공연 당일 신분증을 지참해주세요.</div>
            <div>• 공연 시작 30분 전까지 입장해주세요.</div>
            <div>• 취소/환불은 공연 3일 전까지 가능합니다.</div>
            <div>• 문의사항은 고객센터(1588-1234)로 연락해주세요.</div>
            {paymentResult.paymentMethod === "bank" && (
              <div className="text-red-600">
                • 무통장 입금은 24시간 이내 완료해주세요.
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
