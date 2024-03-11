import ChoiceEditor from "./ChoiceEditor";
import LabelEditor from "./LabelEditor";
import DescriptionEditor from "./DescriptionEditor";
import RulesEditor from "./RulesEditor";
import PropertiesEditor from "./PropertiesEditor";
import DefaultValueEditor from "./DefaultValueEditor";
import ValidationRuleEditor from "./ValidationRuleEditor";

// eslint-disable-next-line @typescript-eslint/no-namespace
namespace Editors {
  export const Label = LabelEditor;
  export const Description = DescriptionEditor;
  export const Choice = ChoiceEditor;
  export const Rules = RulesEditor;
  export const Properties = PropertiesEditor;
  export const DefaultValue = DefaultValueEditor;
  export const Validations = ValidationRuleEditor;
}

export default Editors;