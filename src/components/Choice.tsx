import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillValueSet, useFillSession } from '@dialob/fill-react';
import React, {useMemo} from 'react';
import { Select, MenuItem, InputLabel, FormControl } from '@material-ui/core';
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles';
import { ErrorHelperText } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

const useStyles = makeStyles((theme: Theme) => createStyles({
  formControl: {
    minWidth: 120,
  },
})
);

export interface ChoiceProps {
  choice: ItemAction<'list'>['item'];
  errors: SessionError[];
};

export const Choice: React.FC<ChoiceProps> = ({ choice, errors }) => {
  const classes = useStyles();
  const session = useFillSession();
  const {setAnswer} = useFillActions();
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
    <FormControl fullWidth={true} required={choice.required} error={errors.length > 0} className={classes.formControl}>
      <InputLabel id={`${itemId}_label`}>{choice.label}</InputLabel>
      <Select labelId={`${itemId}_label`}
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
