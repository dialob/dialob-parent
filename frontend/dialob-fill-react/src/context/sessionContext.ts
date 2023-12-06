import React from 'react';
import { Session } from '@dialob/fill-api';

export interface Actions {
  setAnswer: Session['setAnswer'];
  addRowToGroup: Session['addRowToGroup'];
  deleteRow: Session['deleteRow'];
  complete: Session['complete'];
  next: Session['next'];
  previous: Session['previous'];
  goToPage: Session['goToPage'];
  on: Session['on'];
  removeListener: Session['removeListener'];
};

// This is a bit of a hack to keep better typings. In reality, session will never be undefined
// in our app code.
const defaultSession: Session = (undefined as unknown) as Session;
const defaultActions: Actions = (undefined as unknown) as Actions;
export const SessionContext = React.createContext<{
  session: Session,
  actions: Actions,
}>({ session: defaultSession, actions: defaultActions });
