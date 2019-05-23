import { SessionItem, SessionError } from '@resys/dialob-fill-api';
import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export interface FillItem {
  item?: SessionItem;
  errors: SessionError[];
}
export function useFillItem(id: string | undefined): FillItem {
  const session = useFillSession();
  const initialValue = id ? session.getItem(id) : undefined;
  const [item, setItem] = useState<SessionItem | undefined>(initialValue);
  const initialErrors = id ? session.getItemErrors(id) || [] : [];
  const [errors, setErrors] = useState<SessionError[]>(initialErrors);

  useEffect(() => {
    if(!id) return;

    const updateItem = () => {
      setItem(session.getItem(id));
    }

    const updateErrors = () => {
      const newErrors = session.getItemErrors(id);
      if(newErrors) {
        setErrors(newErrors);
      } else if(errors.length > 0) {
        setErrors([]);
      }
    }

    const listener = () => {
      updateItem();
      updateErrors();
    };
    session.on('update', listener);
    listener();

    return () => {
      session.removeListener('update', listener);
    }
  }, [session, id, setItem, setErrors]);

  return { item, errors };
}
