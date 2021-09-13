import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions, useFillLocale } from '@dialob/fill-react';
import React from 'react';
import { TextField } from '@mui/material';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import NumberFormat from 'react-number-format';

interface FormattedNumberFieldProps {
  inputRef: (instance: NumberFormat| null) => void;
  onChange: (event: {target: {name: string; value: string}}) => void;
  name: string;
  decimalSeparator: string;
  integer: boolean;
}

const FormattedNumberField: React.FC<FormattedNumberFieldProps> = ({inputRef, onChange, name, decimalSeparator, integer, ...other}) => {
  return (
    <NumberFormat {...other}
      getInputRef={inputRef}
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
};

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
    </DescriptionWrapper>
  );

}
