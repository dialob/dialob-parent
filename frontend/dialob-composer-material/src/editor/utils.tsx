import React from "react";
import { useTheme } from "@mui/material";
import { EditorError, ErrorSeverity } from ".";
import { DialobItem } from "../dialob";
import { Check, Info, Warning } from "@mui/icons-material";

export const translateErrorType = (error: EditorError): string => {
  switch (error.type) {
    case 'VARIABLE':
      return 'Variable';
    case 'VISIBILITY':
      return 'Visibility';
    case 'GENERAL':
      return error.message === 'INVALID_DEFAULT_VALUE' ? 'Default' : '';
    case 'REQUIREMENT':
      return 'Requirement';
    case 'VALIDATION':
      return 'Validation';
    case 'VALUESET':
      return 'List';
    case 'VALUESET_ENTRY':
      return 'List entry';
    case 'CANADDROW':
      return 'Add row';
    case 'CANREMOVEROW':
      return 'Remove row';
    default:
      return error.type;
  };
}

export const translateErrorMessage = (error: EditorError): string => {
  switch (error.message) {
    case 'RB_VARIABLE_NEEDS_EXPRESSION':
      return 'Missing expression';
    case 'INVALID_DEFAULT_VALUE':
      return 'Invalid value';
    case 'UNKNOWN_VARIABLE':
      return 'Unknown variable';
    case 'SYNTAX_ERROR':
      return 'Syntax error';
    case 'COULD_NOT_DEDUCE_TYPE':
      return 'Can\'t deduce type ';
    case 'NO_ORDER_RELATION_BETWEEN_TYPES':
      return 'Can\'t compare these variables';
    case 'NO_EQUALITY_RELATION_BETWEEN_TYPES':
      return 'Can\'t compare these variables';
    case 'BOOLEAN_EXPRESSION_EXPECTED':
      return 'Boolean expression expected';
    case 'VALUESET_EMPTY':
      return 'Choice list is empty';
    case 'VALUESET_DUPLICATE_KEY':
      return `Choice list has duplicate key '${error.expression}'`;
    case 'VALUESET_EMPTY_KEY':
      return 'Choice list has empty key';
    case 'CONTEXT_VARIABLE_UNDEFINED_TYPE':
      return 'Context variable type not defined';
    case 'VALUE_TYPE_NOT_SET':
      return 'Value type not set';
    case 'TAG_EXISTS':
      return 'Tag already exists';
    case 'MATCHER_REGEX_SYNTAX_ERROR':
      return 'Invalid regular expression';
    case 'MATCHER_DYNAMIC_REGEX':
      return 'Dynamic regular expressions not supported';
    case 'REDUCER_TARGET_MUST_BE_REFERENCE':
      return 'Multirow aggregate function target must be directly multirow item';
    case 'CANNOT_USE_REDUCER_INSIDE_SCOPE':
      return 'Multirow aggregate function can\'t be used for non-multirow item';
    case 'UNKNOWN_REDUCER_OPERATOR':
      return 'Unknown multirow aggregate function';
    case 'OPERATOR_CANNOT_REDUCE_TYPE':
      return 'This aggregate function can\'t be used for this item type';
    case 'UNKNOWN_FUNCTION':
      return 'Undefined function';

    default:
      return error.message;
  };
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
