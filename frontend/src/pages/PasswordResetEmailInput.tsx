import React, { useState } from "react";
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Mail } from "lucide-react";
import { Toaster } from "@/components/ui/sonner";
import { toast } from "sonner";
import { apiClient, ApiError } from "@/lib/api";
import type { SendPasswordResetEmailRequestDto } from "@/types/auth";
import { EMAIL_SENT_MSG } from "@/i18n/infoMessages";
import { getErrorMessage } from "@/lib/messages";

export default function PasswordResetEmailPage() {
  const [form, setForm] = useState({ email: "" });
  const [errors, setErrors] = useState<{ email?: string; password?: string; confirm?: string }>({});
  const [submitting,  setSubmitting] = useState(false);

  function validate() {
    const next: typeof errors = {};
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) next.email = "Įveskite galiojantį el. paštą";
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!validate()) return;

    const payload: SendPasswordResetEmailRequestDto = { email: form.email };

    setSubmitting(true);

    try {
      await apiClient.post("/v1/auth/password-reset-request", {
          json: payload
      });
    } catch (err) {
        if (err instanceof ApiError) {
          switch (err.response.status) {
            case 404:
              toast.warning(getErrorMessage("emailMissing"));
              return;
            default:
              toast.warning(getErrorMessage("systemError"));
              return;
          }
        }
    }

    setSubmitting(false);
    toast.info(EMAIL_SENT_MSG);
  }

  return (
    <div className="min-h-screen grid place-items-center bg-[var(--color-muted)] px-4 py-10">
      <Card className="w-full max-w-md rounded-2xl shadow-lg">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-semibold">Įveskite savo el. paštą</CardTitle>
          <CardDescription className="mt-1">Į pateiktą el. paštą atsiūsime nuorodą į slaptažodžio keitimo puslapį</CardDescription>
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
            
            <Button type="submit" isSubmitting={submitting} className="w-full h-11 rounded-xl cursor-pointer text-white disabled:opacity-70 disabled:cursor-not-allowed">
              Siūsti
            </Button>
          </form>
        </CardContent>
      </Card>
      <Toaster richColors position="top-center"/>
    </div>
  );
}
