import React from 'react';
import { RuleEditor } from './RuleEditor';
import { FormattedMessage } from 'react-intl';
import { Box, Button } from '@mui/material';
import { Help } from '@mui/icons-material';
import { useDocs } from '../../utils/DocsUtils';
import { DefaultValueEditor } from './DefaultValueEditor';

const RulesEditor: React.FC = () => {
  const docsUrl = useDocs('del');

  return (
    <>
      <Box sx={{ display: 'flex' }}>
        <Box flexGrow={1} />
        <Button variant='outlined' endIcon={<Help />} onClick={() => window.open(docsUrl, "_blank")}>
          <FormattedMessage id='buttons.help' />
        </Button>
      </Box>
      <RuleEditor type='visibility' />
      <RuleEditor type='requirement' />
      <RuleEditor type='canaddrow' />
      <RuleEditor type='canremoverow' />
      <DefaultValueEditor />
    </>
  );
}

export { RulesEditor };
