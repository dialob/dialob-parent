import { createTheme, PaletteOptions } from "@mui/material";

const palette = {
  type: 'light',
  primary: {
    main: '#078299',
    light: '#88CDEA',
  },
  secondary: {
    main: '#652b8e',
    light: '#cccccc',
    dark: '#e0e1e2',
  },
  common: {
    white: "#ffffff",
    black: "#000000"
  },
  text: {
    primary: '#666666',
    secondary: '#555555',
  },
  success: {
    main: '#009900',
    light: '#96C301',
  },
  error: {
    main: '#ff0000',
    contrastText: '#9b0a25',
    light: '#ffebed',
  },
  action: {
    selected: '#EADDDD'
  },
  info: {
    main: '#fffff8',
    light: '#efefef',
  },
};


export const popTheme = createTheme({
  palette: palette as PaletteOptions,

  typography: {
    fontFamily: "'Inter', sans-serif",
    h1: {
      fontSize: "44px",
      lineHeight: 2,
      textAlign: 'center',
      fontFamily: "'Inter', sans-serif",
      fontWeight: 600,
      color: palette.text.secondary
    },
    h2: {
      fontSize: "36px",
      lineHeight: 2,
      textAlign: 'center',
      fontFamily: "'Inter', sans-serif",
      fontWeight: 550,
      color: palette.text.secondary
    },
    h3: {
      fontSize: "30px",
      lineHeight: 1,
      textAlign: 'center',
      fontFamily: "'Inter', sans-serif",
      fontWeight: 550,
      color: palette.common.black
    },
    subtitle1: {
      fontFamily: "'Inter', sans-serif",
      fontSize: "18px",
      color: palette.text.primary
    },
    subtitle2: {
      fontFamily: "'Inter', sans-serif",
      fontSize: "16px",
      fontWeight: 400,
      color: palette.text.primary
    },
    body1: {
      fontFamily: "'Inter', sans-serif",
      fontSize: "18px",
      color: palette.text.primary
    },
    body2: {
      fontFamily: "'Inter', sans-serif",
      fontSize: "18px",
      color: '#444444',
      fontWeight: 400,
    },
  },
  components: {
    MuiOutlinedInput: {
      defaultProps: {
        notched: true
      }
    }
  },
  spacing: [1, 2, 4, 6, 8, 12, 16, 24, 36, 48, 64, 72],
});