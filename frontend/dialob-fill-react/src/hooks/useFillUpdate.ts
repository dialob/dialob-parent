import { useFillSession } from "./useFillSession";
import { Session } from "@dialob/fill-api";
import { useEffect, useRef } from "react";

export function useFillUpdate(updateFn: (session: Session) => void) {
  const session = useFillSession();
  const updateRef = useRef(updateFn);

  useEffect(() => {
    updateRef.current = updateFn;
  }, [updateFn]);

  useEffect(() => {
    const listener = () => {
      updateRef.current(session);
    };

    session.on('update', listener);
    listener();

    return () => {
      session.removeListener('update', listener);
    }
  });
}
