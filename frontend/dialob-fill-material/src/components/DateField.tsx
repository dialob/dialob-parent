import React, { useMemo } from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillLocale } from '@dialob/fill-react';
import { Box } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { format, parse } from 'date-fns';
import { RenderErrors, getLayoutStyleFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';

import {
  sv,
  fi,
  et,
  enGB,
  enUS,
} from 'date-fns/locale';

const DATE_FORMAT_MAPPING: { [key: string]: string } = {
  'en': enGB.formatLong?.date({ width: 'short' }),
  'en-gb': enGB.formatLong?.date({ width: 'short' }),
  'en-us': enUS.formatLong?.date({ width: 'short' }),
  'fi': fi.formatLong?.date({ width: 'short' }),
  'sv': sv.formatLong?.date({ width: 'short' }),
  'et': et.formatLong?.date({ width: 'short' })
}

export interface DateFieldProps {
  datefield: ItemAction<'date'>['item'];
  errors: SessionError[];
};

const toPickerValue = (wire?: string): Date | null => {
  if (!wire) return null;
  return parse(wire, 'yyyy-MM-dd', new Date());
};

const toWireValue = (d: Date | null): string | null => {
  if (!d) return null;
  return format(d, 'yyyy-MM-dd');
};

export const DateField: React.FC<DateFieldProps> = ({ datefield, errors }) => {
  const { setAnswer } = useFillActions();
  const locale = useFillLocale();
  const pickerValue = useMemo(() => toPickerValue(datefield.value), [datefield.value]);
  const pickerFormat = DATE_FORMAT_MAPPING[locale];

  return (
    <DescriptionWrapper text={datefield.description} title={datefield.label}>
      <Box sx={getLayoutStyleFromProps(datefield.props)}>
        <DatePicker
          label={datefield.label}
          value={pickerValue}
          onChange={(d) => setAnswer(datefield.id, toWireValue(d))}
          format={pickerFormat}
          slotProps={{
            textField: {
              fullWidth: true,
              required: datefield.required,
              error: errors.length > 0,
              helperText: <RenderErrors errors={errors} />,
            },
          }}
        />
      </Box>
    </DescriptionWrapper>
  );
}
