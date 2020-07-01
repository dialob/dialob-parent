import React from 'react';
import { FillError } from "@resys/dialob-fill-api";
import { FormHelperText } from '@material-ui/core';

export const renderErrors = (errors:FillError[]) => errors.length > 0 ?  <ul>{errors.map((e, i) => <li key={i}>{e.description}</li>)}</ul> : undefined;

interface ErrorHelperTextProps {
  errors: FillError[];
}

export const ErrorHelperText: React.FC<ErrorHelperTextProps> = ({errors}) => errors.length > 0 ? <FormHelperText>{renderErrors(errors)}</FormHelperText> : null;
