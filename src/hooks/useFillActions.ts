import { useContext } from 'react';
import { SessionContext } from '../context/sessionContext';

export function useFillActions() {
  const context = useContext(SessionContext);
  return {
    setAnswer: context.setAnswer,
    addRowToGroup: context.addRowToGroup,
    deleteRow: context.deleteRow,
    complete: context.complete,
    next: context.next,
    previous: context.previous,
    goToPage: context.goToPage,
    on: context.on,
    removeListener: context.removeListener,
  };
}

