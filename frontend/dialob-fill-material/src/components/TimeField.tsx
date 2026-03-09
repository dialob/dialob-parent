import React, { useMemo } from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { Box } from '@mui/material';
import { format, parse } from 'date-fns';
import { RenderErrors, getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

const TIME_FORMAT = 'HH:mm';

export interface TimeFieldProps {
  timefield: ItemAction<'time'>['item'];
  errors: SessionError[];
};

const toPickerValue = (wire?: string): Date | null => {
  if (!wire) return null;
  return parse(wire, TIME_FORMAT, new Date());
};

const toWireValue = (d: Date | null): string | null => {
  if (!d) return null;
  try {
    return format(d, TIME_FORMAT);
  } catch {
    return null;
  }
};

export const TimeField: React.FC<TimeFieldProps> = ({ timefield, errors }) => {
  const { setAnswer } = useFillActions();

  const pickerValue = useMemo(() => toPickerValue(timefield.value), [timefield.value]);

  return (
    <DescriptionWrapper text={timefield.description} title={timefield.label}>
      <Box sx={getLayoutStyleFromProps(timefield.props)}>
        <TimePicker
          ampm={false}
          label={timefield.label}
          value={pickerValue}
          onChange={(d) => setAnswer(timefield.id, toWireValue(d))}
          readOnly={timefield.readOnly ?? false}
          slotProps={{
            textField: {
              fullWidth: true,
              margin: 'normal',
              required: timefield.required,
              error: errors.length > 0,
              helperText: <RenderErrors errors={errors} />,
            },
          }}
        />
      </Box>
    </DescriptionWrapper>
  );
}
