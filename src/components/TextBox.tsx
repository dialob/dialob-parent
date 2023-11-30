import { ItemAction, SessionError } from '@dialob/fill-api';
import { useFillActions } from '@dialob/fill-react';
import React from 'react';
import { TextField, Box } from '@mui/material';
import { RenderErrors } from './helpers';
import { DescriptionWrapper } from './DescriptionWrapper';
import { calculateMargin, getIndent } from '../util/helperFunctions';

export interface TextBoxProps {
  text: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};
export const TextBox: React.FC<TextBoxProps> = ({ text, errors }) => {
  const {setAnswer} = useFillActions();
  const indent = getIndent(parseInt(text.props?.indent || 0));
  const spacesTop = parseInt(text.props?.spacesTop || 0);
  const spacesBottom = parseInt(text.props?.spacesBottom || 0);
  const marginTop = calculateMargin(spacesTop);
  const marginBottom = calculateMargin(spacesBottom);

  return (
    <DescriptionWrapper text={text.description} title={text.label}>
      <Box sx={{pl: indent, mt: marginTop, mb: marginBottom}}>
        <TextField
          fullWidth={true}
          label={text.label}
          required={text.required}
          error={errors.length > 0}
          value={text.value || ''}
          onChange={e => setAnswer(text.id, e.currentTarget.value)}
          multiline
          helperText={<RenderErrors errors={errors} />}
        />
      </Box>
    </DescriptionWrapper>
  );
}
