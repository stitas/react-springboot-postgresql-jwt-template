import React, { useEffect, useMemo, useRef } from "react";
import type { Options as PlyrOptions } from "plyr";
import "plyr/dist/plyr.css";

type Props = {
  title: string;
  src?: string; // self-hosted video
  poster?: string;
  type?: string; // default "video/mp4"
  provider?: "html5" | "youtube" | "vimeo";
  embedId?: string; // for youtube/vimeo
  colorMain?: string;
  className?: string;
  options?: PlyrOptions;
};

export default function PlyrVideoCard({
  title,
  src,
  poster,
  type = "video/mp4",
  provider = "html5",
  embedId,
  colorMain,
  options,
  className,
}: Props) {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const embedRef = useRef<HTMLDivElement | null>(null);
  const playerRef = useRef<Plyr | null>(null);

  useEffect(() => {
    let destroyed = false;
    let instance: Plyr | null = null;

    (async () => {
      const mod = (await import("plyr")) as typeof import("plyr");
      const PlyrClass = mod.default;

      const el = provider === "html5" ? videoRef.current : embedRef.current;
      if (!el || destroyed) return;

      if (playerRef.current) {
        try {
          playerRef.current.destroy();
        } catch (error) {
          // Log and ignore errors when destroying the previous player instance
          console.error("Failed to destroy previous Plyr instance", error);
        }
        playerRef.current = null;
      }

      const defaultOptions: PlyrOptions = {
        controls: ["play", "progress", "mute", "pip", "airplay", "fullscreen"],
        loadSprite: true,
      };

      instance = new PlyrClass(el, { ...defaultOptions, ...(options || {}) });
      playerRef.current = instance;
    })();

    return () => {
      destroyed = true;
      try {
        instance?.destroy();
      } catch (error) {
        // Log and ignore errors during cleanup
        console.error("Failed to destroy Plyr instance on cleanup", error);
      }
    };
  }, [provider, embedId, src, options]);

  const resolvedColorMain = colorMain ?? "var(--color-primary)";

  const style = useMemo<React.CSSProperties>(
    () =>
      ({
        "--plyr-color-main": resolvedColorMain,
      } as React.CSSProperties),
    [resolvedColorMain],
  );

  return (
    <div
      className={`relative w-full overflow-hidden ${className ?? ""}`}
      style={style}
    >
      {provider === "html5" ? (
        <video
          ref={videoRef}
          className="block w-full h-auto"
          playsInline
          controls
          preload="metadata"
          poster={poster}
        >
          {src ? <source src={src} type={type} /> : null}
          Your browser does not support the video tag.
        </video>
      ) : (
        <div
          ref={embedRef}
          className="block w-full h-auto"
          data-plyr-provider={provider}
          data-plyr-embed-id={embedId}
          aria-label={title}
        />
      )}
    </div>
  );
}
