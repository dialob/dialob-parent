import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export function useFillLocale() {
  const session = useFillSession();
  const [locale, setLocale] = useState(session.getLocale());

  useEffect(() => {
    const listener = () => {
      setLocale(session.getLocale());
    };
    session.on('update', listener);
    setLocale(session.getLocale());

    return () => {
      session.removeListener('update', listener);
    }
  }, [session]);

  return locale;
}


