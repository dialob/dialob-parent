import { useEffect, useRef } from 'react';

/**
 * Syncs staged saving state from global form when the global value changes externally
 * (e.g. ID rename in another dialog) and the user has not made local edits since baseline.
 */
export function useSyncStagingBaseline<T>(
  globalValue: T | undefined,
  stagedValue: T | undefined,
  reset: (value: T) => void,
  enabled: boolean
): void {
  const baselineRef = useRef<T | undefined>(globalValue);

  useEffect(() => {
    if (!enabled || stagedValue === undefined || globalValue === undefined) {
      return;
    }

    const globalChanged = JSON.stringify(globalValue) !== JSON.stringify(baselineRef.current);
    const userEdited = JSON.stringify(stagedValue) !== JSON.stringify(baselineRef.current);

    if (globalChanged && !userEdited) {
      reset(structuredClone(globalValue) as T);
    }

    if (globalChanged) {
      baselineRef.current = globalValue;
    }
  }, [globalValue, stagedValue, enabled, reset]);
}

/**
 * Captures a baseline snapshot when a dialog opens or its primary entity changes.
 */
export function useDialogBaseline<T>(open: boolean, entityId: string | undefined, snapshot: () => T): T | null {
  const baselineRef = useRef<T | null>(null);
  const lastEntityRef = useRef<string | undefined>(undefined);

  useEffect(() => {
    if (open && entityId !== undefined) {
      if (entityId !== lastEntityRef.current || baselineRef.current === null) {
        baselineRef.current = snapshot();
        lastEntityRef.current = entityId;
      }
    } else if (!open) {
      baselineRef.current = null;
      lastEntityRef.current = undefined;
    }
  }, [open, entityId, snapshot]);

  return open && entityId !== undefined ? baselineRef.current : null;
}
