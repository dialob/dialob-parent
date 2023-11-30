import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { FormControlLabel, Checkbox, FormControl } from '@mui/material';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { calculateMargin, getIndent } from '../util/helperFunctions';

export interface BooleanCheckboxProps {
  boolean: ItemAction<'boolean'>['item'];
  errors: SessionError[];
};

export const BooleanCheckbox: React.FC<BooleanCheckboxProps> = ({ boolean, errors }) => {
  const {setAnswer} = useFillActions();
  const indent = getIndent(parseInt(boolean.props?.indent || 0));
  const spacesTop = parseInt(boolean.props?.spacesTop || 0);
  const spacesBottom = parseInt(boolean.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);
  
  return (
    <DescriptionWrapper text={boolean.description} title={boolean.label}>
      <FormControl fullWidth={true} required={boolean.required} error={errors.length > 0} sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
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
