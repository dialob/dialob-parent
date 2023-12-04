import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { RadioGroup, FormControlLabel, Radio, FormControl, FormLabel } from '@mui/material';
import { ErrorHelperText, buildSxFromProps } from './helpers';
import { useIntl } from 'react-intl';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface BooleanRadioProps {
  boolean: ItemAction<'boolean'>['item'];
  errors: SessionError[];
};
export const BooleanRadio: React.FC<BooleanRadioProps> = ({ boolean, errors }) => {
  const {setAnswer} = useFillActions();
  const intl = useIntl();

  const setValue = (value:string) => {
    // convert text from radio button value to boolean
    return value === 'true' ? true : (value === 'false' ? false : value);
  }
  const getValue = (value?:boolean) => {
    // convert booleans to radio button string values
    if (value === true) {
      return 'true';
    }
    else if (value === false) {
      return 'false';
    }
    // accept other values as they are (from previous version storing strings)
    return value;
  }
  return (
    <DescriptionWrapper text={boolean.description} title={boolean.label}>
      <FormControl 
        component='fieldset' 
        required={boolean.required} 
        fullWidth={true} 
        error={errors.length > 0} 
        sx={buildSxFromProps(boolean.props)}
      >
        <FormLabel component="legend">{boolean.label}</FormLabel>
        <RadioGroup value={getValue(boolean.value)} onChange={e => {setAnswer(boolean.id, setValue(e.target.value));}} row={true}>
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
