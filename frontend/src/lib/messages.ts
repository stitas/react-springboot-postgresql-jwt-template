import type { ErrorKey } from "@/types/messageKeys";
import { ltErrors } from "@/i18n/errorMessages";

// Add more languages later
export type Locale = "lt";

const dictionaries: Record<Locale, Record<ErrorKey, string>> = {
  lt: ltErrors,
};

export function getErrorMessage(
  key: ErrorKey,
  locale: Locale = "lt"
): string {
  return dictionaries[locale][key];
}
