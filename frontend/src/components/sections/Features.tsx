import { Section } from "@/components/common/Section";
import { motion } from "framer-motion";
import { Badge } from "@/components/ui/badge";
import PlyrVideoCard from "@/components/common/VideoPlayer";

export const Features: React.FC = () => {
  return (
    <Section id="features" className="py-16 md:py-24">
      <div className="text-center mb-10">
        <Badge
          variant="secondary"
          className="rounded-full bg-[var(--color-primary)] text-white"
        >
          Kodėl žmonės renkasi Lorem
        </Badge>
        <h2 className="mt-3 text-3xl md:text-4xl font-semibold tracking-tight">
          Lorem ipsum dolor sit amet consectetur adipisicing elit.
        </h2>
        <p className="mt-2 text-muted-foreground">
          Lorem ipsum dolor sit amet consectetur adipisicing elit.
        </p>
      </div>

      {/* Large video player instead of feature grid */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.4 }}
        className="flex justify-center"
      >
        <div className="w-full max-w-5xl rounded-2xl overflow-hidden shadow-lg">
          <PlyrVideoCard
            title="Produkto demonstracija"
            src="/videos/demo.mp4"
            poster="/videos/demo-poster.jpg"
          />
        </div>
      </motion.div>
    </Section>
  );
};
