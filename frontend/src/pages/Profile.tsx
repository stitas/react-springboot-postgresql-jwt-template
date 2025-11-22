import { useEffect, useState } from "react";
import { apiClient } from "@/lib/api";
import type { MeResponseDto } from "@/types/auth";

export default function ProfilePage() {
  const [body, setBody] = useState<string>("Loading...");

  useEffect(() => {
    apiClient
      .get<MeResponseDto>("/v1/auth/me")
      .then((data) => setBody(JSON.stringify(data, null, 2)));
  }, []);

  return (
    <div className="min-h-screen p-6">
      <pre>{body}</pre>
      <button
        type="button"
        onClick={async () => {
          await apiClient.post("/v1/auth/logout");
          window.location.href = "/";
        }}
        className="absolute right-0 top-1/4 grid place-items-center px-3 bg-primary hover:text-foreground"
      >
        Atsijungti
      </button>
    </div>
  );
}
