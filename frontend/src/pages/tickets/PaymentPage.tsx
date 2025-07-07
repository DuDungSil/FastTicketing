import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {
  CreditCard,
  Building2,
  ArrowLeft,
  Calendar,
  MapPin,
  Clock,
} from "lucide-react";
import { Button } from "../../components/ui/button";
import axios from "axios";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../../components/ui/card";
import { Separator } from "../../components/ui/separator";
import { Badge } from "../../components/ui/badge";
import { Label } from "../../components/ui/label";
import { Input } from "../../components/ui/input";
import { RadioGroup, RadioGroupItem } from "../../components/ui/radio-group";

type SeatInfo = {
  id: number;
  rowIndex: number;
  columnIndex: number;
};

type ReservationData = {
  reservationId?: number;
  seats: SeatInfo[];
  scheduleId?: number;
  ticketOpenId?: number;
  timestamp: number;
  eventName?: string;
  venue?: string;
  date?: string;
  time?: string;
};

export default function PaymentPage() {
  const navigate = useNavigate();
  const [reservationData, setReservationData] =
    useState<ReservationData | null>(null);
  const [paymentMethod, setPaymentMethod] = useState<"card" | "bank">("card");
  const [isProcessing, setIsProcessing] = useState(false);
  const [customerInfo, setCustomerInfo] = useState({
    name: "",
    phone: "",
    email: "",
  });
  const [cardInfo, setCardInfo] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardHolder: "",
  });

  const SEAT_PRICE = 50000; // 좌석당 가격

  useEffect(() => {
    const savedData = localStorage.getItem("reservationData");
    if (savedData) {
      setReservationData(JSON.parse(savedData));
    } else {
      navigate("/select-seat");
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

  const handlePayment = async () => {
    if (!reservationData) return;

    // 필수 정보 검증
    if (!customerInfo.name || !customerInfo.phone || !customerInfo.email) {
      alert("고객 정보를 모두 입력해주세요.");
      return;
    }

    if (paymentMethod === "card") {
      if (
        !cardInfo.cardNumber ||
        !cardInfo.expiryDate ||
        !cardInfo.cvv ||
        !cardInfo.cardHolder
      ) {
        alert("카드 정보를 모두 입력해주세요.");
        return;
      }
    }

    setIsProcessing(true);

    try {
      if (reservationData.reservationId) {
        // 실제 API 호출
        const paymentId = `PAY${Date.now()}`;
        await axios.post(`/api/reservations/payment/start`, null, {
          params: {
            reservationId: reservationData.reservationId,
            paymentId: paymentId
          }
        });

        await new Promise((resolve) => setTimeout(resolve, 3000));

        await axios.post(`/api/reservations/payment/confirm`, null, {
          params: {
            reservationId: reservationData.reservationId
          }
        });

        const paymentResult = {
          ...reservationData,
          customerInfo,
          paymentMethod,
          paymentAmount: reservationData.seats.length * SEAT_PRICE,
          paymentDate: new Date().toISOString(),
          paymentId: paymentId,
          eventName: "콘서트 2024",
          venue: "올림픽공원 체조경기장",
          date: "2024년 12월 25일",
          time: "19:30",
        };

        localStorage.setItem("paymentResult", JSON.stringify(paymentResult));
      } else {
        // Mock 데이터용
        await new Promise((resolve) => setTimeout(resolve, 3000));
        
        const paymentResult = {
          ...reservationData,
          customerInfo,
          paymentMethod,
          paymentAmount: reservationData.seats.length * SEAT_PRICE,
          paymentDate: new Date().toISOString(),
          reservationId: `RES${Date.now()}`,
          paymentId: `PAY${Date.now()}`,
        };

        localStorage.setItem("paymentResult", JSON.stringify(paymentResult));
      }
      
      localStorage.removeItem("reservationData");
      navigate("/payment/complete");
    } catch (error) {
      console.error('Payment failed:', error);
      alert('결제에 실패했습니다. 다시 시도해주세요.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (!reservationData) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">예약 정보를 불러오는 중...</div>
      </div>
    );
  }

  const totalAmount = reservationData.seats.length * SEAT_PRICE;

  return (
    <div className="container mx-auto p-6 max-w-4xl">
      <div className="mb-6">
        <Button
          variant="ghost"
          onClick={() => navigate(-1)} // 이전 페이지로 돌아가기
          className="mb-4"
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          좌석 선택으로 돌아가기
        </Button>
        <h1 className="text-3xl font-bold text-center">결제하기</h1>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        {/* 예약 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>예약 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-gray-500" />
                <div>
                  <div className="font-semibold">
                    {reservationData.eventName || "콘서트 2024"}
                  </div>
                  <div className="text-sm text-gray-600">
                    {reservationData.date || "2024년 12월 25일"}
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <Clock className="w-4 h-4 text-gray-500" />
                <div className="text-sm">{reservationData.time || "19:30"}</div>
              </div>

              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4 text-gray-500" />
                <div className="text-sm">{reservationData.venue || "올림픽공원 체조경기장"}</div>
              </div>
            </div>

            <Separator />

            <div>
              <h3 className="font-semibold mb-2">선택된 좌석</h3>
              <div className="flex flex-wrap gap-2 mb-3">
                {reservationData.seats.map((seat) => (
                  <Badge key={seat.id} variant="secondary">
                    {getRowLabel(seat.rowIndex)}
                    {seat.columnIndex}
                  </Badge>
                ))}
              </div>
              <div className="text-sm text-gray-600">
                총 {reservationData.seats.length}석
              </div>
            </div>

            <Separator />

            <div className="space-y-2">
              <div className="flex justify-between">
                <span>좌석료 ({reservationData.seats.length}석)</span>
                <span>
                  {(reservationData.seats.length * SEAT_PRICE).toLocaleString()}
                  원
                </span>
              </div>
              <div className="flex justify-between">
                <span>예매수수료</span>
                <span>2,000원</span>
              </div>
              <Separator />
              <div className="flex justify-between font-bold text-lg">
                <span>총 결제금액</span>
                <span>{(totalAmount + 2000).toLocaleString()}원</span>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 결제 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>결제 정보</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* 고객 정보 */}
            <div className="space-y-4">
              <h3 className="font-semibold">고객 정보</h3>
              <div className="space-y-3">
                <div>
                  <Label htmlFor="name">이름 *</Label>
                  <Input
                    id="name"
                    value={customerInfo.name}
                    onChange={(e) =>
                      setCustomerInfo((prev) => ({
                        ...prev,
                        name: e.target.value,
                      }))
                    }
                    placeholder="홍길동"
                  />
                </div>
                <div>
                  <Label htmlFor="phone">휴대폰 번호 *</Label>
                  <Input
                    id="phone"
                    value={customerInfo.phone}
                    onChange={(e) =>
                      setCustomerInfo((prev) => ({
                        ...prev,
                        phone: e.target.value,
                      }))
                    }
                    placeholder="010-1234-5678"
                  />
                </div>
                <div>
                  <Label htmlFor="email">이메일 *</Label>
                  <Input
                    id="email"
                    type="email"
                    value={customerInfo.email}
                    onChange={(e) =>
                      setCustomerInfo((prev) => ({
                        ...prev,
                        email: e.target.value,
                      }))
                    }
                    placeholder="example@email.com"
                  />
                </div>
              </div>
            </div>

            <Separator />

            {/* 결제 방법 선택 */}
            <div className="space-y-4">
              <h3 className="font-semibold">결제 방법</h3>
              <RadioGroup
                value={paymentMethod}
                onValueChange={(value: "card" | "bank") =>
                  setPaymentMethod(value)
                }
              >
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="card" id="card" />
                  <Label
                    htmlFor="card"
                    className="flex items-center gap-2 cursor-pointer"
                  >
                    <CreditCard className="w-4 h-4" />
                    신용카드
                  </Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="bank" id="bank" />
                  <Label
                    htmlFor="bank"
                    className="flex items-center gap-2 cursor-pointer"
                  >
                    <Building2 className="w-4 h-4" />
                    무통장 입금
                  </Label>
                </div>
              </RadioGroup>
            </div>

            {/* 카드 결제 정보 */}
            {paymentMethod === "card" && (
              <div className="space-y-3">
                <div>
                  <Label htmlFor="cardNumber">카드 번호 *</Label>
                  <Input
                    id="cardNumber"
                    value={cardInfo.cardNumber}
                    onChange={(e) =>
                      setCardInfo((prev) => ({
                        ...prev,
                        cardNumber: e.target.value,
                      }))
                    }
                    placeholder="1234-5678-9012-3456"
                    maxLength={19}
                  />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <Label htmlFor="expiryDate">유효기간 *</Label>
                    <Input
                      id="expiryDate"
                      value={cardInfo.expiryDate}
                      onChange={(e) =>
                        setCardInfo((prev) => ({
                          ...prev,
                          expiryDate: e.target.value,
                        }))
                      }
                      placeholder="MM/YY"
                      maxLength={5}
                    />
                  </div>
                  <div>
                    <Label htmlFor="cvv">CVV *</Label>
                    <Input
                      id="cvv"
                      value={cardInfo.cvv}
                      onChange={(e) =>
                        setCardInfo((prev) => ({
                          ...prev,
                          cvv: e.target.value,
                        }))
                      }
                      placeholder="123"
                      maxLength={3}
                    />
                  </div>
                </div>
                <div>
                  <Label htmlFor="cardHolder">카드 소유자명 *</Label>
                  <Input
                    id="cardHolder"
                    value={cardInfo.cardHolder}
                    onChange={(e) =>
                      setCardInfo((prev) => ({
                        ...prev,
                        cardHolder: e.target.value,
                      }))
                    }
                    placeholder="HONG GIL DONG"
                  />
                </div>
              </div>
            )}

            {/* 무통장 입금 정보 */}
            {paymentMethod === "bank" && (
              <div className="bg-blue-50 p-4 rounded-lg">
                <h4 className="font-semibold mb-2">입금 계좌 정보</h4>
                <div className="space-y-1 text-sm">
                  <div>은행: 국민은행</div>
                  <div>계좌번호: 123456-78-901234</div>
                  <div>예금주: (주)티켓링크</div>
                  <div className="text-red-600 mt-2">
                    * 결제 후 24시간 이내 입금해주세요.
                  </div>
                </div>
              </div>
            )}

            {/* 결제 버튼 */}
            <Button
              className="w-full"
              size="lg"
              onClick={handlePayment}
              disabled={isProcessing}
            >
              {isProcessing
                ? "결제 처리 중..."
                : `${(totalAmount + 2000).toLocaleString()}원 결제하기`}
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
