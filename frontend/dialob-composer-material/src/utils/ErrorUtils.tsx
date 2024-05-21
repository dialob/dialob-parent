import React from "react";
import { AlertColor, useTheme } from "@mui/material";
import { EditorError, ErrorSeverity } from "../editor";
import { Check, Info, Warning } from "@mui/icons-material";
import { PreTextIcon } from "../components/tree/NavigationTreeItem";

const getDominantSeverity = (errors: EditorError[] | undefined): ErrorSeverity | undefined => {
  if (!errors || errors.length === 0) {
    return undefined;
  }
  const hasFatal = errors.some(error => error.level === 'FATAL');
  const hasError = errors.some(error => error.level === 'ERROR');
  const hasWarning = errors.some(error => error.level === 'WARNING');
  const hasInfo = errors.some(error => error.level === 'INFO');
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

const getItemErrorSeverity = (errors: EditorError[] | undefined, itemId: string): ErrorSeverity | undefined => {
  const itemErrors = errors?.filter(error => error.itemId === itemId);
  return getDominantSeverity(itemErrors);
}

export const getErrorSeverity = (error: EditorError): AlertColor => {
  switch (error.level) {
    case 'FATAL':
      return 'error';
    case 'ERROR':
      return 'error';
    case 'WARNING':
      return 'warning';
    case 'INFO':
      return 'info';
    default:
      return 'info';
  }
}

export const getItemErrorColor = (errors: EditorError[] | undefined, itemId: string) => {
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

export const useErrorColorSx = (errors: EditorError[] | undefined, itemId: string): string | undefined => {
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

export const useErrorColor = (error: EditorError | undefined): string | undefined => {
  const theme = useTheme();
  if (error === undefined) {
    return undefined;
  }
  switch (error.level) {
    case 'FATAL':
      return theme.palette.error.main;
    case 'ERROR':
      return theme.palette.error.main;
    case 'WARNING':
      return theme.palette.warning.main;
    case 'INFO':
      return theme.palette.info.main;
  }
}

export const getErrorIcon = (errors: EditorError[] | undefined, itemId: string): React.ReactNode | undefined => {
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

export const getStatusIcon = (errors: EditorError[] | undefined): React.ReactElement => {
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

export const getStatus = (errors: EditorError[] | undefined): string => {
  const dominantSeverity = getDominantSeverity(errors);
  switch (dominantSeverity) {
    case 'FATAL':
      return 'fatal';
    case 'ERROR':
      return 'error';
    case 'WARNING':
      return 'warning';
    case 'INFO':
      return 'info';
    default:
      return 'ok';
  }
}
