import React from 'react';
import { FillError } from "@dialob/fill-api";
import { FormHelperText } from '@mui/material';
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

export const ErrorHelperText: React.FC<ErrorHelperTextProps> = ({ errors }) => errors.length > 0 ? <FormHelperText component='div'><RenderErrors errors={errors} /></FormHelperText> : null;

export const getLayoutStyleFromProps = (props: { [name: string]: any } | undefined) => {
  const indent = parseInt(props?.indent ?? undefined);
  const spacesTop = parseInt(props?.spacesTop ?? undefined);
  const spacesBottom = parseInt(props?.spacesBottom ?? undefined);

  const sx: { [key: string]: any } = {
    ...(indent && { paddingLeft: indent }),
    ...(spacesTop && { marginTop: spacesTop }),
    ...(spacesBottom && { marginBottom: spacesBottom })
  };

  return sx;
};