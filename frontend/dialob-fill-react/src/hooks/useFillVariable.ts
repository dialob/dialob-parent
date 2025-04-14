import { useEffect, useState } from 'react';
import { useFillSession } from './useFillSession';

export function useFillVariable(id: string | undefined): any {
  const session = useFillSession();
  const initialValue = id ? session.getVariable(id) : undefined;
  const [variable, setVariable] = useState<any>(initialValue);

  useEffect(() => {
    if (!id) return;

    const listener = () => {
      const updatedVariable = session.getVariable(id);
      setVariable(updatedVariable);
    };
    session.on('update', listener);
    setVariable(session.getVariable(id));

    return () => {
      session.removeListener('update', listener);
    }
  }, [session, id]);

  return variable;
}

