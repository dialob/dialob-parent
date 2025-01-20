import React from 'react';
import { FormattedMessage } from 'react-intl';
import { CircularProgress, Grid, Typography, Link, Box } from '@mui/material';
import TenantDashboard from './components/TenantDashboard';
import { AppConfig } from './types';
import { useTenantContext } from './context/useTenantContext';
import Header from './components/Header';

export const ProgressSplash: React.FC = () => (
  <Grid
    container
    spacing={0}
    direction="column"
    alignItems="center"
    justifyContent="center"
    sx={{ minHeight: '100vh' }}
  >
    <Grid item xs={3}>
      <CircularProgress size={100} thickness={5} />
    </Grid>
  </Grid>
);

const App: React.FC<{ appConfig: AppConfig }> = ({ appConfig }) => {
  const { tenants, selectedTenant, isLoading, error } = useTenantContext();

  if (isLoading) {
    return <ProgressSplash />;
  }

  if (error) {
    return (
      <>
        <Header />
        <Box p={2}>
          <Typography color="error" variant="h6">
            <FormattedMessage id="errors.message.tenants.loading" />
          </Typography>
        </Box>
      </>
    );
  }

  if (!tenants || tenants.length === 0) {
    return (
      <>
        <Header />
        <Box p={2}>
          <Typography color="error" variant="h6">
            <FormattedMessage id="errors.message.tenants.noAccess" />
          </Typography>
          <Typography variant="body1">
            <FormattedMessage id="errors.message.tenants.empty" />
            <Link href="/logout" sx={{ ml: 1 }}>
              <FormattedMessage id="errors.message.tenants.tryAgain" />
            </Link>
          </Typography>
        </Box>
      </>
    );
  }

  const noTenantSelected: JSX.Element = (
    <Box display="flex" justifyContent="center" pt={4}>
      <Typography variant='h4'>
        <FormattedMessage id="placeholders.tenants.unselected" />
      </Typography>
    </Box>
  )

  return (
    <>
      <Header />
      {selectedTenant ?
        <TenantDashboard appConfig={appConfig} /> :
        noTenantSelected
      }
    </>
  );
};

export default App;