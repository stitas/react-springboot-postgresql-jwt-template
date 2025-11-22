import { Section } from "@/components/common/Section";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";

export const Faq: React.FC = () => {
  return (
    <Section id="faq" className="relative py-16 md:py-16">
    <div className="absolute inset-x-0 -top-10 h-10 " />
    <div className="text-center mb-8">
        <h2 className="text-3xl md:text-4xl font-semibold tracking-tight">Dažniausiai užduodami klausimai</h2>
        <p className="mt-2 text-muted-foreground">Viskas, ką norėjote sužinoti apie lorem ipsum.</p>
    </div>

    <div className="max-w-3xl mx-auto">
        <Accordion type="single" collapsible className="w-full [--radius:1rem]">
        {/* Make each item look like a card */}
        <div className="space-y-3">
            <AccordionItem value="item-1" className="border rounded-xl bg-white shadow-sm">
                <AccordionTrigger className="px-4 py-3">Question</AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                    Answer
                </AccordionContent>
            </AccordionItem>

            <AccordionItem value="item-2" className="border rounded-xl bg-white shadow-sm">
                <AccordionTrigger className="px-4 py-3">Question</AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                  Answer
                </AccordionContent>
            </AccordionItem>

            <AccordionItem value="item-3" className="border rounded-xl bg-white shadow-sm">
                <AccordionTrigger className="px-4 py-3">Question </AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                 Answer
                </AccordionContent>
            </AccordionItem>

            <AccordionItem value="item-4" className="border rounded-xl bg-white shadow-sm">
                <AccordionTrigger className="px-4 py-3">Question</AccordionTrigger>
                <AccordionContent className="px-4 pb-4">
                 Answer
                </AccordionContent>
            </AccordionItem>
        </div>
        </Accordion>
    </div>
    </Section>
  );
};