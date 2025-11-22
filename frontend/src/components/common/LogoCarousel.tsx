import React from "react";
import AutoScroll from "embla-carousel-auto-scroll";
import {
  Carousel,
  CarouselContent,
  CarouselItem,
} from "@/components/ui/carousel";
import { Card } from "@/components/ui/card";

export type Logo = {
  name: string
  src: string
}

type LogoCarouselProps = {
  logos: Logo[]
  className?: string
}

export const LogoCarousel: React.FC<LogoCarouselProps> = ({
  logos,
  className,
}) => {
  const autoScroll = React.useRef(
    AutoScroll({
      speed: 1.2,
      stopOnInteraction: false,
      stopOnMouseEnter: false,
    })
  );

  return (
    <Carousel
      plugins={[autoScroll.current]}
      opts={{
        align: "start",
        loop: true,
        dragFree: true,
      }}
      className={className ?? "w-full max-w-5xl mx-auto overflow-hidden"}
    >
      <CarouselContent className="-ml-4">
        {logos.map((logo) => (
          <CarouselItem
            key={logo.name}
            className="basis-1/2 sm:basis-1/3 md:basis-1/4 lg:basis-1/6 pl-4"
          >
            <Card>
                <img
                    src={logo.src}
                    alt={logo.name}
                    className="h-12 pr-2 pl-2 w-auto object-contain"
                />
            </Card>
          </CarouselItem>
        ))}
      </CarouselContent>

      {/* no arrows */}
    </Carousel>
  );
};
