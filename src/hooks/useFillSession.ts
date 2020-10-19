import { useContext } from 'react';
import { SessionContext } from '../context/sessionContext';

/**
 * @deprecated `useFillActions()` is what you probably want instead
 */
export function useFillSession() {
  return useContext(SessionContext);
}
