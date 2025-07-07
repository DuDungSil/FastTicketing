export type AuthUser = {
  userId: number;
  loginTime: string;
};

export const getAuthUser = (): AuthUser | null => {
  const isLoggedIn = localStorage.getItem("isLoggedIn");
  const userId = localStorage.getItem("userId");
  const loginTime = localStorage.getItem("loginTime");

  if (isLoggedIn === "true" && userId && loginTime) {
    return {
      userId: parseInt(userId),
      loginTime,
    };
  }

  return null;
};

export const isAuthenticated = (): boolean => {
  return getAuthUser() !== null;
};

export const logout = (): void => {
  localStorage.removeItem("isLoggedIn");
  localStorage.removeItem("userId");
  localStorage.removeItem("loginTime");
  
  // 큐 관련 정보도 제거
  localStorage.removeItem("queueToken");
  localStorage.removeItem("ticketOpenId");
  localStorage.removeItem("reservationData");
};

export const getFormattedLoginTime = (): string => {
  const user = getAuthUser();
  if (!user) return "";

  const loginDate = new Date(user.loginTime);
  return loginDate.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};