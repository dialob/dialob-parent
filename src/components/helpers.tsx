import React from 'react';
import { FillError } from "@dialob/fill-api";
import { FormHelperText } from '@material-ui/core';
import { ConfigContext } from '../';

interface ErrorHelperTextProps {
  errors: FillError[];
}


export const DefaultRenderErrors: React.FC<ErrorHelperTextProps> = ({ errors }) => {
  return <ul>{errors.map((e, i) => <li key={i}>{e.description}</li>)}</ul>;
};

export const RenderErrors: React.FC<ErrorHelperTextProps> = ({ errors }) => {  
  const config = React.useContext(ConfigContext);
  if (errors.length === 0) {
    return null;
  }
  return config.errors(errors);
};



export const ErrorHelperText: React.FC<ErrorHelperTextProps> = ({ errors }) => errors.length > 0 ? <FormHelperText><RenderErrors errors={errors} /></FormHelperText> : null;
