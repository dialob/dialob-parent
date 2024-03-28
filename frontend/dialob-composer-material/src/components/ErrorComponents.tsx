import { useIntl } from "react-intl";
import { EditorError } from "../editor";

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

export const ErrorMessage: React.FC<{ error: EditorError }> = ({ error }) => {
  const intl = useIntl();
  if (error.message === 'VALUESET_DUPLICATE_KEY') {
    return intl.formatMessage({ id: 'errors.message.VALUESET_DUPLICATE_KEY' }, { expression: error.expression });
  }
  const label = `errors.message.${error.message}`;
  const stringExists = !!intl.messages[label];
  if (stringExists) {
    return intl.formatMessage({ id: label });
  }
  return error.message;
}