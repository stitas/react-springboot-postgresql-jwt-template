import { Button } from "@/components/ui/button";
import { apiClient } from "@/lib/api";
import { useEffect, useState } from "react";
import { Menu, X } from "lucide-react";

export const Header: React.FC = () => {
  const [loginRedirect, setLoginRedirect] = useState("/login");
  const [mobileOpen, setMobileOpen] = useState(false);

  // figure out where "Prisijungti" should go
  useEffect(() => {
    (async () => {
      try {
        await apiClient.get("/v1/auth/me", { skipRedirectOn401: true });
        setLoginRedirect("/profile");
      }
      catch {
        setLoginRedirect("/login");
      }
    })();
  }, []);

  const closeMobile = () => setMobileOpen(false);

  return (
    <header className="fixed inset-x-0 top-0 z-50 flex justify-center">
      <div
        className="
          relative mt-3
          w-full max-w-7xl
          mx-2 sm:mx-4
          px-4 sm:px-6 lg:px-8
          h-14 md:h-16
          flex items-center justify-between
          rounded-full border bg-white/80 backdrop-blur shadow-sm
        "
      >
        <a href="#" className="flex items-center gap-2">
          <span className="font-semibold text-lg md:text-xl tracking-tight">
            TITLE
          </span>
        </a>

        <nav className="hidden md:flex absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 items-center gap-6 text-sm">
          <a
            href="#features"
            className="hover:text-foreground/80 text-foreground/70"
          >
            Funkcijos
          </a>
          <a
            href="#pricing"
            className="hover:text-foreground/80 text-foreground/70"
          >
            Kainos
          </a>
          <a
            href="#faq"
            className="hover:text-foreground/80 text-foreground/70"
          >
            DUK
          </a>
        </nav>

        <div className="flex items-center gap-2">
          <Button
            asChild
            variant="ghost"
            className="hidden sm:inline-flex md:inline-flex"
          >
            <a href={loginRedirect}>Prisijungti</a>
          </Button>
          <Button asChild className="hidden sm:inline-flex md:inline-flex rounded-2xl">
            <a href="sign-up">Registruotis</a>
          </Button>

          <button
            type="button"
            className="md:hidden inline-flex items-center justify-center rounded-full p-2 border bg-white/80 hover:bg-white"
            onClick={() => setMobileOpen((open) => !open)}
            aria-label="Meniu"
          >
            {mobileOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
      </div>

      {mobileOpen && (
        <div className="fixed inset-x-0 top-[4.5rem] flex justify-center md:hidden">
          <div className="w-full max-w-7xl px-4 sm:px-6 lg:px-8">
            <div className="rounded-2xl border bg-white/95 shadow-lg py-3 px-4 flex flex-col gap-3 text-sm">
              <a
                href="#features"
                className="hover:text-foreground/80 text-foreground/70"
                onClick={closeMobile}
              >
                Funkcijos
              </a>
              <a
                href="#pricing"
                className="hover:text-foreground/80 text-foreground/70"
                onClick={closeMobile}
              >
                Kainos
              </a>
              <a
                href="#faq"
                className="hover:text-foreground/80 text-foreground/70"
                onClick={closeMobile}
              >
                DUK
              </a>

              <div className="flex flex-col gap-2 pt-2">
                <Button asChild className="w-full">
                  <a href={loginRedirect} onClick={closeMobile}>
                    Prisijungti
                  </a>
                </Button>
                <Button asChild className="w-full">
                  <a href="sign-up" onClick={closeMobile}>
                    Registruotis
                  </a>
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};
