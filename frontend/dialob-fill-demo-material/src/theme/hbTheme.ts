import { createTheme } from '@mui/material';

export const hbTheme = createTheme({
  typography: {
    h1: {
      fontSize: 28,
      fontWeight: 'normal',
      fontFamily: 'StagMedium, Georgia, sans-serif',
      lineHeight: 1.5
    },
    h2: {
      fontSize: 20,
      fontWeight: 'normal',
      fontFamily: 'StagBook, Georgia, sans-serif',
      lineHeight: '26px'
    },
    h3: {
      fontSize: 20,
      fontWeight: 'normal',
      fontFamily: 'StagBook, Georgia, sans-serif',
      lineHeight: '26px',
      color: '#043b62',
    },
    h4: {
      fontSize: 18,
      fontWeight: 'normal',
      fontFamily: 'StagBook, Georgia, sans-serif',
      lineHeight: '22px'
    },
    h5: {
      fontSize: 14,
      fontWeight: 'normal',
      fontFamily: 'StagBook, Georgia, sans-serif',
      lineHeight: '22px'
    },
    h6: {
      fontSize: 12,
      fontWeight: 'bold',
      fontFamily: 'StagBook, Georgia, sans-serif',
      lineHeight: '22px'
    }
  },
  palette: {
    common: { black: "#000", white: "#fff" },
    background: { paper: "#fff", default: "rgba(245, 244, 245, 1)" },
    primary: {
      light: "rgba(188, 212, 237, 1)",
      main: "rgba(0, 92, 155, 1)",
      dark: "rgba(4, 59, 98, 1)",
      contrastText: "#fff"
    },
    secondary: {
      light: "rgba(226, 233, 237, 1)",
      main: "rgba(66, 181, 215, 1)",
      dark: "rgba(0, 128, 187, 1)",
      contrastText: "#fff"
    },
    error: {
      light: "#e57373",
      main: "#222",
      dark: "#d32f2f",
      contrastText: "#fff"
    },
    text: {
      primary: "#222",
      secondary: "rgba(0, 0, 0, 0.54)",
      disabled: "rgba(0, 0, 0, 0.38)",
    }
  },
  components: {
    MuiOutlinedInput: {
      defaultProps: {
        notched: true
      }
    },
    MuiTypography: {
      styleOverrides: {
        h1: {
          color: '#043b62'
        }
      }
    },
    MuiAppBar: {
      styleOverrides: {
        colorDefault: {
          backgroundColor: '#f1f1f1'
        }
      }
    },
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          fontSize: 20,
          lineHeight: 1.6,
          fontWeight: 500,
          fontFamily: 'StagBook, Georgia, sans-serif',
        }
      }
    },
    MuiFormLabel: {
      styleOverrides: {
        root: {
          color: "#222"
        }
      }
    },
    MuiTextField: {
      defaultProps: {
        variant: 'outlined'
      }
    },
    MuiFormControl: {
      defaultProps: {
        variant: 'standard'
      }
    }
  }
});
