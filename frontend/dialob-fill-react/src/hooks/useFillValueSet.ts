import { SessionValueSet } from '@dialob/fill-api';
import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export function useFillValueSet(id: string | undefined): SessionValueSet | undefined {
  const session = useFillSession();
  const initialValue = id ? session.getValueSet(id) : undefined;
  const [valueSet, setValueSet] = useState<SessionValueSet | undefined>(initialValue);

  useEffect(() => {
    if(!id) return;

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

