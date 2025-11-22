import type { AuthResponseDto } from "@/types/auth";

type LogoutHandler = () => void;

let logoutHandler: LogoutHandler | null = null;

export function bindLogout(handler: LogoutHandler) {
  logoutHandler = handler;
}

const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/+$/, "") ?? "";
const LOGIN_ROUTE =
  (import.meta.env.VITE_LOGIN_ROUTE as string | undefined) ?? "/login";
const ACCESS_TOKEN_STORAGE_KEY =
  (import.meta.env.VITE_ACCESS_TOKEN_STORAGE_KEY as string | undefined) ?? "accessToken";
const LOGIN_PATH =
  (import.meta.env.VITE_LOGIN_PATH as string | undefined) ?? "/v1/auth/login";
const REGISTER_PATH =
  (import.meta.env.VITE_REGISTER_PATH as string | undefined) ?? "/v1/auth/register";
const REFRESH_PATH =
  (import.meta.env.VITE_REFRESH_PATH as string | undefined) ?? "/v1/auth/refresh";
const LOGOUT_PATH =
  (import.meta.env.VITE_LOGOUT_PATH as string | undefined) ?? "/v1/auth/logout";
const CSRF_PREFLIGHT_PATH =
  (import.meta.env.VITE_CSRF_PREFLIGHT_PATH as string | undefined) ?? "/v1/auth/csrf";

const AUTHORIZATION_HEADER = "Authorization";
const BEARER_PREFIX = "Bearer ";
const X_REQUESTED_WITH = "X-Requested-With";
const XML_HTTP_REQUEST = "XMLHttpRequest";
const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

let redirectingToLogin = false;
let accessTokenCache: string | null | undefined = undefined;
let refreshPromise: Promise<string | null> | null = null;
let csrfTokenCache: string | null = null;
let csrfFetchPromise: Promise<string> | null = null;

export class ApiError extends Error {
  readonly response: Response;
  constructor(message: string, response: Response) {
    super(message);
    this.name = "ApiError";
    this.response = response;
  }
}

interface RequestOptions extends RequestInit {
  json?: unknown;
  parseJson?: boolean;
  skipAuth?: boolean;
  skipRefresh?: boolean;
  skipRedirectOn401?: boolean;
}

function resolveUrl(path: string): string {
  if (/^https?:\/\//i.test(path)) return path;
  if (!API_BASE_URL) return path.startsWith("/") ? path : `/${path}`;
  return `${API_BASE_URL}${path.startsWith("/") ? path : `/${path}`}`;
}

function normalizePath(path: string): string {
  try {
    const base =
      API_BASE_URL ||
      (typeof window !== "undefined" ? window.location.origin : "http://localhost");
    return new URL(path, base).pathname;
  } catch {
    return path;
  }
}

const NORMALIZED_LOGIN_PATH = normalizePath(resolveUrl(LOGIN_PATH));
const NORMALIZED_REGISTER_PATH = normalizePath(resolveUrl(REGISTER_PATH));
const NORMALIZED_REFRESH_PATH = normalizePath(resolveUrl(REFRESH_PATH));
const NORMALIZED_LOGOUT_PATH = normalizePath(resolveUrl(LOGOUT_PATH));

const AUTH_FREE_PATHS = new Set([NORMALIZED_LOGIN_PATH, NORMALIZED_REGISTER_PATH, NORMALIZED_REFRESH_PATH]);

function readCookie(name: string): string | null {
  if (typeof document === "undefined") return null;
  const cookieString = document.cookie;
  if (!cookieString) return null;
  const prefix = `${encodeURIComponent(name)}=`;
  for (const c of cookieString.split(";").map((s) => s.trim())) {
    if (c.startsWith(prefix)) return decodeURIComponent(c.substring(prefix.length));
  }
  return null;
}

async function refreshCsrfToken(): Promise<string> {
  const existing = readCookie(CSRF_COOKIE_NAME);
  if (existing) {
    csrfTokenCache = existing;
    return existing;
  }

  if (!csrfFetchPromise) {
    csrfFetchPromise = (async () => {
      let lastError: unknown = null;
      try {
        const response = await fetch(resolveUrl(CSRF_PREFLIGHT_PATH), {
          method: "GET",
          credentials: "include",
          headers: { [X_REQUESTED_WITH]: XML_HTTP_REQUEST },
        });
        if (!response.ok && response.status !== 204) {
          lastError = new Error(`CSRF preflight failed with status ${response.status}`);
        }
      } catch (err) {
        lastError = err;
      }

      const token = readCookie(CSRF_COOKIE_NAME);
      csrfTokenCache = token;
      if (!token) throw lastError ?? new Error("Unable to obtain CSRF token.");
      return token;
    })().finally(() => {
      csrfFetchPromise = null;
    });
  }

  return csrfFetchPromise;
}

async function ensureCsrfToken(): Promise<string> {
  if (csrfTokenCache) return csrfTokenCache;
  const token = await refreshCsrfToken();
  csrfTokenCache = token;
  return token;
}

function readStoredAccessToken(): string | null {
  if (accessTokenCache !== undefined) return accessTokenCache;
  if (typeof window === "undefined") {
    accessTokenCache = null;
    return null;
  }
  try {
    const stored = window.localStorage.getItem(ACCESS_TOKEN_STORAGE_KEY);
    accessTokenCache = stored ?? null;
    return accessTokenCache;
  } catch {
    accessTokenCache = null;
    return null;
  }
}

function storeAccessToken(token: string | null) {
  accessTokenCache = token ?? null;
  if (typeof window === "undefined") return;
  try {
    if (token) {
      window.localStorage.setItem(ACCESS_TOKEN_STORAGE_KEY, token);
    } else {
      window.localStorage.removeItem(ACCESS_TOKEN_STORAGE_KEY);
    }
  } catch {
    // Ignore storage errors (e.g. private mode)
  }
}

function getAccessToken(): string | null {
  return readStoredAccessToken();
}

function setAccessToken(token: string) {
  storeAccessToken(token);
}

function clearAccessToken() {
  storeAccessToken(null);
  csrfTokenCache = null;
}

function isTokenResponse(value: unknown): value is AuthResponseDto {
  return typeof value === "object" && value !== null && typeof (value as AuthResponseDto).token === "string";
}

async function handleRedirectOn401(response: Response, skipRedirect?: boolean) {
  if (response.status === 401 && !skipRedirect && !redirectingToLogin) {
    clearAccessToken();
    redirectingToLogin = true;
    try {
      logoutHandler?.();
    } finally {
      window.location.assign(LOGIN_ROUTE);
    }
  }
}

async function refreshAccessToken(): Promise<string | null> {
  if (refreshPromise) return refreshPromise;

  const resolvedUrl = resolveUrl(REFRESH_PATH);
  refreshPromise = (async () => {
    try {
      const csrfToken = await ensureCsrfToken();
      const headers = new Headers({ [X_REQUESTED_WITH]: XML_HTTP_REQUEST });
      headers.set(CSRF_HEADER_NAME, csrfToken);

      const response = await fetch(resolvedUrl, {
        method: "POST",
        credentials: "include",
        headers,
      });
      if (!response.ok) {
        clearAccessToken();
        return null;
      }

      const contentType = response.headers.get("Content-Type") ?? "";
      if (!contentType.includes("application/json")) {
        clearAccessToken();
        return null;
      }

      const payload = (await response.json()) as unknown;
      if (isTokenResponse(payload)) {
        storeAccessToken(payload.token);
        return payload.token;
      }

      clearAccessToken();
      return null;
    } catch {
      clearAccessToken();
      return null;
    } finally {
      refreshPromise = null;
    }
  })();

  return refreshPromise;
}

async function request<T = unknown>(path: string, options: RequestOptions = {}): Promise<T> {
  const {
    method: rawMethod = "GET",
    headers,
    body,
    json,
    parseJson = true,
    skipAuth = false,
    skipRefresh = false,
    skipRedirectOn401,
    credentials,
    ...rest
  } = options;

  const method = rawMethod.toUpperCase();
  const resolvedUrl = resolveUrl(path);
  const normalizedPath = normalizePath(resolvedUrl);

  const finalHeaders = new Headers(headers ?? undefined);
  let finalBody = body;

  console.log(finalHeaders);
  console.log(finalBody);

  if (json !== undefined) {
    if (body !== undefined) throw new Error("Use either `json` or `body`, but not both.");
    finalHeaders.set("Content-Type", "application/json");
    finalBody = JSON.stringify(json);
  }

  if (!finalHeaders.has(X_REQUESTED_WITH)) {
    finalHeaders.set(X_REQUESTED_WITH, XML_HTTP_REQUEST);
  }

  const shouldAttachAuth = !skipAuth && !AUTH_FREE_PATHS.has(normalizedPath);
  if (shouldAttachAuth) {
    const token = getAccessToken();
    if (token) {
      finalHeaders.set(AUTHORIZATION_HEADER, `${BEARER_PREFIX}${token}`);
    }
  }

  const response = await fetch(resolvedUrl, {
    method,
    headers: finalHeaders,
    credentials: credentials ?? "include",
    body: finalBody,
    ...rest,
  });

  if (response.status === 401) {
    const canAttemptRefresh = shouldAttachAuth && !skipRefresh;
    if (canAttemptRefresh) {
      const refreshed = await refreshAccessToken();
      if (refreshed) {
        return request<T>(path, { ...options, skipRefresh: true });
      }
    }
    if (shouldAttachAuth) {
      clearAccessToken();
    }
    await handleRedirectOn401(response, skipRedirectOn401);
  }

  console.log(response);

  if (!response.ok) {
    console.log("FAILED");
    throw new ApiError(`Request failed with status ${response.status}`, response);
  }

  // Successful logout should always purge client-side credentials.
  if (response.ok && normalizedPath === NORMALIZED_LOGOUT_PATH) {
    clearAccessToken();
  }

  if (!parseJson || response.status === 204) {
    return response as unknown as T;
  }

  const contentType = response.headers.get("Content-Type") ?? "";
  if (contentType.includes("application/json")) {
    const payload = (await response.json()) as unknown;
    if (isTokenResponse(payload)) {
      storeAccessToken(payload.token);
    }
    return payload as T;
  }

  const textPayload = await response.text();
  return textPayload as unknown as T;
}

function get<T = unknown>(path: string, options?: Omit<RequestOptions, "method">) {
  return request<T>(path, { ...options, method: "GET" });
}
function post<T = unknown>(path: string, options?: Omit<RequestOptions, "method">) {
  return request<T>(path, { ...options, method: "POST" });
}
function put<T = unknown>(path: string, options?: Omit<RequestOptions, "method">) {
  return request<T>(path, { ...options, method: "PUT" });
}
function patch<T = unknown>(path: string, options?: Omit<RequestOptions, "method">) {
  return request<T>(path, { ...options, method: "PATCH" });
}
function del<T = unknown>(path: string, options?: Omit<RequestOptions, "method">) {
  return request<T>(path, { ...options, method: "DELETE" });
}

export const apiClient = {
  request,
  get,
  post,
  put,
  patch,
  delete: del,
  refreshCsrfToken,
  refreshAccessToken,
  getAccessToken,
  setAccessToken,
  clearAccessToken,
};
