import React from 'react';
import { FormattedMessage } from 'react-intl';
import { CircularProgress, Grid, Container, Typography, Link } from '@mui/material';
import TenantSelector from './components/TenantSelector';
import TenantDashboard from './components/TenantDashboard';
import { AppConfig } from './types';
import { useTenantContext } from './context/useTenantContext';

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
      <Container>
        <Typography color="error" variant="h6">
          <FormattedMessage id="errors.message.tenants.loading" />
        </Typography>
      </Container>
    );
  }

  if (!tenants || tenants.length === 0) {
    return (
      <Container sx={{ p: 4 }}>
        <Typography color="error" variant="h6">
          <FormattedMessage id="errors.message.tenants.noAccess" />
        </Typography>
        <Typography variant="body1">
          <FormattedMessage
            id="errors.message.tenants.empty"
          />
          <Link href="/logout" sx={{ ml: 1 }}>
            <FormattedMessage id="errors.message.tenants.tryAgain" />
          </Link>
        </Typography>
      </Container>
    );
  }

  return (
    <Grid
      container
      spacing={0}
      direction="column"
      alignItems="left"
    >
      <Grid item xs={3}>
        <TenantSelector />
      </Grid>
      <Grid item xs={12}>
        {selectedTenant && <TenantDashboard appConfig={appConfig} />}
      </Grid>
    </Grid>
  );
};

export default App;