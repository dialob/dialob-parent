import { ItemAction } from '@dialob/fill-api';
import React, { PropsWithChildren, useContext } from 'react';
import { Paper, Typography, Grid, Fade, Box, Theme } from '@mui/material';
import { GroupContext } from '../context/GroupContext';
import { Description } from './Description';

type ColumnType = boolean | "auto" | 2 | 1 | 12 | 6 | 3 | 4 | 5 | 7 | 8 | 9 | 10 | 11 | number | undefined;

export interface GroupProps {
  group: ItemAction<'group' | 'rowgroup'>['item'];
};

export const Group: React.FC<PropsWithChildren<GroupProps>> = ({ group, children }) => {
  const { label, description, props } = group;
  const groupCtx = useContext(GroupContext);
  let columns = props?.columns || 1;
  columns = columns > 4 ? 4 : columns;
  const lg: ColumnType = columns > 1 ? Math.floor(12 / columns) : undefined;
  const border = props?.border;
  const backgroundColor = props?.color;
  const invisible = props?.invisible;
  const indent = parseInt(props?.indent || 0) + 2;
  const spacesTop = parseInt(props?.spacesTop ?? undefined);
  const spacesBottom = parseInt(props?.spacesBottom ?? undefined);
  const contentSx = {
    p: 2,
    ...(backgroundColor && { backgroundColor: (theme: Theme) => theme.palette.background.default }),
    ...(spacesTop && { marginTop: spacesTop }),
    ...(spacesBottom && { marginBottom: spacesBottom })
  }

  const childItems = React.Children.map(children, i => <Grid item xs={12} lg={lg}>{i}</Grid>);

  const groupContent: any = invisible ? (
    <Grid container spacing={2} sx={{ paddingLeft: indent }}>{childItems}</Grid>
  ) : (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Typography variant='h3' sx={{ mt: 2, mb: 3 }}>
          {label || <span>&nbsp;</span>}
        </Typography>
        <Description title={label} text={description} />
      </Grid>
      <Grid container spacing={2} sx={{ paddingLeft: indent }}>{childItems}</Grid>
    </Grid>
  );



  return (
    /*@ts-ignore*/
    <GroupContext.Provider value={{ level: groupCtx.level < 6 ? groupCtx.level + 1 : groupCtx.level }}>
      <Fade in={true}>
        {border && !invisible ? (
          <Paper elevation={groupCtx.level} sx={contentSx}>
            {groupContent}
          </Paper>
        ) : (
          <Box sx={contentSx}>
            {groupContent}
          </Box>
        )}
      </Fade>
    </GroupContext.Provider>
  );
};
