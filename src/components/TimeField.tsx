import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import {TimePicker} from '@material-ui/pickers';
import moment from 'moment';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TimeFieldProps {
  timefield: ItemAction<'time'>['item'];
  errors: SessionError[];
};
export const TimeField: React.FC<TimeFieldProps> = ({ timefield, errors }) => {
  const {setAnswer} = useFillActions();
  const handleChange = (value: any) => {
    setAnswer(timefield.id, (value as moment.Moment).format('HH:mm'));
  }
  const value = timefield.value ? timefield.value as string : null;
  return (
    <DescriptionWrapper text={timefield.description} title={timefield.label}>
      <TimePicker
        fullWidth={true}
        disableToolbar
        ampm={false}
        variant='inline'
        margin='normal'
        label={timefield.label}
        value={value}
        onChange={handleChange}
        autoOk
        required={timefield.required}
        error={errors.length > 0}
        helperText={renderErrors(errors)}
      />
    </DescriptionWrapper>
  );
}
