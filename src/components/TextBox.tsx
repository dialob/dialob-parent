import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillSession } from '@resys/dialob-fill-react';
import React from 'react';
import { TextField } from '@material-ui/core';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TextBoxProps {
  text: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};
export const TextBox: React.FC<TextBoxProps> = ({ text, errors }) => {
  const session = useFillSession();
  return (
    <DescriptionWrapper text={text.description} title={text.label}>
      <TextField
        fullWidth={true}
        label={text.label}
        required={text.required}
        error={errors.length > 0}
        value={text.value || ''}
        onChange={e => session.setAnswer(text.id, e.currentTarget.value)}
        multiline
        helperText={renderErrors(errors)}
      />
    </DescriptionWrapper>
  );
}
