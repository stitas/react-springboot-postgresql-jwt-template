import React, { useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Eye, EyeOff } from "lucide-react";
import { apiClient, ApiError } from "@/lib/api";
import type { PasswordResetRequestDto } from "@/types/auth";
import { toast } from "sonner";
import { useParams } from "react-router-dom";
import { Toaster } from "@/components/ui/sonner";
import { getErrorMessage } from "@/lib/messages";

export default function PasswordResetPage() {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [form, setForm] = useState({ password: "", confirm: "" });
  const [errors, setErrors] = useState<{ email?: string; password?: string; confirm?: string }>({});
  const [submitting, setSubmitting] = useState(false);
  const { token } = useParams();


  function validate() {
    const next: typeof errors = {};
    if (form.password.length < 8) next.password = "Slaptažodis turi būti bent 8 simbolių";
    if (form.password !== form.confirm) next.confirm = "Slaptažodžiai nesutampa";
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!validate()) return;

    if(token == null) {
      toast.warning(getErrorMessage("systemError"));
      return;
    }

    const payload: PasswordResetRequestDto = { password: form.password, token: token};

    setSubmitting(true);

    try {
      await apiClient.post("/v1/auth/password-reset", {
        json: payload
      });
    } catch (err) {
      if (err instanceof ApiError) {
        switch (err.response.status) {
          case 404:
            toast.warning(getErrorMessage("passwordResetSessionMissing"));
            setSubmitting(false);
            return;
          case 403:
            toast.warning(getErrorMessage("passwordResetSessionExpired"));
            setSubmitting(false);
            return;
          default:
            toast.warning(getErrorMessage("systemError"));
            setSubmitting(false);
            return;
        }
      }
    }

    setSubmitting(false);
    window.location.assign("/");
  }

  return (
    <div className="min-h-screen grid place-items-center bg-[var(--color-muted)] px-4 py-10">
      <Card className="w-full max-w-md rounded-2xl shadow-lg">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-semibold">Pakeisti slaptažodį</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={onSubmit}>
            <div className="grid gap-2">
              <Label htmlFor="password">Naujas slaptažodis</Label>
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
            
            <Button type="submit" isSubmitting={submitting} className="w-full h-11 rounded-xl cursor-pointe text-white disabled:opacity-70 disabled:cursor-not-allowed">
                Pakeisti slaptažodį
            </Button>
          </form>
        </CardContent>
      </Card>
      <Toaster richColors position="top-center"/>
    </div>
  );
}
