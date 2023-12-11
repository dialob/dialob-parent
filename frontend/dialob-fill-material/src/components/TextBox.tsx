import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { TextField, Box } from '@mui/material';
import { RenderErrors, getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TextBoxProps {
  text: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};
export const TextBox: React.FC<TextBoxProps> = ({ text, errors }) => {
  const {setAnswer} = useFillActions();

  return (
    <DescriptionWrapper text={text.description} title={text.label}>
      <Box sx={getLayoutStyleFromProps(text.props)}>
        <TextField
          fullWidth={true}
          label={text.label}
          required={text.required}
          error={errors.length > 0}
          value={text.value || ''}
          onChange={e => setAnswer(text.id, e.currentTarget.value)}
          multiline
          helperText={<RenderErrors errors={errors} />}
        />
      </Box>
    </DescriptionWrapper>
  );
}
