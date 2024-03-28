import React from 'react';
import { RuleEditor } from './RuleEditor';
import { FormattedMessage } from 'react-intl';
import { Box, Button } from '@mui/material';
import { Help } from '@mui/icons-material';

const RulesEditor: React.FC = () => {
  return (
    <>
      <Box sx={{ display: 'flex' }}>
        <Box flexGrow={1} />
        <Button variant='outlined' endIcon={<Help />} onClick={() => window.open('https://docs.dialob.io/#/400_dialob_expression_language:_DEL/100_basic_del', "_blank")}>
          <FormattedMessage id='help' />
        </Button>
      </Box>
      <RuleEditor type='visibility' />
      <RuleEditor type='requirement' />
    </>
  );
}

export { RulesEditor };
