import { Link, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { AlertTriangle, ArrowLeft, Home } from "lucide-react";

export default function NotFound() {
  const { pathname } = useLocation();

  return (
    <main className="min-h-screen grid place-items-center bg-[var(--color-muted)]">
      <Card className="w-full max-w-lg rounded-2xl shadow-sm">
        <CardHeader className="text-center">
          <div className="mx-auto mb-2 grid h-12 w-12 place-items-center rounded-full bg-[var(--color-primary)] text-[var(--color-secondary)]">
            <AlertTriangle className="h-6 w-6" />
          </div>
          <CardTitle className="text-2xl">404 – Puslapis nerastas</CardTitle>
          <p className="text-sm text-muted-foreground">
            Maršrutas <span className="font-mono">{pathname}</span> neegzistuoja.
          </p>
        </CardHeader>
        <CardContent className="space-y-4 text-center">
          <p className="text-sm text-muted-foreground">
          </p>

          <div className="flex items-center justify-center gap-2">
            <Button asChild variant="outline" className="rounded-xl">
              <Link to={-1 as unknown as string}>
                <ArrowLeft className="mr-2 h-4 w-4" />
                Atgal
              </Link>
            </Button>

            <Button asChild className="rounded-xl">
              <Link to="/">
                <Home className="mr-2 h-4 w-4" />
                Į pradžią
              </Link>
            </Button>
          </div>
        </CardContent>
      </Card>
    </main>
  );
}
