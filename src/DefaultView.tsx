import { useFillItem, useFillSession } from '@resys/dialob-fill-react';
import { Session } from '@resys/dialob-fill-api';
import React, { useEffect, useState } from 'react';
import { CircularProgress, Grid, Typography, Button, Paper, Dialog, DialogTitle, DialogContent, DialogActions } from '@material-ui/core';
import MomentUtils from '@date-io/moment';
import {MuiPickersUtilsProvider} from '@material-ui/pickers';
import moment from 'moment';
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
  const [completed, setCompleted] = useState(session.isComplete());
  const [completeConfirmationOpen, setCompleteConfirmationOpen] = useState(false);

  useEffect(() => {
    const listener = () => {
      setCompleted(session.isComplete());
    }
    session.on('update', listener);
    return () => {
      session.removeListener('update', listener);
    }
  }, [session]);

  let locale: 'en' | 'fi' | 'sv' = 'en';
  const sessionLocale = session.getLocale();
  if (sessionLocale && (sessionLocale === 'fi' || sessionLocale === 'sv')) {
    locale = sessionLocale;
  }

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
      <Grid container={true} justify='center' alignItems='center' style={{minHeight: '100vh'}}>
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
    <MuiPickersUtilsProvider libInstance={moment} utils={MomentUtils} locale={locale}>
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
          <Grid container justify='space-between' spacing={3} style={{marginTop: '1em', marginBottom: '1em'}}>
            <Grid item >
              {questionnaire.allowedActions.includes('PREVIOUS') && (
                <Button variant='contained' onClick={() => {window.scrollTo(0, 0); session.previous();}} startIcon={<ChevronLeftIcon />}>
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
                <Button variant='contained' onClick={() => {window.scrollTo(0, 0); session.next();}} endIcon={<ChevronRightIcon />}>
                  <FormattedMessage id='page.next' />
                </Button>
              )}
            </Grid>
          </Grid>
          )}
          <Dialog open={completeConfirmationOpen}>
            <DialogTitle><FormattedMessage id='Are you sure you want to complete this dialog?' /></DialogTitle>
            <DialogContent>
              <Typography variant='body1'>
                <FormattedMessage id='complete.confirmation.message' />
              </Typography>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => setCompleteConfirmationOpen(false)} color='secondary'><FormattedMessage id='complete.confirmation.cancel' /></Button>
                <Button onClick={() => session.complete()} color='primary' autoFocus><FormattedMessage id='complete.confirmation.confirm' /></Button>
            </DialogActions>
          </Dialog>
    </MuiPickersUtilsProvider>
  )
}
