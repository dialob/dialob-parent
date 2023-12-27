import React from 'react';
import { AppBar, Box, Divider, InputBase, Stack, Typography, useTheme, Button } from '@mui/material';
import { ArrowDropDown, Check, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';
import { useComposer } from '../../dialob';


const HeaderButton: React.FC<{
  label: string,
  startIcon?: React.ReactElement,
  endIcon?: React.ReactElement
}> = ({ label, startIcon, endIcon }) => {
  const intl = useIntl();
  const stringExists = !!intl.messages[label];
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }} startIcon={startIcon} endIcon={endIcon}>
      {stringExists ? <FormattedMessage id={label} /> : label}
    </Button>
  );
};

const HeaderIconButton: React.FC<{ icon: React.ReactElement }> = ({ icon }) => {
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }}>
      {icon}
    </Button>
  );
};

const MenuBar: React.FC = () => {
  const theme = useTheme();
  const intl = useIntl();
  const { form } = useComposer();
  const headerPaddingSx = { px: theme.spacing(1) };

  return (
    <AppBar position="fixed" color='inherit' sx={{ zIndex: theme.zIndex.drawer + 1 }}>
      <Stack direction='row' divider={<Divider orientation='vertical' flexItem />}>
        <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
          <Typography sx={{ fontWeight: 'bold' }}>
            Dialob Composer
          </Typography>
          <Typography sx={{ ml: 1 }}>
            {form.metadata.label}
          </Typography>
        </Box>
        <HeaderButton label='translations' />
        <HeaderButton label='variables' />
        <HeaderButton label='lists' />
        <HeaderButton label='options' />
        <HeaderButton label={intl.formatMessage({ id: 'version' }) + ": " + intl.formatMessage({ id: 'version.latest' })} endIcon={<ArrowDropDown />} />
        <HeaderIconButton icon={<Support fontSize='small' />} />
        <Box flexGrow={1} />
        <Box sx={{ display: 'flex', alignItems: 'center', ...headerPaddingSx }}>
          <InputBase placeholder={intl.formatMessage({ id: 'search' })} />
          <Search />
        </Box>
        <HeaderIconButton icon={<Download />} />
        <HeaderIconButton icon={<Check color='success' />} />
        <HeaderButton label='locales.english' endIcon={<ArrowDropDown />} />
        <HeaderIconButton icon={<Visibility fontSize='small' />} />
        <HeaderIconButton icon={<Close />} />
      </Stack>
    </AppBar>
  );
};

export default MenuBar;
