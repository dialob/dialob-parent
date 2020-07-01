import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillSession } from '@resys/dialob-fill-react';
import React from 'react';
import { TextField } from '@material-ui/core';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface NumberProps {
  number: ItemAction<any, any, number>['item'];
  errors: SessionError[];
};
export const Number: React.FC<NumberProps> = ({ number, errors }) => {
  const session = useFillSession();

  return (
    <DescriptionWrapper text={number.description} title={number.label}>
      <TextField
        fullWidth
        label={number.label}
        required={number.required}
        error={errors.length > 0}
        value={number.value || ''}
        onChange={e => session.setAnswer(number.id, e.currentTarget.value)}
        helperText={renderErrors(errors)}
        type='number'
      />
    </DescriptionWrapper>
  );
};
