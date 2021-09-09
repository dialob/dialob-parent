import React from 'react';
import { appTheme } from '../src/theme';
import { AppHeader } from '../src/components/AppHeader';
import LocalizationProvider from '@material-ui/lab/LocalizationProvider';
import DatePicker from '@material-ui/lab/DatePicker';
import TimePicker from '@material-ui/lab/TimePicker';
import AdapterDateFns from '@material-ui/lab/AdapterDateFns';
import moment from 'moment';

import svLocale from 'date-fns/locale/sv';
import fiLocale from 'date-fns/locale/fi';
import etLocale from 'date-fns/locale/et';
import enLocale from 'date-fns/locale/en-US';


import createSession from './session';
import App from '../src/App';
import { Dialob } from '../src/dialob/Dialob';



const localeMap = {
  en: enLocale,
  et: etLocale,
  fi: fiLocale,
  sv: svLocale,
};
//MMMM dd, yyyy, MMMM dd, yyyy

import {
  ThemeProvider, CssBaseline, Paper, Button, Container, Grid, Typography, AppBar, Toolbar,
  Theme, TextField, Select, InputLabel, MenuItem, FormControl, FormLabel, FormGroup, FormControlLabel, Checkbox,
  StyledEngineProvider
} from '@material-ui/core';

import { IntlProvider } from 'react-intl'

const locale = 'en';

export default { title: 'Material Components' };



export const DialobSession = () => {
  const [locale, setLocale] = React.useState<string>("en");
  const onComplete = (session: any) => {
    console.log('Session completed callback:', session.id);
  }

  const session = createSession();

  return (
    <IntlProvider locale={locale}>
      <App setLocale={setLocale}>
        <Dialob key={session?.id} session={session} locale={locale} onComplete={onComplete} />
      </App>
    </IntlProvider>
  );
}


export const date = () => {
  const [locale, setLocale] = React.useState<"en">("en");
  const [value, setValue] = React.useState<string>();

  const handleChange = (value: any) => {
    setValue(moment(value).format('YYYY-MM-DD'));
  }
  //https://github.com/date-fns/date-fns/blob/master/docs/unicodeTokens.md
  const format = moment.localeData(locale).longDateFormat('LL').replace("YYYY", "yyyy").replace("D", "d");
  console.log(format, value);

  return (<ThemeProvider theme={appTheme}>

    <IntlProvider locale={locale}>
      <App setLocale={(newLocale) => setLocale(newLocale as any)}>
        <LocalizationProvider dateAdapter={AdapterDateFns} locale={localeMap[locale]}>
          <DatePicker
            label="test date"
            value={value}
            onChange={handleChange}
            inputFormat={format}
            renderInput={(props) => {
              if(props.inputProps && props.inputProps.placeholder) {
                props.inputProps.placeholder = format.substring(2, format.length);  
              }
               
              return <TextField {...props} />;
            }} />
        </LocalizationProvider>
      </App>
    </IntlProvider>
  </ThemeProvider>);
}



export const time = () => {
  const [value, setValue] = React.useState<string>();

  const handleChange = (value: any) => {
    console.log("on CHANGE", moment(value).format('HH:mm'));
    setValue(moment(value).format('HH:mm'));
  }

  console.log(value);
  return (<ThemeProvider theme={appTheme}>

    <IntlProvider locale={locale}>
      <LocalizationProvider dateAdapter={AdapterDateFns} locale={localeMap[locale]}>
        <TimePicker
          inputFormat={'HH:mm'}
          ampm={false}
          value={value ? moment(value, 'HH:mm').toDate() : null}
          onChange={handleChange}
          renderInput={(props) => <TextField {...props} fullWidth={true} margin='normal' />}
        />

      </LocalizationProvider>

    </IntlProvider>
  </ThemeProvider>);
}


export const appHeader = () => {
  return (<ThemeProvider theme={appTheme}>

    <IntlProvider locale={locale}>
      <AppHeader setLocale={(locale) => { }} />

    </IntlProvider>
  </ThemeProvider>);
}

const GridBody = () => {
  const [locale, setLocale] = React.useState<"en" | "fi" | "sv">('en');

  return (<IntlProvider locale='en'>
    <CssBaseline />
    <AppHeader setLocale={(locale) => setLocale(locale as any)} />

    <Container>
      <Grid container spacing={1}>

        <Grid item xs={12}>
          <Typography variant='h1'>Heading 1
                </Typography>
        </Grid>
        <Grid item xs={12}>

          <Grid item xs={12}>
            <Typography variant='h3'>Heading 3
                  <Button color='primary' variant='contained' size='small' style={{ float: 'right' }}>add</Button>
            </Typography>
          </Grid>

          <Paper variant='outlined' style={{ padding: '8px', margin: '8px' }}>
            <Grid>
              <Grid item xs={12}>

                <FormControl fullWidth={true} >
                  <FormLabel component='legend'>Please write stuff here</FormLabel>
                  <FormGroup>
                    <FormControlLabel key={0} control={<Checkbox checked={false} onChange={() => { }} />} label='first option' />
                    <FormControlLabel key={1} control={<Checkbox checked={true} onChange={() => { }} />} label='second option' />
                    <FormControlLabel key={2} control={<Checkbox checked={true} onChange={() => { }} />} label='third option' />
                  </FormGroup>
                </FormControl>
              </Grid>

              <Grid item xs={12}>
                <FormControl fullWidth={true} >
                  <LocalizationProvider dateAdapter={AdapterDateFns} locale={localeMap[locale]}>
                    <DatePicker
                      onChange={(e) => console.log(e)}
                      value={new Date()}
                      label={'datefield.label'}
                      renderInput={(props) => <TextField {...props} fullWidth={true} />}

                    />
                  </LocalizationProvider>
                </FormControl>
              </Grid>


              <Grid item xs={12}>
                <FormControl fullWidth={true}>
                  <TextField
                    id="filled-basic"
                    label="Last Name"
                    fullWidth
                    margin='normal' />
                </FormControl>
              </Grid>

              <Grid item xs={12}>

                <legend>Please write stuff here</legend>

                <FormControl fullWidth={true}>
                  <InputLabel>Age</InputLabel>
                  <Select>
                    <MenuItem value={10}>Ten</MenuItem>
                    <MenuItem value={20}>Twenty</MenuItem>
                    <MenuItem value={30}>Thirty</MenuItem>
                  </Select>

                </FormControl>

              </Grid>

              <Grid item xs={12}>
                <FormControl fullWidth={true}>
                  <TextField
                    id="filled-basic"
                    label="Address"
                    variant="filled"
                    fullWidth
                    margin='normal' />
                </FormControl>
              </Grid>
            </Grid>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  </IntlProvider>)
}

export const grid = () => {
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={appTheme}>
        <GridBody />
      </ThemeProvider>
    </StyledEngineProvider>
  );
}

export const form = () => {
  return (
    <StyledEngineProvider injectFirst>
      <ThemeProvider theme={appTheme}>
        <form noValidate autoComplete="off">
          <TextField id="filled-basic" label="Filled" variant="filled" fullWidth />
        </form>
      </ThemeProvider>
    </StyledEngineProvider>
  );
}

export const typography = () => (
  <>
    <StyledEngineProvider injectFirst>
      <CssBaseline />
      <ThemeProvider theme={appTheme}>
        <Container>
          <Grid container spacing={2}>
            <Grid item>
              <Paper>
                <Typography variant='h1' gutterBottom>Heading 1</Typography>
                <Typography variant='h2' gutterBottom>Heading 2</Typography>
                <Typography variant='h3' gutterBottom>Heading 3</Typography>
                <Typography variant='h4' gutterBottom>Heading 4</Typography>
                <Typography variant='h5' gutterBottom>Heading 5</Typography>
                <Typography variant='h6' gutterBottom>Heading 6</Typography>
                <Typography variant='body1' gutterBottom>
                  body1. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quos blanditiis tenetur
                  unde suscipit, quam beatae rerum inventore consectetur, neque doloribus, cupiditate numquam
                  dignissimos laborum fugiat deleniti? Eum quasi quidem quibusdam.
              </Typography>
                <Typography variant='body2' gutterBottom>
                  body2. Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quos blanditiis tenetur
                  unde suscipit, quam beatae rerum inventore consectetur, neque doloribus, cupiditate numquam
                  dignissimos laborum fugiat deleniti? Eum quasi quidem quibusdam.
              </Typography>
              </Paper>
            </Grid>
          </Grid>
        </Container>
      </ThemeProvider>
    </StyledEngineProvider>
  </>
);

export const components = () => {
  return (
    <ThemeProvider theme={appTheme}>
      <CssBaseline />
      <Container>
        <div>
          <AppBar position='relative' color='default'>
            <Toolbar>
              <Typography variant='h6' color='inherit'>
                Default App Bar
            </Typography>
            </Toolbar>
          </AppBar>
          <AppBar position='relative' color='primary'>
            <Toolbar>
              <Typography variant='h6' color='inherit'>
                Primary App Bar
            </Typography>
            </Toolbar>
          </AppBar>
          <AppBar position='relative' color='secondary'>
            <Toolbar>
              <Typography variant='h6' color='inherit'>
                Secondary App Bar
            </Typography>
            </Toolbar>
          </AppBar>
        </div>
        <div>
          <Button >Default</Button>
          <Button color='primary' >
            Primary
          </Button>
          <Button color='secondary' >
            Secondary
          </Button>
          <Button disabled >
            Disabled
          </Button>
          <Button href='#text-buttons' >
            Link
          </Button>
        </div>
        <div>
          <Button variant='outlined'>Default</Button>
          <Button color='primary' variant='outlined'>
            Primary
          </Button>
          <Button color='secondary' variant='outlined'>
            Secondary
          </Button>
          <Button disabled variant='outlined'>
            Disabled
          </Button>
          <Button href='#text-buttons' variant='outlined'>
            Link
          </Button>
        </div>
        <div>
          <Button variant='contained'>Default</Button>
          <Button color='primary' variant='contained'>
            Primary
          </Button>
          <Button color='secondary' variant='contained'>
            Secondary
          </Button>
          <Button disabled variant='contained'>
            Disabled
          </Button>
          <Button href='#text-buttons' variant='contained'>
            Link
          </Button>
        </div>
        <div>
          <TextField label='Standard' />
          <TextField label='Filled' variant='filled' />
          <TextField label='Outlined' variant='outlined' />
        </div>
        <div>
          <TextField label='Required' required />
          <TextField label='Standard' helperText='Some helper text' />
          <TextField label='Standard' error helperText='Failed' />
        </div>

      </Container>
    </ThemeProvider>
  );
}
