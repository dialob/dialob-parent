import React from "react";
import { useTheme } from "@mui/material";
import { EditorError, ErrorSeverity } from ".";
import { DialobItem } from "../dialob";
import { Check, Info, Warning } from "@mui/icons-material";
import { useIntl } from "react-intl";

export const translateErrorType = (error: EditorError): string => {
  const intl = useIntl();
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

export const translateErrorMessage = (error: EditorError): string => {
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

const getItemErrorSeverity = (errors: EditorError[], item: DialobItem): ErrorSeverity | undefined => {
  const itemErrors = errors.filter(error => error.itemId === item.id);
  return getDominantSeverity(itemErrors);
}

export const getErrorColor = (errors: EditorError[], item: DialobItem): string | undefined => {
  const theme = useTheme();
  const itemErrorSeverity = getItemErrorSeverity(errors, item);
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

export const getErrorIcon = (errors: EditorError[], item: DialobItem): React.ReactNode | undefined => {
  const itemErrorSeverity = getItemErrorSeverity(errors, item);
  switch (itemErrorSeverity) {
    case 'FATAL':
      return <Warning color='error' fontSize='small' />;
    case 'ERROR':
      return <Warning color='error' fontSize='small' />;
    case 'WARNING':
      return <Warning color='warning' fontSize='small' />;
    case 'INFO':
      return <Info color='info' fontSize='small' />;
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
