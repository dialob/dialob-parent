import React from 'react'
import { CircularProgress, Grid } from '@mui/material'

export const ProgressSplash: React.FC = () => {
  return (
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
  )
}
