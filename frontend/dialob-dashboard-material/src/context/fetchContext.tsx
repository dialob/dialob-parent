/* eslint-disable react-refresh/only-export-components */
import React from 'react';

export const DialobDashboardFetchContext = React.createContext<{
  fetch: typeof window.fetch;
}>({
  fetch: window.fetch
});

export const DialobDashboardFetchProvider: React.FC<{
  children: React.ReactNode;
  fetch?: typeof window.fetch;
}> = ({ children, fetch }) => {
  return (
    <DialobDashboardFetchContext.Provider value={{ fetch: fetch || window.fetch }}>
      {children}
    </DialobDashboardFetchContext.Provider>
  );
};

export const useDialobDashboardFetch = (): typeof window.fetch => {
  return React.useContext(DialobDashboardFetchContext).fetch;
};