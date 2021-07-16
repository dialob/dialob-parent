import React from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import TimePicker from '@material-ui/lab/TimePicker';
import TextField from '@material-ui/core/TextField';
import moment from 'moment';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface TimeFieldProps {
  timefield: ItemAction<'time'>['item'];
  errors: SessionError[];
};
const format = 'HH:mm';
export const TimeField: React.FC<TimeFieldProps> = ({ timefield, errors }) => {
  const { setAnswer } = useFillActions();
  const handleChange = (value: any) => {
    setAnswer(timefield.id, moment(value).format(format));
  }
  const value = timefield.value ? timefield.value as string : null;
  return (
    <DescriptionWrapper text={timefield.description} title={timefield.label}>
      <TimePicker
        ampm={false}
        label={timefield.label}
        value={value ? moment(value, format).toDate() : null}
        onChange={handleChange}
        renderInput={(props) => <TextField {...props}
          fullWidth={true}
          margin='normal'
          required={timefield.required}
          error={errors.length > 0}
          helperText={<RenderErrors errors={errors} />}
        />}
      />
    </DescriptionWrapper>
  );
}
