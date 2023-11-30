import React from 'react';
import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillLocale } from '@dialob/fill-react';
import { TextField, Box } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import {format} from 'date-fns';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import enGB from 'date-fns/locale/en-GB';
import enUS from 'date-fns/locale/en-US';
import fi from 'date-fns/locale/fi';
import sv from 'date-fns/locale/sv';
import et from 'date-fns/locale/et';
import { calculateMargin, getIndent } from '../util/helperFunctions';

const DATE_FORMAT_MAPPING: { [key: string]: string } = {
  'en': enGB.formatLong?.date({width: 'short'}),
  'en-gb': enGB.formatLong?.date({width: 'short'}),
  'en-us': enUS.formatLong?.date({width: 'short'}),
  'fi': fi.formatLong?.date({width: 'short'}),
  'sv': sv.formatLong?.date({width: 'short'}),
  'et': et.formatLong?.date({width: 'short'})
}

export interface DateFieldProps {
  datefield: ItemAction<'date'>['item'];
  errors: SessionError[];
};

const formatToWire = (value: any): string => {
  try {
    return format(value, 'yyyy-MM-dd');
  } catch (error) {
    return '';
  }
}

export const DateField: React.FC<DateFieldProps> = ({ datefield, errors }) => {
  const { setAnswer } = useFillActions();
  const locale = useFillLocale();
  const indent = getIndent(parseInt(datefield.props?.indent || 0));
  const spacesTop = parseInt(datefield.props?.spacesTop || 0);
  const spacesBottom = parseInt(datefield.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);

  const handleChange = (value: any) => {
    setAnswer(datefield.id, formatToWire(value) /*  format(value, 'yyyy-MM-dd') */);
  }
  const value = datefield.value ? datefield.value as string : null;
  // https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md
  //const inputFormat = moment.localeData(locale).longDateFormat('LL').replaceAll("Y", "y").replaceAll("D", "d");

  return (
    <DescriptionWrapper text={datefield.description} title={datefield.label}>
      <Box sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
        <DatePicker
          label={datefield.label}
          value={value}
          onChange={handleChange}
          inputFormat={DATE_FORMAT_MAPPING[locale]}
          renderInput={(props) => {
            /*
            if (props.inputProps && props.inputProps.placeholder) {
              props.inputProps.placeholder = format.substring(2, format.length);
            }*/
            return (<TextField {...props}
              fullWidth
              required={datefield.required}
              error={errors.length > 0}
              helperText={<RenderErrors errors={errors} />}
            />)
          }
          }
        />
      </Box>
    </DescriptionWrapper>
  );
}
