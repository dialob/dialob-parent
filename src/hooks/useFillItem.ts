import { SessionItem } from '@resys/dialob-fill-api';
import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export function useFillItem(id: string | undefined): SessionItem | undefined {
  const session = useFillSession();
  const initialValue = id ? session.getItem(id) : undefined;
  const [item, setItem] = useState<SessionItem | undefined>(initialValue);

  useEffect(() => {
    if(!id) return;

    const listener = () => {
      const updatedItem = session.getItem(id);
      setItem(updatedItem);
    };
    session.on('update', listener);
    setItem(session.getItem(id));

    return () => {
      session.removeListener('update', listener);
    }
  }, [session, id]);

  return item;
}
