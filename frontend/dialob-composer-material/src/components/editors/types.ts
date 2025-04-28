import { ValidationRule } from "../../types";

export type PropValue = string | string[] | boolean;

export interface ItemProp {
  key: string;
  value: PropValue;
}

export interface IndexedRule {
  index: number;
  validationRule: ValidationRule;
}