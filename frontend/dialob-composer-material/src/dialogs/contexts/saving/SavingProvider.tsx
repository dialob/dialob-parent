import React, { PropsWithChildren, useReducer } from "react";
import { SavingContext, SavingState } from "./SavingContext";
import { itemReducer } from "./reducer";

export const SavingProvider: React.FC<PropsWithChildren<{ savingState: SavingState }>> = ({ children, savingState }) => {
  const [state, dispatch] = useReducer(itemReducer, savingState);

  return (
    <SavingContext.Provider value={{ state, dispatch }}>
      {children}
    </SavingContext.Provider>
  );
}
