import { Section } from "@/components/common/Section";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle2, Sparkles } from "lucide-react";

export const FooterCta: React.FC = () => {
  return (
    <Section id="cta" className="py-16 md:py-16">
      <Card className="rounded-2xl overflow-hidden bg-[var(--color-primary)]">
        <CardContent className="p-0">
          <div className="grid md:grid-cols-2">
            <div className="p-8 md:p-12">
              <h3 className="text-2xl md:3xl font-semibold text-white">Pasiruošę lorem ipsum?</h3>
              <p className="mt-2 text-white">Lorem ipsum šiandien. Pradžia nemokama.</p>
              <div className="mt-6 flex flex-col sm:flex-row gap-3">
                <Button asChild size="lg" variant="outline" className="rounded-2xl text-black">
                  <a href="/sign-up"><Sparkles className="mr-2 h-4 w-4"/>Sukurti Lorme ipsum</a>
                </Button>
              </div>
            </div>
            <div className="p-8 md:p-12 flex items-center justify-center text-white">
              <ul className="space-y-6 text-sm">
                <li className="flex items-center gap-4 text-2xl font-bold"><CheckCircle2 className="h-8 w-8"/> FREE feature 1</li>
                <li className="flex items-center gap-4 text-2xl font-bold"><CheckCircle2 className="h-8 w-8"/> FREE feature 2</li>
                <li className="flex items-center gap-4 text-2xl font-bold"><CheckCircle2 className="h-8 w-8"/> FREE feature 3</li>
              </ul>
            </div>
          </div>
        </CardContent>
      </Card>

      <footer className="mt-12 py-10 border-t text-sm text-muted-foreground">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-foreground">
            <span>Name - desc</span>
          </div>
          <div className="flex items-center gap-5">
            <a href="#features" className="hover:text-foreground">Funkcijos</a>
            <a href="#faq" className="hover:text-foreground">DUK</a>
            <a href="#" className="hover:text-foreground">Privatumas</a>
            <a href="#" className="hover:text-foreground">Taisyklės</a>
          </div>
        </div>
      </footer>
    </Section>
  );
};