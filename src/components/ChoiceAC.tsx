import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet } from '@dialob/fill-react';
import React from 'react';
import { TextField, Autocomplete, Box } from '@mui/material';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { useIntl } from 'react-intl';

export interface ChoiceACProps {
  choice: ItemAction<'list'>['item'];
  errors: SessionError[];
};

interface ValueSetEntry {
  key: string;
  value: string;
};

export const ChoiceAC: React.FC<ChoiceACProps> = ({ choice, errors }) => {
  const intl = useIntl();
  const { setAnswer } = useFillActions();
  const valueSet = useFillValueSet(choice.valueSetId);
  const entries: ValueSetEntry[] = valueSet?.entries ? valueSet.entries : [];
  const indent = parseInt(choice.props?.indent || 0);
  const spacesTop = parseInt(choice.props?.spacesTop || 0);
  const spacesBottom = parseInt(choice.props?.spacesBottom || 0);

  return (
    <DescriptionWrapper text={choice.description} title={choice.label}>
      <Box sx={{paddingLeft: (theme) => theme.spacing(indent), marginTop: (theme) => theme.spacing(spacesTop), marginBottom: (theme) => theme.spacing(spacesBottom)}}>
        <Autocomplete
          options={entries}
          noOptionsText={intl.formatMessage({id: 'autocomplete.nooptions'})}
          getOptionLabel={c => c?.value || ''}
          value={choice.value ? entries.find(e => e.key === choice.value) : { key: '', value: '' } as ValueSetEntry}
          isOptionEqualToValue={(option, value) => option?.key === value?.key}
          fullWidth
          autoComplete
          onChange={(event: any, newValue?: ValueSetEntry | undefined | null) => {
            setAnswer(choice.id, newValue?.key);
          }}
          renderInput={params => <TextField {...params} inputProps={{ ...params.inputProps, autoComplete: 'new-password' }} label={choice.label} error={errors.length > 0}
            helperText={<RenderErrors errors={errors} />}
          />}
        />
      </Box>
    </DescriptionWrapper>
  );

};
