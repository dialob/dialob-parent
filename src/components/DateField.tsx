import React from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillLocale } from '@dialob/fill-react';
import { TextField } from '@material-ui/core';
import DatePicker from '@material-ui/lab/DatePicker';
import moment from 'moment';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';


export interface DateFieldProps {
  datefield: ItemAction<'date'>['item'];
  errors: SessionError[];
};
export const DateField: React.FC<DateFieldProps> = ({ datefield, errors }) => {
  const { setAnswer } = useFillActions();
  const locale = useFillLocale();
  const handleChange = (value: any) => {
    setAnswer(datefield.id, moment(value).format('YYYY-MM-DD'));
  }
  const value = datefield.value ? datefield.value as string : null;
  // https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md
  const format = moment.localeData(locale).longDateFormat('LL').replaceAll("Y", "y").replaceAll("D", "d");

  return (
    <DescriptionWrapper text={datefield.description} title={datefield.label}>
      <DatePicker
        label={datefield.label}
        value={value}
        onChange={handleChange}
        inputFormat={format}
        renderInput={(props) => (<TextField {...props} 
          fullWidth 
          required={datefield.required}
          error={errors.length > 0}
          helperText={<RenderErrors errors={errors} />} 
        />)}
      />
    </DescriptionWrapper> 
  );
}
