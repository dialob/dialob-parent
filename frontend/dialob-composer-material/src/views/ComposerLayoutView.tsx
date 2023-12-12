import React, { ReactElement, ReactNode } from 'react';
import { AppBar, Drawer, Typography, Box, Stack, Button, useTheme, Divider, InputBase, CSSObject, Container } from '@mui/material';
import { useComposer } from '../dialob';
import { ArrowDropDown, Check, Close, Download, Search, Support, Visibility } from '@mui/icons-material';
import { FormattedMessage, useIntl } from 'react-intl';

const HeaderButton: React.FC<{
  label: string, startIcon?: ReactElement,
  endIcon?: ReactElement
}> = ({ label, startIcon, endIcon }) => {
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }} startIcon={startIcon} endIcon={endIcon}>
      <FormattedMessage id={label} />
    </Button>
  );
};

const HeaderIconButton: React.FC<{ icon: ReactElement }> = ({ icon }) => {
  return (
    <Button variant='text' color='inherit' sx={{ px: 1 }}>
      {icon}
    </Button>
  );
};

const ComposerLayoutView: React.FC = () => {
  const theme = useTheme();
  const menuHeight = (theme.components?.MuiStack?.styleOverrides?.root as CSSObject)?.height;
  const intl = useIntl();
  const { form } = useComposer();
  const headerPaddingSx = { px: theme.spacing(1) };

  return (
    <Box display='flex'>
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
          <HeaderButton label={intl.formatMessage({ id: 'version' }) + ": " + intl.formatMessage({ id: 'latest' })} endIcon={<ArrowDropDown />} />
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
      <Drawer variant="permanent">
        <Box sx={{ mt: `${menuHeight}px` }}>
          Navigation pane
        </Box>
      </Drawer>
      <Container>
        <Box sx={{ mt: `${menuHeight}px` }}>
          Main area
        </Box>
      </Container>
      <Drawer variant="permanent" anchor="right">
        <Box sx={{ mt: `${menuHeight}px` }}>
          Error pane
        </Box>
      </Drawer>
    </Box>
  );
};

export default ComposerLayoutView;
