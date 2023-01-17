import { createTheme } from "@mui/material";

const palette = {
  common: { black: "#000", white: "#fff" },
  background: { paper: "#fff", default: "#fafafa" },
  primary: {
    light: "rgba(168, 222, 245, 1)",
    main: "rgba(75, 186, 234, 1)",
    dark: "rgba(16, 104, 142, 1)",
    contrastText: "rgba(0, 0, 0, 1)",
  },
  secondary: {
    light: "rgba(151, 116, 200, 1)",
    main: "rgba(76, 46, 117, 1)",
    dark: "rgba(52, 32, 80, 1)",
    contrastText: "#fff",
  },
  error: {
    light: "#e57373",
    main: "#f44336",
    dark: "#d32f2f",
    contrastText: "#fff",
  },
  text: {
    primary: "rgba(0, 0, 0, 0.87)",
    secondary: "rgba(0, 0, 0, 0.54)",
    disabled: "rgba(0, 0, 0, 0.38)",
   // hint: "rgba(0, 0, 0, 0.38)",
  },
};


export const altTheme = createTheme({

  palette,

  components: {
    MuiTextField: {
      defaultProps: {
        variant: 'filled'
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

    MuiInputLabel: {
      styleOverrides: {
        root: {
        focused: {
          color: palette.text.primary
        }
      }
      }
    }

  },

  typography: {
    h1: {
      fontSize: "2.5rem",
      lineHeight: 1
    },
    h2: {
      fontSize: "2.2rem",
      lineHeight: 1
    },
    h3: {
      fontSize: "2rem",
      lineHeight: 1
    },
    h4: {
      fontSize: "1.8rem",
      lineHeight: 1
    },
    h5: {
      fontSize: "1.5rem"
    }
  }

});
