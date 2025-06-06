import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { TextField, Box } from '@mui/material';
import { RenderErrors, getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TextProps {
  text: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};
export const Text: React.FC<TextProps> = ({ text, errors }) => {
  const { setAnswer } = useFillActions();

  return (
    <DescriptionWrapper text={text.description} title={text.label}>
      <Box sx={getLayoutStyleFromProps(text.props)}>
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
