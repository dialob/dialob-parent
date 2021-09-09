import React from 'react';
import { Session as DialobSession } from '@dialob/fill-api';
import { MaterialDialob, DefaultView } from '@dialob/fill-material';
import { Item } from './Item';
import { Theme, Box } from '@material-ui/core';
import { makeStyles } from '@material-ui/styles';

const useStyles = makeStyles((theme: Theme) => (
  {
    dialobContainer: {
      marginTop: theme.spacing(1)
    },
  }
));

export interface DialobProps {
  session: DialobSession | null;
  locale: string;
  onComplete?: (session: DialobSession) => void;
}

export const Dialob: React.FC<DialobProps> = ({ session, locale, onComplete }) => {
  const classes = useStyles();
  return (
    session &&
    <Box className={classes.dialobContainer}>
      <MaterialDialob session={session} locale={locale}>
        <DefaultView onComplete={onComplete}>
          {items => items.map(id => (<Item id={id} key={id} />))}
        </DefaultView>
      </MaterialDialob>
    </Box>
  );
};
