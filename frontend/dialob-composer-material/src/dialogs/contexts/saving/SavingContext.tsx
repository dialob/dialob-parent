import { createContext, Dispatch } from "react";
import { ComposerMetadata, ContextVariable, DialobItem, ValueSet, Variable } from "../../../types";
import { SavingAction } from "./SavingAction";

export interface SavingState {
  item?: DialobItem;
  valueSets?: ValueSet[];
  composerMetadata?: ComposerMetadata;
  variables?: (ContextVariable | Variable)[];
}

export const SavingContext = createContext<{ state: SavingState, dispatch: Dispatch<SavingAction> }>({
  state: { } as SavingState,
  dispatch: () => null
});
