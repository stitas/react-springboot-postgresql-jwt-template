import React from "react";
import { Section } from "@/components/common/Section";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, Minus } from "lucide-react";

const Feature: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <li className="flex items-start gap-2 text-sm">
    <CheckCircle2 className="mt-0.5 h-4 w-4 text-[var(--color-primary)]" />
    <span>{children}</span>
  </li>
);

const TbaItem: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <li className="flex items-start gap-2 text-sm text-muted-foreground">
    <Minus className="mt-0.5 h-4 w-4 text-[var(--color-primary)" />
    <span>{children}</span>
  </li>
);

export default function Pricing() {
  return (
    <Section id="pricing" className="py-16 md:py-24 bg-[var(--color-primary)]">
      <div className="text-center mb-10">
        <Badge className="rounded-full bg-[var(--color-secondary)] text-[var(--color-foreground)]">Kainodara</Badge>
        <h2 className="mt-3 text-3xl md:text-4xl font-semibold text-white">Išsirinkite jums tinkamą planą</h2>
        <p className="mt-2 text-white">Pradėkite nemokamai, o kai prireiks – lengvai išplėskite galimybes.</p>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Free */}
        <Card className="rounded-2xl">
          <CardHeader>
            <CardTitle className="text-xl">Nemokama</CardTitle>
            <div className="text-3xl font-semibold">€0</div>
            <p className="text-sm text-muted-foreground">Idealu startui ir mažesniam renginiui</p>
          </CardHeader>
          <CardContent className="space-y-5">
            <ul className="space-y-2">
              <Feature>FEATURE 1</Feature>
              <Feature>FEATURE 2</Feature>
              <Feature>FEATURE 3</Feature>
              <Feature>FEATURE 4</Feature>
              <Feature>FEATURE 5</Feature>
            </ul>
            <Button className="w-full rounded-xl cursor-pointer text-white" asChild>
              <a href="/sign-up">Pradėti nemokamai</a>
            </Button>
          </CardContent>
        </Card>

        {/* Standart */}
        {/* border-rose-300 ring-1 ring-rose-200 shadow-sm */}
        <Card className="rounded-2xl"> 
          <CardHeader>
            <div className="flex items-center gap-2">
              <CardTitle className="text-xl">Standartas</CardTitle>
              {/* <Badge className="bg-rose-600 text-white">Populiariausias</Badge> */}
            </div>
            <div className="text-3xl font-semibold italic">Jau greitai</div>
            <p className="text-sm text-muted-foreground">Didesniems renginiams ir papildomoms funkcijoms</p>
          </CardHeader>
          <CardContent className="space-y-5">
            <ul className="space-y-2">
              <TbaItem>Bus paskelbta</TbaItem>
              <TbaItem>Bus paskelbta</TbaItem>
              <TbaItem>Bus paskelbta</TbaItem>
            </ul>
            <Button className="w-full rounded-xl cursor-pointer text-white" asChild>
              <a href="#">Netrukus</a>
            </Button>
          </CardContent>
        </Card>

        {/* Premium */}
        <Card className="rounded-2xl">
          <CardHeader>
            <CardTitle className="text-xl">Premium</CardTitle>
            <div className="text-3xl font-semibold italic">Jau greitai</div>
            <p className="text-sm text-muted-foreground">Profesionalioms reikmėms ir maksimaliam lankstumui</p>
          </CardHeader>
          <CardContent className="space-y-5">
            <ul className="space-y-2">
              <TbaItem>Bus paskelbta</TbaItem>
              <TbaItem>Bus paskelbta</TbaItem>
              <TbaItem>Bus paskelbta</TbaItem>
            </ul>
            <Button className="w-full rounded-xl cursor-pointer text-white" asChild>
              <a href="#">Netrukus</a>
            </Button>
          </CardContent>
        </Card>
      </div>
    </Section>
  );
}
