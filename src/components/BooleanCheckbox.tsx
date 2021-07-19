import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { FormControlLabel, Checkbox, FormControl, Typography } from '@material-ui/core';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface BooleanCheckboxProps {
  boolean: ItemAction<'boolean'>['item'];
  errors: SessionError[];
};

export const BooleanCheckbox: React.FC<BooleanCheckboxProps> = ({ boolean, errors }) => {
  const {setAnswer} = useFillActions();
  return (
    <DescriptionWrapper text={boolean.description} title={boolean.label}>
      <FormControl fullWidth={true} required={boolean.required} error={errors.length > 0}>
        <FormControlLabel
          label={boolean.label ? boolean.label : ""}
          control={
            <Checkbox
              checked={boolean.value || false}
              onChange={e => setAnswer(boolean.id, e.target.checked)}
              />
          }
        />
        <ErrorHelperText errors={errors} />
      </FormControl>
    </DescriptionWrapper>
  );
};
