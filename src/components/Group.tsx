import { ItemAction } from '@resys/dialob-fill-api';
import React, {useContext} from 'react';
import { Paper, Typography, Grid, Fade } from '@material-ui/core';
import { makeStyles, createStyles, Theme } from '@material-ui/core/styles';
import { GroupContext } from '../context/GroupContext';
import { Description } from './Description';

const useStyles = makeStyles((theme: Theme) => createStyles({
  paper: {
    padding: theme.spacing(2)
  }
})
);

export interface GroupProps {
  group: ItemAction<'group' | 'rowgroup'>['item'];
};

export const Group: React.FC<GroupProps> = ({ group, children }) => {
  const classes = useStyles();
  const groupCtx = useContext(GroupContext);
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
                <Grid item xs={12}>{i}</Grid>
              )
            }
          </Grid>
        </Paper>
      </Fade>
    </GroupContext.Provider>
  );

};
