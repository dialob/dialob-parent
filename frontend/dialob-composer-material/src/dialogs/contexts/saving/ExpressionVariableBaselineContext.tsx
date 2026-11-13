import React from 'react';
import { ContextVariable, DialobItems, Variable } from '../../../types';

export type ExpressionVariableBaseline = {
  variables: (Variable | ContextVariable)[];
  items: DialobItems;
};

export const ExpressionVariableBaselineContext = React.createContext<ExpressionVariableBaseline | null>(null);

export const useExpressionVariableBaseline = (): ExpressionVariableBaseline | null =>
  React.useContext(ExpressionVariableBaselineContext);
