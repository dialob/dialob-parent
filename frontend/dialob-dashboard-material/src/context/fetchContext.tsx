/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useContext } from 'react';
import { DialobDashboardFetchProviderProps, FetchAuthFunction } from '../types';



const defaultFetchAuth: FetchAuthFunction = (input, init) => fetch(input, init);

export const DialobDashboardFetchContext = createContext<FetchAuthFunction>(defaultFetchAuth);

export const useDialobDashboardFetch = (): FetchAuthFunction => {
  return useContext(DialobDashboardFetchContext);
};


export const DialobDashboardFetchProvider: React.FC<DialobDashboardFetchProviderProps> = ({
  children,
  fetch,
}) => {
  return (
    <DialobDashboardFetchContext.Provider value={fetch || defaultFetchAuth}>
      {children}
    </DialobDashboardFetchContext.Provider>
  );
};