export type AuthRequestDto = {
  email: string;
  password: string;
}

export type AuthResponseDto = {
  token: string;
}

export type SendPasswordResetEmailRequestDto = {
  email: string;
}

export type PasswordResetRequestDto = {
  password: string;
  token: string;
}

export type MeResponseDto = {
  id: number;
  email: string;
  role: string;
}
