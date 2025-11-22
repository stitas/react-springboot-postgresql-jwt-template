import { Button } from "@/components/ui/button";
import { GoogleIcon } from "@/components/icons/GoogleIcon";

export function OAuthButtons({
  onGoogle,
  disabled,
}: { onGoogle: () => void; disabled?: boolean; }) {
  return (
    <div className="grid gap-3">
      <Button variant="outline" className="w-full h-11 rounded-xl cursor-pointer" onClick={onGoogle} disabled={disabled}>
        <GoogleIcon className="mr-2 h-5 w-5" /> Tęsti su Google
      </Button>
    </div>
  );
}
