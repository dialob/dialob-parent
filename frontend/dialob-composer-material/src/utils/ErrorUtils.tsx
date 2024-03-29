import React from "react";
import { useTheme } from "@mui/material";
import { EditorError, ErrorSeverity } from "../editor";
import { Check, Info, Warning } from "@mui/icons-material";
import { PreTextIcon } from "../components/tree/NavigationTreeItem";

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
