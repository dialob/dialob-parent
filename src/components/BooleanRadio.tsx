import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillSession } from '@resys/dialob-fill-react';
import React from 'react';
import { RadioGroup, FormControlLabel, Radio, FormControl, FormLabel } from '@material-ui/core';
import { ErrorHelperText } from './helpers';
import { useIntl } from 'react-intl';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface BooleanRadioProps {
  boolean: ItemAction<'boolean'>['item'];
  errors: SessionError[];
};
export const BooleanRadio: React.FC<BooleanRadioProps> = ({ boolean, errors }) => {
  const session = useFillSession();
  const intl = useIntl();
  return (
    <DescriptionWrapper text={boolean.description} title={boolean.label}>
      <FormControl component='fieldset' required={boolean.required} fullWidth={true} error={errors.length > 0}>
        <FormLabel component="legend">{boolean.label}</FormLabel>
        <RadioGroup value={boolean.value || false} onChange={e => {session.setAnswer(boolean.id, e.target.value);}} row={true}>
          <FormControlLabel
            value='true'
            control={<Radio />}
            label={intl.formatMessage({id: 'yes'})}
            labelPlacement='end'
          />
          <FormControlLabel
            value='false'
            control={<Radio />}
            label={intl.formatMessage({id: 'no'})}
            labelPlacement='end'
          />
        </RadioGroup>
        <ErrorHelperText errors={errors} />
      </FormControl>
    </DescriptionWrapper>
  );
};
