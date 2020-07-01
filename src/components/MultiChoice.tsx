import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillSession, useFillValueSet } from '@resys/dialob-fill-react';
import React from 'react';
import { FormControl, Checkbox,  FormLabel, FormGroup, FormControlLabel } from '@material-ui/core';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface MultiChoiceProps {
  multichoice: ItemAction<'multichoice'>['item'];
  errors: SessionError[];
};
export const MultiChoice: React.FC<MultiChoiceProps> = ({ multichoice, errors }) => {
  const session = useFillSession();
  const valueSet = useFillValueSet(multichoice.valueSetId);
  const currentValue: string[] = multichoice.value || [];

  const options: JSX.Element[] = [];
  if (valueSet) {
    for (const entry of valueSet.entries) {
      const isSelected = currentValue.includes(entry.key);
      options.push(
        <FormControlLabel key={entry.key}
          control={<Checkbox checked={isSelected} onChange={() => {
            if (isSelected) {
              session.setAnswer(multichoice.id, currentValue.filter(v => v !== entry.key));
            } else {
              session.setAnswer(multichoice.id, [...currentValue, entry.key]);
            }
          }}
            value={entry.key} />}
          label={entry.value} />
      );
    }
  }

  return (
    <DescriptionWrapper text={multichoice.description} title={multichoice.label}>
      <FormControl component='fieldset' fullWidth={true} required={multichoice.required} error={errors.length > 0}>
        <FormLabel component='legend'>{multichoice.label}</FormLabel>
        <FormGroup>
          {options}
        </FormGroup>
        <ErrorHelperText errors={errors} />
      </FormControl>
    </DescriptionWrapper>
  );

};
