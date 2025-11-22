// src/i18n/errors-lt.ts
import type { ErrorKey } from "@/types/messageKeys";

export const ltErrors: Record<ErrorKey, string> = {
  wrongCredentials: "Klaidingi prisijungimo duomenys !",
  systemError: "Sistemos klaida. Bandykite vėliau dar kartą.",
  passwordResetSessionMissing:
    "Ši slaptažodžio atstatymo sesija neegzistuoja. Sukurkite naują slaptažodžio atstatymo prašymą.",
  passwordResetSessionExpired:
    "Ši slaptažodžio atstatymo sesija yra pasenusi. Sukurkite naują slaptažodžio atstatymo prašymą.",
  emailMissing: "Toks el. paštas neegzistuoja.",
  emailAlreadyExists: "Vartotojas su tokiu el. paštu jau egzistuoja !",
};
