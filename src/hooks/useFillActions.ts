import { useContext } from 'react';
import { SessionContext } from '../context/sessionContext';

export function useFillActions() {
  const { actions } = useContext(SessionContext);
  return actions;
}

