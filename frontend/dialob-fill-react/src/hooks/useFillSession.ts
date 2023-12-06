import { useContext } from 'react';
import { SessionContext } from '../context/sessionContext';

/**
 * @deprecated `useFillActions()` is what you probably want instead
 */
export function useFillSession() {
  const { session } = useContext(SessionContext);
  return session;
}
