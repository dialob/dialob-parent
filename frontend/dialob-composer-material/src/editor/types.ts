import { DialobItem } from "../dialob";

export type TextEditDialogType = 'label' | 'description';
export type RuleEditDialogType = 'requirement' | 'visibility';

export type EditorState = {
  activePage?: DialobItem;
  activeFormLanguage: string;
  textEditDialogType?: TextEditDialogType;
  ruleEditDialogType?: RuleEditDialogType;
  validationRuleEditDialogOpen?: boolean;
};
