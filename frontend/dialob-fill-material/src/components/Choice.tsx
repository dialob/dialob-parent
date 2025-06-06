import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet, useFillSession } from '@dialob/fill-react';
import React, { useMemo } from 'react';
import { Select, MenuItem, InputLabel, FormControl } from '@mui/material';
import { ErrorHelperText, getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface ChoiceProps {
  choice: ItemAction<'list'>['item'];
  errors: SessionError[];
};

export const Choice: React.FC<ChoiceProps> = ({ choice, errors }) => {
  const session = useFillSession();
  const { setAnswer } = useFillActions();
  const valueSet = useFillValueSet(choice.valueSetId);
  const itemId = `item_${session.id}_${choice.id}`;

  const options = useMemo(() => {
    const options: JSX.Element[] = [];
    if (!valueSet) {
      return options;
    }
    for (const entry of valueSet.entries) {
      options.push(<MenuItem key={entry.key} value={entry.key}>{entry.value}</MenuItem>)
    }
    return options;
  }, [valueSet]);

  return (
    <DescriptionWrapper text={choice.description} title={choice.label}>
      <FormControl
        fullWidth={true}
        required={choice.required}
        error={errors.length > 0}
        sx={{ minWidth: 120, ...getLayoutStyleFromProps(choice.props) }}
      >
        <InputLabel id={`${itemId}_label`}>{choice.label}</InputLabel>
        <Select labelId={`${itemId}_label`}
          label={choice.label}
          value={choice.value || ''}
          onChange={e => setAnswer(choice.id, e.target.value)}
        >
          {options}
        </Select>
        <ErrorHelperText errors={errors} />
      </FormControl>
    </DescriptionWrapper>
  );
};
