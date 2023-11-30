import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet, useFillSession } from '@dialob/fill-react';
import React, {useMemo} from 'react';
import { Select, MenuItem, InputLabel, FormControl } from '@mui/material';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { calculateMargin, getIndent } from '../util/helperFunctions';

export interface ChoiceProps {
  choice: ItemAction<'list'>['item'];
  errors: SessionError[];
};

export const Choice: React.FC<ChoiceProps> = ({ choice, errors }) => {
  const session = useFillSession();
  const {setAnswer} = useFillActions();
  const valueSet = useFillValueSet(choice.valueSetId);
  const itemId = `item_${session.id}_${choice.id}`;
  const indent = getIndent(parseInt(choice.props?.indent || 0));
  const spacesTop = parseInt(choice.props?.spacesTop || 0);
  const spacesBottom = parseInt(choice.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);
  
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
    <FormControl fullWidth={true} required={choice.required} error={errors.length > 0} sx={{minWidth: 120, pl: indent, mt: marginTop, mb: marginBottom}}>
      <InputLabel id={`${itemId}_label`} shrink>{choice.label}</InputLabel>
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
