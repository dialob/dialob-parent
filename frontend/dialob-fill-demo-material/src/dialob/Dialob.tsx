import React from 'react';
import { Session as DialobSession } from '@dialob/fill-api';
import { MaterialDialob, DefaultView } from '@dialob/fill-material';
import { Item } from './Item';
import { Box } from '@mui/material';

export interface DialobProps {
  session: DialobSession | null;
  locale: string;
  onComplete?: (session: DialobSession) => void;
}

export const Dialob: React.FC<DialobProps> = ({ session, locale, onComplete }) => {
  return (
    session &&
    <Box sx={{ mt: 1 }}>
      <MaterialDialob session={session} locale={locale}>
        <DefaultView onComplete={onComplete}>
          {items => items.map(id => (<Item id={id} key={id} />))}
        </DefaultView>
      </MaterialDialob>
    </Box>
  );
};
