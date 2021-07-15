import { useFillActions, useFillItem, useFillLocale, useFillSession } from '@dialob/fill-react';
import { Session } from '@dialob/fill-api';
import React, { useEffect, useState } from 'react';
import { CircularProgress, Grid, Typography, Button, Paper, Dialog, DialogTitle, DialogContent, DialogActions } from '@material-ui/core';
import LocalizationProvider from '@material-ui/lab/LocalizationProvider';
import AdapterDateFns from '@material-ui/lab/AdapterDateFns';

import svLocale from 'date-fns/locale/sv';
import fiLocale from 'date-fns/locale/fi';
import etLocale from 'date-fns/locale/et';
import enLocale from 'date-fns/locale/en-US';

const localeMap = {
  en: enLocale,
  et: etLocale,
  fi: fiLocale,
  sv: svLocale,
};

import { FormattedMessage } from 'react-intl';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import { Questionnaire } from './components/Questionnaire';

export interface DefaultViewProps {
  children: (itemIds: string[]) => React.ReactNode;
  onComplete?: (session: Session) => void;
};

export const DefaultView: React.FC<DefaultViewProps> = ({children, onComplete}) => {
  const { item: questionnaire } = useFillItem('questionnaire');
  const session = useFillSession();
  const sessionLocale = useFillLocale();
  const fillActions = useFillActions();
  const [completed, setCompleted] = useState(session.isComplete());
  const [completeConfirmationOpen, setCompleteConfirmationOpen] = useState(false);

  useEffect(() => {
    const updateListener = () => {
      setCompleted(session.isComplete());
    }
    const completeListener = (syncState: any) => {
      if (syncState === 'DONE' && session.isComplete() && onComplete) {
        onComplete(session);
      }
    }
    fillActions.on('update', updateListener);
    fillActions.on('sync', completeListener);
    return () => {
      fillActions.removeListener('update', updateListener);
      fillActions.removeListener('sync', completeListener);
    }
  }, [session]);

  const locale = sessionLocale || 'en';

  const preComplete = () => {
    setCompleteConfirmationOpen(true);
  }

  if(!session.id) {
    return (
      <>
        <div>
          <FormattedMessage id='session.not.found' />
        </div>
      </>
    )
  } else if (!questionnaire) {
    return (
      <Grid container={true} justifyContent='center' alignItems='center' style={{minHeight: '100vh'}}>
        <Grid item>
          <CircularProgress size={50}/>
        </Grid>
      </Grid>
    );
  }

  if (completed) {
    return (
      <Paper style={{marginTop: '2em', padding: '2em'}}>
        <Typography variant='h2'><FormattedMessage id='form.completed' /></Typography>
      </Paper>
    );
  }

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} locale={localeMap[locale]}>
        <Grid container spacing={1}>
          <Grid item xs={12}>
            <Typography variant='h1'>{questionnaire.label}</Typography>
          </Grid>
          {
             questionnaire.items && questionnaire.activeItem ?
              <Questionnaire>
                {questionnaire.items && children(questionnaire.items)}
              </Questionnaire>
              : null
          }
        </Grid>

          {questionnaire.allowedActions && (
          <Grid container justifyContent='space-between' spacing={3} style={{marginTop: '1em', marginBottom: '1em'}}>
            <Grid item >
              {questionnaire.allowedActions.includes('PREVIOUS') && (
                <Button variant='contained' onClick={() => {window.scrollTo(0, 0); fillActions.previous();}} startIcon={<ChevronLeftIcon />}>
                  <FormattedMessage id='page.previous' />
                </Button>
              )}
            </Grid>
            <Grid item>
              {!questionnaire.allowedActions.includes('NEXT') && (
                <Button variant='contained' color='primary'
                  disabled={!questionnaire.allowedActions.includes('COMPLETE')}
                  onClick={preComplete}>
                    <FormattedMessage id='complete' />
                </Button>
              )}
            </Grid>
            <Grid item >
              {questionnaire.allowedActions.includes('NEXT') && (
                <Button variant='contained' onClick={() => {window.scrollTo(0, 0); fillActions.next();}} endIcon={<ChevronRightIcon />}>
                  <FormattedMessage id='page.next' />
                </Button>
              )}
            </Grid>
          </Grid>
          )}
          <Dialog open={completeConfirmationOpen}>
            <DialogTitle><FormattedMessage id='complete.confirmation.title' /></DialogTitle>
            <DialogContent>
              <Typography variant='body1'>
                <FormattedMessage id='complete.confirmation.message' />
              </Typography>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setCompleteConfirmationOpen(false)} color='secondary'><FormattedMessage id='complete.confirmation.cancel' /></Button>
                <Button onClick={() => fillActions.complete()} color='primary' autoFocus><FormattedMessage id='complete.confirmation.confirm' /></Button>
            </DialogActions>
          </Dialog>
    </LocalizationProvider>
  )
}
