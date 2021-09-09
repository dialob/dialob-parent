import { createTheme } from "@material-ui/core/styles";


const palette = {
  "common": {
    "black": "#000",
    "white": "#fff"
  },
  "background": {
    "paper": "#ffffff",
    "default": "#ffffff"
  },
  "primary": {
    "light": "rgba(151, 177, 233, 1)",
    "main": "rgba(102, 130, 183, 1)",
    "dark": "rgba(54, 86, 135, 1)",
    "contrastText": "rgba(255, 255, 255, 1)"
  },
  "secondary": {
    "light": "rgba(86, 93, 184, 1)",
    "main": "rgba(31, 52, 135, 1)",
    "dark": "rgba(0, 16, 89, 1)",
    "contrastText": "rgba(255, 255, 255, 1)"
  },
  "error": {
    "light": "#e57373",
    "main": "#f44336",
    "dark": "#d32f2f",
    "contrastText": "#fff"
  },
  "text": {
    "primary": "rgba(85, 85, 85, 100)",
    "secondary": "rgba(0, 0, 0, 0.54)",
    "disabled": "rgba(0, 0, 0, 0.38)",
    "hint": "rgba(0, 0, 0, 0.38)"
  },
};

export const appTheme = createTheme({
  palette,

  components: {

    MuiTypography: {
      styleOverrides: {
        root: {
          color: palette.text.primary,
        }
      }
    },
    MuiTextField: {
      defaultProps: {
        variant: 'filled'
      },

      styleOverrides: {
        root: {
          marginBottom: 0
        }
      }
    },
    MuiInputLabel: {
      styleOverrides: {
        formControl: {
          transform: 'translate(10px, 20px) scale(1)',
        }
      }
    },
    MuiFormLabel: {
      styleOverrides: {
        root: {
          zIndex: 10,
        }
      }
    },
    MuiFormGroup: {
      styleOverrides: {
        root: {
          marginTop: '20px'
        }
      },
    },

    MuiSelect: {
      defaultProps: {
        variant: 'filled'
      },
    },

    MuiGrid: {
      styleOverrides: {
        root: {
          margin: 2,
        },
        item: {
          margin: 10,
        }
      }
    },

    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 0,
          textTransform: 'uppercase'
        },
        contained: {
          minWidth: 200
        },
      }
    },
    MuiPaper: {
      defaultProps: {
        square: true
      }
    },
  },


  typography: {
    h1: {
      fontSize: "2.2rem",
      lineHeight: 1.5,
      padding: 10,
      marginLeft: '10px',
      marginRight: '10px',
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 800,
      textAlign: 'center',
      backgroundColor: palette.primary.main,
      color: palette.common.white
    },
    h2: {
      fontSize: "1.8rem",
      lineHeight: 1.5,
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 900,
    },
    h3: {
      fontSize: "1.4rem",
      lineHeight: 1.5,
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 900,
    },
    h4: {
      fontSize: "1.2rem",
      lineHeight: 1,
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 700
    },
    h5: {
      fontSize: "1.1rem",
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 700
    },
    h6: {
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 700
    },
    body1: {
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 600,
    },
    body2: {
      fontFamily: '"Mulish", sans-serif',
      fontWeight: 200
    }
  }
}
);
