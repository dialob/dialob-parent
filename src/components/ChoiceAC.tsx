import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillActions, useFillValueSet } from '@resys/dialob-fill-react';
import React from 'react';
import { TextField } from '@material-ui/core';
import { Autocomplete } from '@material-ui/lab';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface ChoiceACProps {
  choice: ItemAction<'list'>['item'];
  errors: SessionError[];
};

interface ValueSetEntry {
  key: string;
  value: string;
};

export const ChoiceAC: React.FC<ChoiceACProps> = ({ choice, errors }) => {
  const {setAnswer} = useFillActions();
  const valueSet = useFillValueSet(choice.valueSetId);
  const entries: ValueSetEntry[] = valueSet?.entries ? valueSet.entries : [];

  return (
    <DescriptionWrapper text={choice.description} title={choice.label}>
      <Autocomplete
        options={entries}
        getOptionLabel={c => c?.value || ''}
        value={choice.value ? entries.find(e => e.key === choice.value) : {key:'',value:''} as ValueSetEntry}
        getOptionSelected={(option, value) => option?.key === value?.key}
        fullWidth
        autoComplete
        onChange = {(event: any, newValue?: ValueSetEntry | undefined | null) => {
          setAnswer(choice.id, newValue?.key);
        }}
        renderInput={params => <TextField {...params} inputProps={{...params.inputProps, autoComplete: 'new-password'}} label={choice.label} error={errors.length > 0} helperText={renderErrors(errors)}/>}
      />
    </DescriptionWrapper>
  );

};
