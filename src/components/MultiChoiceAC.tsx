import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet } from '@dialob/fill-react';
import React from 'react';
import { TextField, Autocomplete, Box } from '@mui/material';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { useIntl } from 'react-intl';
import { calculateMargin, getIndent } from '../util/helperFunctions';

export interface MultiChoiceACProps {
  multichoice: ItemAction<'multichoice'>['item'];
  errors: SessionError[];
};

interface ValueSetEntry {
  key: string;
  value: string;
};

export const MultiChoiceAC: React.FC<MultiChoiceACProps> = ({ multichoice, errors }) => {
  const intl = useIntl();
  const { setAnswer } = useFillActions();
  const valueSet = useFillValueSet(multichoice.valueSetId);
  const entries: ValueSetEntry[] = valueSet?.entries ? valueSet.entries : [];
  const indent = getIndent(parseInt(multichoice.props?.indent || 0));
  const spacesTop = parseInt(multichoice.props?.spacesTop || 0);
  const spacesBottom = parseInt(multichoice.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);

  return (
    <DescriptionWrapper text={multichoice.description} title={multichoice.label}>
      <Box sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
        <Autocomplete
          multiple
          noOptionsText={intl.formatMessage({id: 'autocomplete.nooptions'})}
          options={entries}
          getOptionLabel={c => c?.value || ''}
          // @ts-ignore
          value={multichoice.value ? multichoice.value.map(v => entries.find(e => e.key === v)) : []}
          isOptionEqualToValue={(option, value) => option?.key === value?.key}
          fullWidth
          autoComplete
          onChange={(event: any, newValue: (ValueSetEntry | undefined)[]) => {
            // @ts-ignore
            setAnswer(multichoice.id, newValue?.map(c => c?.key));
          }}
          renderInput={params => <TextField {...params} inputProps={{ ...params.inputProps, autoComplete: 'new-password' }} label={multichoice.label}
            // @ts-ignore 
            error={errors.length > 0}
            helperText={<RenderErrors errors={errors} />}
          />}
        />
      </Box>
    </DescriptionWrapper>
  );

};
