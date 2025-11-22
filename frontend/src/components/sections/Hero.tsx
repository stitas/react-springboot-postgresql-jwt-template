import { Section } from "@/components/common/Section";
import { motion } from "framer-motion";
import { Button } from "@/components/ui/button";
import { Sparkles } from "lucide-react";
import { LogoCarousel, Logo } from "@/components/common/LogoCarousel";

const logos: Logo[] = [
  {name: "Maxima", src: "/logos/maxima.webp"},
  {name: "Viada", src: "/logos/viada.png"},
  {name: "Lidl", src: "/logos/lidl.svg"},
  {name: "Depo", src: "/logos/depo.svg"},
  {name: "Circle K", src: "/logos/circlek.svg"},
  {name: "Iki", src: "/logos/iki.svg"},
  {name: "Senukai", src: "/logos/senukai.svg"},
  {name: "Rimi", src: "/logos/rimi.png"},
];

export const Hero: React.FC = () => {
  return (
    <>
      <Section className="pt-28 pb-28 md:pt-40 md:pb-20 bg-gradient-to-b from-[var(--color-primary)] via-[color-mix(in_oklch,var(--color-primary),var(--color-background)_13%)] to-[var(--color-background)]">
        <div className="flex flex-col justify-center items-center text-center text-white">
          <motion.h1
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="text-4xl md:text-5xl font-bold md:w-3/4"
          >
            Lorem ipsum dolor sit amet, consectetur adipiscing.
          </motion.h1>
          <p className="mt-4 text-base md:text-lg  max-w-prose">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla venenatis non nunc non auctor. Nullam nec dui.
          </p>
          <div className="mt-6 flex sm:flex-row gap-3">
            <Button size="lg" className="rounded-2xl"><Sparkles className="mr-2 h-4 w-4"/>
              <a href="sign-up">Pradėti nemokamai</a>
            </Button>
            <Button size="lg" variant="outline" className="rounded-2xl text-black">
              <a href="#features">Peržiūrėti demonstraciją</a>
            </Button>
          </div>
        </div>
      </Section>
      <div className="flex flex-col justify-center items-center text-center pt-16">
          <h5 className="text-md md:text-md font-bold text-foreground pb-3">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla venenatis</h5>
          <LogoCarousel logos={logos}/>
      </div>
    </>
    
  );
};
