import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { TextField, Box } from '@mui/material';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TextProps {
  text: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};
export const Text: React.FC<TextProps> = ({ text, errors }) => {
  const {setAnswer} = useFillActions();
  const indent = parseInt(text.props?.indent || 0);
  const spacesTop = parseInt(text.props?.spacesTop || 0);
  const spacesBottom = parseInt(text.props?.spacesBottom || 0);

  return (
    <DescriptionWrapper text={text.description} title={text.label}>
      <Box sx={{pl: (theme) => theme.spacing(indent), marginTop: (theme) => theme.spacing(spacesTop), marginBottom: (theme) => theme.spacing(spacesBottom)}}>
        <TextField
          fullWidth
          label={text.label}
          required={text.required}
          error={errors.length > 0}
          value={text.value || ''}
          onChange={e => setAnswer(text.id, e.currentTarget.value)}
          helperText={<RenderErrors errors={errors} />}
        />
      </Box>
    </DescriptionWrapper>
  );
};
