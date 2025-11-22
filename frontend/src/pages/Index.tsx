import { Header } from "@/components/layout/Header";
import { Hero } from "@/components/sections/Hero";
import { Features } from "@/components/sections/Features";
import { Faq } from "@/components/sections/Faq";
import { FooterCta } from "@/components/sections/FooterCta";
import Pricing from "@/components/sections/Pricing";

export default function IndexPage() {
  return (
    <div className="min-h-screen bg-background text-foreground">
      <Header />
      <main>
        <Hero />
        <Features />
        <Pricing />
        <Faq />
        <FooterCta />
      </main>
    </div>
  );
}