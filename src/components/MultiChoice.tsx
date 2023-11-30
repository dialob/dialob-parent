import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet } from '@dialob/fill-react';
import React from 'react';
import { FormControl, Checkbox, FormLabel, FormGroup, FormControlLabel, Paper } from '@mui/material';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { calculateMargin, getIndent } from '../util/helperFunctions';

export interface MultiChoiceProps {
  multichoice: ItemAction<'multichoice'>['item'];
  errors: SessionError[];
};
export const MultiChoice: React.FC<MultiChoiceProps> = ({ multichoice, errors }) => {
  const { setAnswer } = useFillActions();
  const valueSet = useFillValueSet(multichoice.valueSetId);
  const currentValue: string[] = multichoice.value || [];
  const indent = getIndent(parseInt(multichoice.props?.indent || 0));
  const spacesTop = parseInt(multichoice.props?.spacesTop || 0);
  const spacesBottom = parseInt(multichoice.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);
  const border = multichoice.props?.border;

  const options: JSX.Element[] = [];
  if (valueSet) {
    for (const entry of valueSet.entries) {
      const isSelected = currentValue.includes(entry.key);
      options.push(
        <FormControlLabel key={entry.key} label={entry.value ? entry.value : ""}
          control={<Checkbox checked={isSelected} value={entry.key}
            onChange={() => {
              if (isSelected) {
                setAnswer(multichoice.id, currentValue.filter(v => v !== entry.key));
              } else {
                setAnswer(multichoice.id, [...currentValue, entry.key]);
              }
            }}
          />}
        />
      );
    }
  }

  const multiChoiceContent = (<FormControl component='fieldset' fullWidth={true} required={multichoice.required} error={errors.length > 0} sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
      <FormLabel component='legend'>{multichoice.label}</FormLabel>
      <FormGroup>
        {options}
      </FormGroup>
      <ErrorHelperText errors={errors} />
    </FormControl>);

  return (
    <DescriptionWrapper text={multichoice.description} title={multichoice.label}>
      { border ? (<Paper elevation={3} sx={{p: 2}}> { multiChoiceContent } </Paper>) : multiChoiceContent }
    </DescriptionWrapper>
  );

};
