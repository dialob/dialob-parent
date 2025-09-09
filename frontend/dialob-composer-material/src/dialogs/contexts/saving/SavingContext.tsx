import { createContext, Dispatch } from "react";
import { ComposerMetadata, ContextVariable, DialobItem, FormMetadata, ValueSet, Variable } from "../../../types";
import { SavingAction } from "./SavingAction";

export interface SavingState {
  item?: DialobItem;
  valueSets?: ValueSet[];
  composerMetadata?: ComposerMetadata;
  variables?: (ContextVariable | Variable)[];
  formMetadata?: FormMetadata;
}

export const SavingContext = createContext<{ state: SavingState, dispatch: Dispatch<SavingAction> }>({
  state: { } as SavingState,
  dispatch: () => null
});
