import { useIntl } from "react-intl";
import { EditorError } from "../editor";
import { DialobItem, Variable, useComposer } from "../dialob";

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
      return item.activeWhen!.substring(start, end + 1);
    case 'REQUIREMENT':
      return item.required!.substring(start, end + 1);
    default:
      return '';
  }
}

const extractCodeFromVariable = (variable: Variable, start: number, end: number) => {
  return variable.expression.substring(start, end + 1);
}

export const ErrorMessage: React.FC<{ error: EditorError }> = ({ error }) => {
  const intl = useIntl();
  const { form } = useComposer();
  const { message, expression, type, startIndex, endIndex, itemId } = error;
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
      code = extractCodeFromItem(form.data[itemId], startIndex, endIndex, type);
    }
  }
  return <>{msg} <i>{code}</i></>;
}