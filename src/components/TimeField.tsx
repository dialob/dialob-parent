import React from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { TextField, Box } from '@mui/material';
import {format, parse} from 'date-fns';
import { RenderErrors, buildSxFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

const timeFormat = 'HH:mm';

export interface TimeFieldProps {
  timefield: ItemAction<'time'>['item'];
  errors: SessionError[];
};

const formatToWire = (value: any): string => {
  try {
    return format(value, timeFormat);
  } catch (error) {
    return '';
  }
}

export const TimeField: React.FC<TimeFieldProps> = ({ timefield, errors }) => {
  const { setAnswer } = useFillActions();

  const handleChange = (value: any) => {
    setAnswer(timefield.id, formatToWire(value));
  }
  const value = timefield.value ? timefield.value as string : null;

  return (
    <DescriptionWrapper text={timefield.description} title={timefield.label}>
      <Box sx={buildSxFromProps(timefield.props)}>
        <TimePicker
          ampm={false}
          label={timefield.label}
          value={value ? parse(value, timeFormat, new Date()) : null}
          onChange={handleChange}
          renderInput={(props) => <TextField {...props}
            fullWidth={true}
            margin='normal'
            required={timefield.required}
            error={errors.length > 0}
            helperText={<RenderErrors errors={errors} />}
          />}
        />
      </Box>
    </DescriptionWrapper>
  );
}
