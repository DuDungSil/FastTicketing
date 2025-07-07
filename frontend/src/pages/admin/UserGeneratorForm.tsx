import { useState } from "react";

export default function UserGeneratorForm() {
  const [count, setCount] = useState(10);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      const response = await fetch(`/api/users/dummy?count=${count}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const result = await response.text();
        setMessage(result);
      } else {
        setMessage("더미 유저 생성에 실패했습니다.");
      }
    } catch (error) {
      setMessage("서버 연결에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ marginTop: "10px" }}>
      <form onSubmit={handleSubmit} style={{ display: "flex", gap: "10px", alignItems: "center" }}>
        <input
          type="number"
          value={count}
          onChange={(e) => setCount(parseInt(e.target.value) || 1)}
          min="1"
          max="1000"
          style={{
            padding: "8px 12px",
            borderRadius: "6px",
            border: "2px solid rgba(255, 255, 255, 0.3)",
            backgroundColor: "rgba(255, 255, 255, 0.1)",
            color: "white",
            fontSize: "14px",
            width: "80px",
          }}
          placeholder="개수"
        />
        <button
          type="submit"
          disabled={loading}
          style={{
            padding: "8px 16px",
            borderRadius: "6px",
            border: "2px solid rgba(255, 255, 255, 0.3)",
            backgroundColor: "rgba(255, 255, 255, 0.2)",
            color: "white",
            fontSize: "14px",
            cursor: loading ? "not-allowed" : "pointer",
            opacity: loading ? 0.7 : 1,
          }}
        >
          {loading ? "생성 중..." : "생성"}
        </button>
      </form>
      
      {message && (
        <div style={{
          marginTop: "10px",
          padding: "8px 12px",
          borderRadius: "6px",
          backgroundColor: "rgba(255, 255, 255, 0.1)",
          color: "white",
          fontSize: "14px",
        }}>
          {message}
        </div>
      )}
    </div>
  );
}