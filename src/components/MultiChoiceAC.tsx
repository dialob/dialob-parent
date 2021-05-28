import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet } from '@dialob/fill-react';
import React from 'react';
import { TextField } from '@material-ui/core';
import { Autocomplete } from '@material-ui/lab';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface MultiChoiceACProps {
  multichoice: ItemAction<'multichoice'>['item'];
  errors: SessionError[];
};

interface ValueSetEntry {
  key: string;
  value: string;
};

export const MultiChoiceAC: React.FC<MultiChoiceACProps> = ({ multichoice, errors }) => {
  const {setAnswer} = useFillActions();
  const valueSet = useFillValueSet(multichoice.valueSetId);
  const entries: ValueSetEntry[] = valueSet?.entries ? valueSet.entries : [];

  return (
    <DescriptionWrapper text={multichoice.description} title={multichoice.label}>
      <Autocomplete
        multiple
        options={entries}
        getOptionLabel={c => c?.value || ''}
        value={multichoice.value ? multichoice.value.map(v => entries.find(e => e.key === v)) : []}
        getOptionSelected={(option, value) => option?.key === value?.key}
        fullWidth
        autoComplete
        onChange = {(event: any, newValue: (ValueSetEntry | undefined)[]) => {
          setAnswer(multichoice.id, newValue?.map(c => c?.key));
        }}
        renderInput={params => <TextField {...params} inputProps={{...params.inputProps, autoComplete: 'new-password'}} label={multichoice.label} error={errors.length > 0} 
        helperText={<RenderErrors errors={errors} />}
        />}
      />
    </DescriptionWrapper>
  );

};
