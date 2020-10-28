import { ItemAction, SessionError } from '@resys/dialob-fill-api';
import { useFillActions } from '@resys/dialob-fill-react';
import React from 'react';
import {DatePicker} from '@material-ui/pickers';
import moment from 'moment';
import { renderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

export interface DateFieldProps {
  datefield: ItemAction<'date'>['item'];
  errors: SessionError[];
};
export const DateField: React.FC<DateFieldProps> = ({ datefield, errors }) => {
  const {setAnswer} = useFillActions();
  const handleChange = (value: any) => {
    setAnswer(datefield.id, (value as moment.Moment).format('YYYY-MM-DD'));
  }
  const value = datefield.value ? datefield.value as string : null;
  return (
    <DescriptionWrapper text={datefield.description} title={datefield.label}>
      <DatePicker
        fullWidth={true}
        label={datefield.label}
        value={value}
        onChange={handleChange}
        autoOk
        required={datefield.required}
        error={errors.length > 0}
        helperText={renderErrors(errors)}
       />
    </DescriptionWrapper>
  );
}
