import React from "react";
import { useTheme } from "@mui/material";
import { EditorError, ErrorSeverity } from "../editor";
import { DialobItem } from "../dialob";
import { Check, Info, Warning } from "@mui/icons-material";
import { useIntl } from "react-intl";
import { PreTextIcon } from "../views/tree/NavigationTreeItem";

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

const getDominantSeverity = (errors: EditorError[]): ErrorSeverity | undefined => {
  const hasFatal = errors.some(error => error.severity === 'FATAL');
  const hasError = errors.some(error => error.severity === 'ERROR');
  const hasWarning = errors.some(error => error.severity === 'WARNING');
  const hasInfo = errors.some(error => error.severity === 'INFO');
  if (hasFatal) {
    return 'FATAL';
  }
  if (hasError) {
    return 'ERROR';
  }
  if (hasWarning) {
    return 'WARNING';
  }
  if (hasInfo) {
    return 'INFO';
  }
  return undefined;
}

const getItemErrorSeverity = (errors: EditorError[], itemId: string): ErrorSeverity | undefined => {
  const itemErrors = errors.filter(error => error.itemId === itemId);
  return getDominantSeverity(itemErrors);
}

export const getErrorColor = (errors: EditorError[], itemId: string) => {
  const itemErrorSeverity = getItemErrorSeverity(errors, itemId);
  switch (itemErrorSeverity) {
    case 'FATAL':
      return 'error';
    case 'ERROR':
      return 'error';
    case 'WARNING':
      return 'warning';
    case 'INFO':
      return 'info';
    default:
      return 'primary';
  }
}

export const useErrorColorSx = (errors: EditorError[], itemId: string): string | undefined => {
  const theme = useTheme();
  const itemErrorSeverity = getItemErrorSeverity(errors, itemId);
  switch (itemErrorSeverity) {
    case 'FATAL':
      return theme.palette.error.main;
    case 'ERROR':
      return theme.palette.error.main;
    case 'WARNING':
      return theme.palette.warning.main;
    case 'INFO':
      return theme.palette.info.main;
    default:
      return undefined;
  }
}

export const getErrorIcon = (errors: EditorError[], itemId: string): React.ReactNode | undefined => {
  const itemErrorSeverity = getItemErrorSeverity(errors, itemId);
  switch (itemErrorSeverity) {
    case 'FATAL':
      return <PreTextIcon disableRipple><Warning color='error' fontSize='small' /></PreTextIcon>;
    case 'ERROR':
      return <PreTextIcon disableRipple><Warning color='error' fontSize='small' /></PreTextIcon>;
    case 'WARNING':
      return <PreTextIcon disableRipple><Warning color='warning' fontSize='small' /></PreTextIcon>;
    case 'INFO':
      return <PreTextIcon disableRipple><Info color='info' fontSize='small' /></PreTextIcon>;
    default:
      return undefined;
  }
}

export const getStatusIcon = (errors: EditorError[]): React.ReactElement => {
  const dominantSeverity = getDominantSeverity(errors);
  switch (dominantSeverity) {
    case 'FATAL':
      return <Warning color='error' fontSize='small' />;
    case 'ERROR':
      return <Warning color='error' fontSize='small' />;
    case 'WARNING':
      return <Warning color='warning' fontSize='small' />;
    case 'INFO':
      return <Info color='info' fontSize='small' />;
    default:
      return <Check color='success' fontSize='small' />;
  }
}
