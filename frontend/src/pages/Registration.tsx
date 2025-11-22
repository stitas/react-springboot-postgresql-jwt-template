import React, { useState } from "react";
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { Eye, EyeOff, Mail } from "lucide-react";
import { OAuthButtons } from "@/components/layout/OAuthButtons";
import { apiClient, ApiError } from "@/lib/api";
import type { AuthRequestDto, AuthResponseDto } from "@/types/auth";
import { Toaster } from "@/components/ui/sonner";
import { toast } from "sonner";
import { getErrorMessage } from "@/lib/messages";

export default function RegistrationPage() {
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [form, setForm] = useState({ email: "", password: "", confirm: "" });
  const [errors, setErrors] = useState<{ email?: string; password?: string; confirm?: string }>({});
  const [submitting, setSubmitting] = useState(false);

  function validate() {
    const next: typeof errors = {};
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) next.email = "Įveskite galiojantį el. paštą";
    if (form.password.length < 8) next.password = "Slaptažodis turi būti bent 8 simbolių";
    if (form.password !== form.confirm) next.confirm = "Slaptažodžiai nesutampa";
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!validate()) return;

    const payload: AuthRequestDto = { email: form.email, password: form.password };
    let response: AuthResponseDto | null = null;

    try {
      setSubmitting(true);
      response = await apiClient.post("/v1/auth/register", {
        json: payload
      });
      await new Promise(r => setTimeout(r, 2000));
    } catch (err) {
      if (err instanceof ApiError) {
        switch (err.response.status) {
          case 409:
            toast.warning(getErrorMessage("emailAlreadyExists"));
            break;
          default:
            toast.warning(getErrorMessage("systemError"));
            break;
        }
      }
    } finally {
      setSubmitting(false);
      if(response != null) {
        window.location.href = "/";
      }
    }
  }

  function handleGoogle() {
    window.location.href = `${API_BASE_URL}/v1/auth/oauth/google`;
  }

  return (
    <div className="min-h-screen grid place-items-center bg-[var(--color-muted)] px-4 py-10">
      <Card className="w-full max-w-md rounded-2xl shadow-lg">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-semibold">Sukurti paskyrą</CardTitle>
          <CardDescription className="mt-1">Registruokitės el. paštu arba tęskite su Google.</CardDescription>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={onSubmit}>
            <div className="grid gap-2">
              <Label htmlFor="email">El. paštas</Label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  id="email"
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  required
                  className="pl-10 h-11"
                  placeholder="vardas@pavyzdys.lt"
                />
              </div>
              {errors.email && <p className="text-sm text-destructive">{errors.email}</p>}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="password">Slaptažodis</Label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                  required
                  className="pr-10 h-11"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  aria-label={showPassword ? "Slėpti slaptažodį" : "Rodyti slaptažodį"}
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute inset-y-0 right-0 grid place-items-center px-3 text-muted-foreground hover:text-foreground"
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              {errors.password && <p className="text-sm text-destructive">{errors.password}</p>}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="confirm">Pakartokite slaptažodį</Label>
              <div className="relative">
                <Input
                  id="confirm"
                  type={showConfirm ? "text" : "password"}
                  value={form.confirm}
                  onChange={(e) => setForm({ ...form, confirm: e.target.value })}
                  required
                  className="pr-10 h-11"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  aria-label={showConfirm ? "Slėpti slaptažodį" : "Rodyti slaptažodį"}
                  onClick={() => setShowConfirm((v) => !v)}
                  className="absolute inset-y-0 right-0 grid place-items-center px-3 text-muted-foreground hover:text-foreground"
                >
                  {showConfirm ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              {errors.confirm && <p className="text-sm text-destructive">{errors.confirm}</p>}
            </div>

            

            <Button type="submit" isSubmitting={submitting} className="w-full h-11 rounded-xl cursor-pointer text-white disabled:opacity-70 disabled:cursor-not-allowed">
              "Sukurti paskyrą"
            </Button>
          </form>

          <div className="my-6 relative">
            <Separator />
            <span className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 bg-card px-3 text-xs text-muted-foreground">arba</span>
          </div>
          
          <OAuthButtons onGoogle={handleGoogle} disabled={submitting} />
        </CardContent>

        <CardFooter className="flex items-center justify-between text-sm text-muted-foreground">
          <span>Jau turite paskyrą?</span>
          <a className="text-[var(--color-primary)] hover:underline" href="/login">Prisijunkite</a>
        </CardFooter>
      </Card>
      <Toaster richColors position="top-center"/>
    </div>
  );
}
