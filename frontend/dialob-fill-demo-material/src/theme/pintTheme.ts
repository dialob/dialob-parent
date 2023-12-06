import { createTheme } from "@mui/material";

export const pintTheme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#f9a825',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
  },
  components: {
    MuiTooltip: {
      defaultProps: {
        arrow: true,
      }
    },
    MuiList: {
      defaultProps: {
        dense: true,
      }
    },
    MuiMenuItem: {
      defaultProps: {
        dense: true,
      }
    },
    MuiTable: {
      defaultProps: {
        size: 'small',
      }
    },
  },
  typography: {
    h1: {
      fontSize: '3.5rem',
      lineHeight: 0.95,
    },
    h2: {
      fontSize: '3rem',
    },
    h3: {
      fontSize: '2.4rem',
    },
  }
});
