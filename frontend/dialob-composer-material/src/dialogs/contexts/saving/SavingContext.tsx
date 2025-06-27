import { createContext, Dispatch } from "react";
import { ComposerMetadata, DialobItem, ValueSet } from "../../../types";
import { SavingAction } from "./SavingAction";

export interface SavingState {
  item: DialobItem;
  valueSets?: ValueSet[];
  composerMetadata?: ComposerMetadata;
}

export const SavingContext = createContext<{ state: SavingState, dispatch: Dispatch<SavingAction> }>({
  state: { } as SavingState,
  dispatch: () => null
});
