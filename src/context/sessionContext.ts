import React from 'react';
import { Session } from '@resys/dialob-fill-api';

// This is a bit of a hack to keep better typings. In reality, session will never be undefined
// in our app code.
const defaultSession: Session = (undefined as unknown) as Session;
export const SessionContext = React.createContext<Session>(defaultSession);
