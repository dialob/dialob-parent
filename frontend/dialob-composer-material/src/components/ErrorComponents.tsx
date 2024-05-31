import { useIntl } from "react-intl";
import { EditorError } from "../editor";
import { DialobItem, ValueSet, Variable, useComposer } from "../dialob";

export const ErrorType: React.FC<{ error: EditorError }> = ({ error }) => {
  const intl = useIntl();
  const resolveType = () => {
    if (error.type === 'GENERAL') {
      if (error.message === 'INVALID_DEFAULT_VALUE') {
        return intl.formatMessage({ id: 'errors.type.DEFAULT_VALUE' });
      }
      return '';
    }
    const label = `errors.type.${error.type}`;
    const stringExists = !!intl.messages[label];
    if (stringExists) {
      return intl.formatMessage({ id: label });
    }
    return error.type;
  }
  return <>{resolveType() + ' ' + intl.formatMessage({ id: 'errors.title' })}</>;
}

const extractCodeFromItem = (item: DialobItem, start: number, end: number, type: string) => {
  switch (type) {
    case 'VISIBILITY':
      return item.activeWhen ? item.activeWhen.substring(start, end + 1) : '';
    case 'REQUIREMENT':
      return item.required ? item.required.substring(start, end + 1) : '';
    case 'VALIDATION':
      return (item.validations && item.validations[0] && item.validations[0].rule) ? item.validations[0].rule.substring(start, end + 1) : '';
    default:
      return '';
  }
}

const extractCodeFromVariable = (variable: Variable, start: number, end: number) => {
  return variable.expression.substring(start, end + 1);
}

const extractListError = (valueSet: ValueSet, index: number) => {
  return valueSet.entries[index].id;
}

export const ErrorMessage: React.FC<{ error: EditorError }> = ({ error }) => {
  const intl = useIntl();
  const { form } = useComposer();
  const { message, expression, type, startIndex, endIndex, index, itemId } = error;
  let msg = message;
  let code = '';
  if (message === 'VALUESET_DUPLICATE_KEY') {
    msg = intl.formatMessage({ id: 'errors.message.VALUESET_DUPLICATE_KEY' }, { expression: expression });
  }
  const label = `errors.message.${message}`;
  const stringExists = !!intl.messages[label];
  if (stringExists) {
    msg = intl.formatMessage({ id: label });
  }
  if (itemId !== undefined && startIndex !== undefined && endIndex !== undefined && type !== undefined) {
    if (type === 'VARIABLE') {
      const variable = form.variables?.find(v => v.name === itemId);
      if (variable) {
        code = extractCodeFromVariable(variable as Variable, startIndex, endIndex);
      }
    } else {
      const item = form.data[itemId];
      if (item) {
        code = extractCodeFromItem(form.data[itemId], startIndex, endIndex, type);
      }
    }
  }
  if (type === 'VALUESET' && index !== undefined) {
    const valueSet = form.valueSets?.find(v => v.id === itemId);
    if (valueSet) {
      code = extractListError(valueSet, index);
    }
  }
  return <>{msg} <i>{code}</i></>;
}