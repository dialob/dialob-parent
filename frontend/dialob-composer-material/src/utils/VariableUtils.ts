import { ContextVariable, Variable } from '../types';
import { isContextVariable } from './ItemUtils';

const arrayMove = <T>(array: T[], from: number, to: number): T[] => {
  const result = [...array];
  const [item] = result.splice(from, 1);
  result.splice(to, 0, item);
  return result;
};

export const reorderVariableSubset = (
  variables: (ContextVariable | Variable)[],
  name: string,
  toFilteredIndex: number,
  context: boolean
): void => {
  const isTargetType = (variable: ContextVariable | Variable) =>
    context ? isContextVariable(variable) : !isContextVariable(variable);

  const subset = variables.filter(isTargetType);
  const fromFilteredIndex = subset.findIndex(v => v.name === name);
  if (fromFilteredIndex === -1 || fromFilteredIndex === toFilteredIndex) {
    return;
  }

  const slots = variables
    .map((variable, index) => isTargetType(variable) ? index : -1)
    .filter(index => index >= 0);
  const reordered = arrayMove(subset, fromFilteredIndex, toFilteredIndex);
  slots.forEach((absIdx, i) => {
    variables[absIdx] = reordered[i];
  });
};
