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
        <Button variant='outlined' endIcon={<Help />} onClick={() => window.open('https://github.com/dialob/dialob-parent/wiki/Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL', "_blank")}>
          <FormattedMessage id='buttons.help' />
        </Button>
      </Box>
      <RuleEditor type='visibility' />
      <RuleEditor type='requirement' />
    </>
  );
}

export { RulesEditor };
