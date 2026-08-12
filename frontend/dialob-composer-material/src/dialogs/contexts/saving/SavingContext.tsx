import { createContext, Dispatch } from "react";
import { ComposerMetadata, ContextVariable, DialobItem, DialobItems, FormMetadata, ValueSet, Variable } from "../../../types";
import { SavingAction } from "./SavingAction";
import { ValueSetEntryRename } from "../../../utils/ValueSetEntryRenameUtils";

export interface VariableRename {
  from: string;
  to: string;
}

export type { ValueSetEntryRename };

export interface SavingState {
  item?: DialobItem;
  valueSets?: ValueSet[];
  composerMetadata?: ComposerMetadata;
  variables?: (ContextVariable | Variable)[];
  formMetadata?: FormMetadata;
  items?: DialobItems;
  pendingVariableRenames?: VariableRename[];
  pendingEntryRenames?: ValueSetEntryRename[];
}

export const SavingContext = createContext<{ state: SavingState, dispatch: Dispatch<SavingAction> }>({
  state: { } as SavingState,
  dispatch: () => null
});
