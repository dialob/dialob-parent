import { ItemAction } from '@dialob/fill-api';
import React, {useContext} from 'react';
import { Paper, Typography, Grid, Fade } from '@material-ui/core';
import { Theme } from '@material-ui/core/styles';
import { makeStyles, createStyles } from '@material-ui/styles';
import { GroupContext } from '../context/GroupContext';
import { Description } from './Description';

const useStyles = makeStyles((theme: Theme) => createStyles({
  paper: {
    padding: theme.spacing(2)
  }
})
);

type ColumnType = boolean | "auto" | 2 | 1 | 12 | 6 | 3 | 4 | 5 | 7 | 8 | 9 | 10 | 11 | undefined;

export interface GroupProps {
  group: ItemAction<'group' | 'rowgroup'>['item'];
};

export const Group: React.FC<GroupProps> = ({ group, children }) => {
  const classes = useStyles();
  const groupCtx = useContext(GroupContext);
  let columns = group.props?.columns || 1;
  columns = columns > 4 ? 4 : columns;
  //@ts-ignore
  const lg: ColumnType = columns > 1 ? Math.floor(12 / columns) : undefined;

  //@ts-ignore
  return (
    <GroupContext.Provider value={{level: groupCtx.level < 6 ? groupCtx.level + 1 : groupCtx.level}}>
      <Fade in={true}>
        <Paper elevation={groupCtx.level} className={classes.paper}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant='h3'>
                {group.label || <span>&nbsp;</span>}
              </Typography>
              <Description title={group.label} text={group.description} />
            </Grid>
            {
              React.Children.map(children, i =>
                <Grid item xs={12} lg={lg}>{i}</Grid>
              )
            }
          </Grid>
        </Paper>
      </Fade>
    </GroupContext.Provider>
  );

};