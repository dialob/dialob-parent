# Dialob Material Dashboard
This package provides a Dialob Admin UI View which enables users to easily interact with the Dialob forms.

## Install

```sh
pnpm add @dialob/dashboard-material 
```

## Quick-start
```jsx
import React from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { DialobAdmin, DialobAdminConfig } from "@dialob/dashboard-material";

const config: DialobAdminConfig = {
	csrf : {
		key : 'X-CSRF-TOKEN',													 
		value : '<csrf token value>'
	},
	dialobApiUrl: 'http://localhost:8085/dialob',
	setLoginRequired: cfg.setLoginRequired,
	setTechnicalError: cfg.setTechnicalError,
	language: "en"
}

const App = () => {
 return (
	  <ThemeProvider theme={yourTheme}>
			<DialobAdmin 
				config={config}
			/>
		</ThemeProvider>
 );
}
```

## Local Development Environment
In order to view this component localy without embedding this module into another application,
you can run in the root directory:

```sh
pnpm dev
```

And then you can change themes on the fly and see how the component behaves.

## Interfaces

```jsx
interface CsrfShape {
	'key': string,
	'value': string
}

interface DialobAdminConfig {
	dialobApiUrl: string; // base url for Dialob Api
	setLoginRequired: () => void; // function used for requesting possible authentication
	setTechnicalError: () => void; // function used for reporting technical errors
	language: string; // Current locale used by your application in ISO language code format ("en","sv","fi" and similar)
	csrf: CsrfShape | undefined; // Adjust according to your application csrf settings
}

interface DialobAdminViewProps {
	config: DialobAdminConfig;
	showNotification?: (message: string, severity: 'success' | 'error') => void;
}
```

## Additional information
If you want your Dialob Admin view to look good in your application you have to use a ThemeProvier and set styles inside the theme 
for these components: Table, TableRow, TableCell, OutlinedInput, SvgIcon, IconButton, Button

Dialob Admin component can also take the showNotification callback function,
This function is used for viewing snackbars after a successful or unsuccessful RESTful APIs calls.

```jsx
import React, { createContext, useContext, useState, ReactNode } from 'react';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert, { AlertProps } from '@mui/material/Alert';
import { useTheme, Theme } from '@mui/material/styles';

interface SnackbarContextType {
  showNotification: (message: string, severity: 'success' | 'error') => void;
}

const SnackbarContext = createContext<SnackbarContextType | undefined>(undefined);

export const useSnackbar = (): SnackbarContextType => {
  const context = useContext(SnackbarContext);
  if (!context) {
    throw new Error('useSnackbar must be used within a SnackbarProvider');
  }
  return context;
};

const Alert = React.forwardRef<HTMLDivElement, AlertProps>(function Alert(props, ref) {
  return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
});

interface SnackbarProviderProps {
  children: ReactNode;
}

export const SnackbarProvider: React.FC<SnackbarProviderProps> = ({ children }) => {
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('success');
  const theme = useTheme<Theme>();

  const showNotification = (message: string, severity: 'success' | 'error') => {
    setSnackbarMessage(message);
    setSnackbarSeverity(severity);
    setSnackbarOpen(true);
  };

  const handleClose = () => {
    setSnackbarOpen(false);
  };

  return (
    <SnackbarContext.Provider value={{ showNotification }}>
      {children}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }} 
      >
        <Alert 
          onClose={handleClose} 
          severity={snackbarSeverity} 
          sx={{ 
            width: '100%',
            backgroundColor: snackbarSeverity === 'success' ? theme.palette.success.main : theme.palette.error.main,
            color: snackbarSeverity === 'success' ? theme.palette.text.primary : theme.palette.common.white
          }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </SnackbarContext.Provider>
  );
};
```
