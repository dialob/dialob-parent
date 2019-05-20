import { useContext } from 'react';
import { SessionContext } from '../context/sessionContext';

export function useFillSession() {
  return useContext(SessionContext);
}
