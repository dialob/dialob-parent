import { SessionValueSet } from '@resys/dialob-fill-api';
import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export function useFillValueSet(id: string): SessionValueSet | undefined {
  const session = useFillSession();
  const [valueSet, setValueSet] = useState<SessionValueSet | undefined>(session.getValueSet(id));

  useEffect(() => {
    const listener = () => {
      const updatedValueSet = session.getValueSet(id);
      setValueSet(updatedValueSet);
    };
    session.on('update', listener);
    setValueSet(session.getValueSet(id));

    return () => {
      session.removeListener('update', listener);
    }
  }, [session, id]);

  return valueSet;
}

