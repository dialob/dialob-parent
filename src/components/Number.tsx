import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillLocale } from '@dialob/fill-react';
import * as React from 'react';
import { TextField, Box } from '@mui/material';
import { RenderErrors, buildSxFromProps } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import NumberFormat from 'react-number-format';

interface FormattedNumberFieldProps {
  onChange: (event: {target: {name: string; value: string}}) => void;
  name: string;
  decimalSeparator: string;
  integer: boolean;
}

//@ts-ignore
const FormattedNumberField = React.forwardRef<NumberFormat, FormattedNumberFieldProps>(
  function FormattedNumberField(props: FormattedNumberFieldProps, ref) {
    const { onChange, name, integer, decimalSeparator, ...other } = props;
    return (
      <NumberFormat {...other}
        getInputRef={ref}
        onValueChange={(values) => {
          onChange({
            target: {
              name: name,
              value: values.value
            }
          });
        }}
        isNumericString
        decimalSeparator={decimalSeparator}
        decimalScale={integer ? 0 : undefined}
      />
    );
  }
);

export interface NumberProps {
  number: ItemAction<any, any, number>['item'];
  errors: SessionError[];
  integer: boolean;
};

export const Number: React.FC<NumberProps> = ({number, errors, integer}) => {
  const {setAnswer} = useFillActions();
  const locale = useFillLocale();
  
  return (
    <DescriptionWrapper text={number.description} title={number.label}>
      <Box sx={buildSxFromProps(number.props)}>
        <TextField
          fullWidth
          label={number.label}
          required={number.required}
          error={errors.length > 0}
          value={number.value || ''}
          onChange={e => setAnswer(number.id, e.target.value)}
          helperText={<RenderErrors errors={errors} />}
          InputProps={{
            inputComponent: FormattedNumberField as any,
            inputProps: {
              decimalSeparator: (locale === 'en' ? '.' : ','),
              integer
            }
          }}
        />
      </Box>
    </DescriptionWrapper>
  );

}
