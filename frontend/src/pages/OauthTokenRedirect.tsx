import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { apiClient } from "@/lib/api";
import { Spinner } from "@/components/ui/spinner";

export default function OauthTokenRedirect() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");

  useEffect(() => {
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    apiClient.setAccessToken(token);
    navigate("/", { replace: true });
  }, [navigate, token]);

  return (
    <main className="min-h-screen grid place-items-center bg-[var(--color-muted)] px-4">
      <div className="flex flex-col items-center gap-3 text-center">
        <Spinner className="size-8 text-[var(--color-primary)]" />
        <p className="text-sm text-muted-foreground">Finalizuojamas prisijungimas...</p>
      </div>
    </main>
  );
}
