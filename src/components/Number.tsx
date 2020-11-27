import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { TextField } from '@material-ui/core';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface NumberProps {
  number: ItemAction<any, any, number>['item'];
  errors: SessionError[];
};
export const Number: React.FC<NumberProps> = ({ number, errors }) => {
  const {setAnswer} = useFillActions();

  return (
    <DescriptionWrapper text={number.description} title={number.label}>
      <TextField
        fullWidth
        label={number.label}
        required={number.required}
        error={errors.length > 0}
        value={number.value || ''}
        onChange={e => setAnswer(number.id, e.currentTarget.value)}
        helperText={renderErrors(errors)}
        type='number'
      />
    </DescriptionWrapper>
  );
};
