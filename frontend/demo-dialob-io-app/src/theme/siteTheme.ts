import { createTheme, PaletteOptions, SxProps } from "@mui/material/styles";

declare module '@mui/material/styles' {
  interface Palette {
    article: Palette['primary'];
    page: Palette['primary'];
    link: Palette['primary'];
    workflow: Palette['primary'];
    release: Palette['primary'];
    locale: Palette['primary'];
    import: Palette['primary'];
    activeItem: Palette['primary'];
    save: Palette['primary'];
    explorer: Palette['primary'];
    explorerItem: Palette['primary'];
    mainContent: Palette['primary'];
    uiElements: Palette['primary'];
    table: Palette['primary'];
  }
  interface PaletteOptions {
    article: Palette['primary'];
    page: Palette['primary'];
    link: Palette['primary'];
    workflow: Palette['primary'];
    release: Palette['primary'];
    locale: Palette['primary'];
    import: Palette['primary'];
    activeItem: Palette['primary'];
    save: Palette['primary'];
    explorer: Palette['primary'];
    explorerItem: Palette['primary'];
    mainContent: Palette['primary'];
    uiElements: Palette['primary'];
    table: Palette['primary'];

  }
}

export const MENU_HEIGHT = 50;
export const SCROLLBAR_WIDTH = '0.7em';
export const DRAWER_WIDTH = '18vw';

export const SCROLL_SX: SxProps = {
  overflowY: 'auto',
  overflowX: 'auto',
  '&::-webkit-scrollbar': {
    width: SCROLLBAR_WIDTH,
    height: SCROLLBAR_WIDTH,
  },
  '&::-webkit-scrollbar-track': {
    backgroundColor: 'uiElements.dark',
  },
  '&::-webkit-scrollbar-thumb': {
    borderRadius: '6px',
    backgroundColor: 'explorerItem.main',
  },
};

const palette = {
  mode: 'light',

  primary: {
    main: '#607196',
    contrastText: '#ffffff',
    dark: '#404c64',
    light: '#7686a7',
  },
  secondary: {
    main: '#3E668E',
    light: '#5585B4',
    dark: '#325171',
    contrastText: '#ffffff'
  },
  error: {
    main: '#e53935',
    dark: '#b71c1c',
    light: '#ef5350',
    contrastText: '#ffffff'
  },
  info: {
    main: '#554971',
    light: '#796AA0',
    dark: '#413857',
    contrastText: '#ffffff',
  },
  warning: {
    main: '#ff9800',
    light: '#ffac33',
    dark: '#b26a00',
    contrastText: '#ffffff',
  },
  success: {
    main: '#4caf50',
    dark: '#388e3c',
    light: '#66bb6a',
    contrastText: '#ffffff',
  },
  text: {
    primary: 'rgba(0,0,0,0.86)',
    secondary: 'rgba(0,0,0,0.55)',
    disabled: 'rgba(0,0,0,0.36)',
    hint: 'rgba(0,0,0,0.37)',
  },
  explorer: {
    main: 'rgb(17, 24, 39)', // background colour, dark grey-black
    dark: 'rgb(11, 15, 25)', // slightly darker for contrast
    light: 'rgb(24, 34, 50)', // slightly lighter for hover
    contrastText: '#ffffff' // white for contrast
  },
  explorerItem: {
    main: 'rgb(209, 213, 219)', // inactive item
    dark: 'rgb(16, 185, 129)', // active item
    light: 'rgba(255, 255, 255, 0.08)', // active item hover
    contrastText: 'rgba(253, 205, 73)' // indicative item
  },
  mainContent: {
    main: 'rgb(249, 250, 252)', // primary bg colour for behind content boxes, light gray
    dark: 'rgb(18, 24, 40)', // primary content text, dark gray/black
    light: 'rgb(255, 255, 255) ', // primary content bg colour, white
    contrastText: 'rgb(101, 116, 139)' // secondary content text, medium gray
  },
  uiElements: {
    main: 'rgb(80, 72, 229)', // primary ui element, blue-purple (button fill, button text, text, checkboxes, etc.)
    dark: '#F3F4F6', // table header gray
    light: 'rgba(80, 72, 229, 0.04)', // transparent purple for hover backgrounds, secondary button fill on hover
    contrastText: '#ffffff' // white for text on dark buttons
  },
  table: {
    main: '#F3F4F6', // table header gray
    dark: '#e8eaed', // table header darker gray
    light: '#fafafa', // light gray for table rows
    contrastText: '#000000' // black for text
  },
  article: {
    main: '#5048E5', // blue
    dark: '#3229e0',
    light: '#3229e0',
    contrastText: '#ffffff'
  },
  page: {
    main: '#14B8A6', // turquoise
    dark: '#109384',
    light: '#18dcc5',
    contrastText: '#ffffff',
  },
  link: {
    main: '#a0548b', // purple
    dark: '#864674',
    light: '#b26c9e',
    contrastText: '#ffffff'
  },
  workflow: {
    main: '#D14343', // red
    dark: '#c53030',
    light: '#db6b6b',
    contrastText: '#ffffff'
  },
  release: {
    main: '#91bc24', // green
    dark: '#779a1d',
    light: '#a9d831',
    contrastText: '#ffffff'
  },
  locale: {
    main: '#FFB020', // orange-yellow
    dark: '#f59f00',
    light: '#ffbf47',
    contrastText: '#ffffff'
  },
  import: {
    main: 'rgba(77, 144, 142)',
    dark: 'rgba(64, 119, 118)',
    light: 'rgba(86, 159, 158)',
    contrastText: '#ffffff'
  },
  activeItem: {
    main: '#edf6f9',
    dark: '#edf6f9',
    light: '#edf6f9',
    contrastText: '#000000'
  },
  save: {
    main: 'rgba(255, 99, 71, 0.8)',
    dark: 'rgba(255, 183, 3)',
    light: 'rgba(255, 183, 3)',
    contrastText: '#000000'
  },

}

const siteTheme = createTheme({
  palette: palette as PaletteOptions,

  typography: {
    fontFamily: "'IBM Plex Sans Arabic', sans-serif",
    h1: {
      fontSize: "2rem",
      lineHeight: 2,
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 600,
    },
    h2: {
      fontSize: "1.9rem",
      lineHeight: 1,
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 400,
      paddingTop: 15,
      paddingBottom: 15,
    },
    h3: {
      fontSize: "1.6rem",
      lineHeight: 1,
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 300,
      paddingTop: 15,
      paddingBottom: 15,
    },
    h4: {
      fontSize: "1.3rem",
      lineHeight: 1,
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 300
    },
    h5: {
      fontSize: "1.1rem",
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 300
    },
    h6: {
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 300
    },
    body1: {
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontWeight: 300,
    },
    body2: {
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontSize: "1rem",
    },
    caption: {
      fontFamily: "'IBM Plex Sans Arabic', sans-serif",
      fontSize: "0.7rem",
      fontWeight: 200
    }
  },



  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          overflowY: 'auto',
          overflowX: 'auto',
          '&::-webkit-scrollbar': {
            width: SCROLLBAR_WIDTH,
            height: SCROLLBAR_WIDTH,
          },
          '&::-webkit-scrollbar-track': {
            backgroundColor: palette.uiElements.dark,
          },
          '&::-webkit-scrollbar-thumb': {
            borderRadius: '6px',
            backgroundColor: palette.explorerItem.main,
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: palette.article.contrastText
        }
      }
    },
    MuiCardActions: {
      styleOverrides: {
        root: {

        }
      }
    },
    MuiListItem: {
      styleOverrides: {
        root: {
          paddingTop: 0,
          paddingBottom: 0,
        }
      }
    },

    MuiListItemText: {
      styleOverrides: {
        root: {
          paddingTop: 0,
          paddingBottom: 0,
          marginTop: 0,
          marginBottom: 0,
        },
        primary: {
          color: palette.text.primary,
          "&:hover": {
            color: palette.primary.dark,
            fontWeight: 'bold',
          }
        },
        secondary: {
          fontSize: '.9rem',
          color: palette.text.primary,
          "&:hover": {
            color: palette.primary.dark,
            fontWeight: 'bold',
          }
        }

      }
    },

    MuiButton: {
      styleOverrides: {
        root: {
          fontVariant: 'body2',
          borderRadius: 0,
          borderColor: palette.text.hint,
          textTransform: 'capitalize',
          borderWidth: '2px solid !important',
        }
      },
      defaultProps: {
        variant: 'outlined',
      }
    },

    MuiPaper: {
      styleOverrides: {
        root: {
          elevation: 1,
          borderColor: palette.secondary.main,
          transition: 'unset'
        }
      },
    },

    MuiMenu: {
      styleOverrides: {
        paper: {
          padding: 0,
        },
        list: {
          paddingTop: 0,
          paddingBottom: 0,
        },
      },
    },

    MuiMenuItem: {
      styleOverrides: {
        root: {
          paddingRight: "24px",
          paddingLeft: "24px",
          "&.Mui-selected": {
            backgroundColor: palette.activeItem.main,
            color: palette.text.disabled
          },
        }
      }
    },

    MuiStack: {
      styleOverrides: {
        root: {
          height: MENU_HEIGHT,
        }
      }
    },

    MuiDrawer: {
      styleOverrides: {
        root: {
          width: DRAWER_WIDTH,
        },
        paper: {
          width: DRAWER_WIDTH,
        },
      }
    },

    MuiTableCell: {
      styleOverrides: {
        root: {
          padding: "4px",
          border: `1px solid ${palette.table.main}`,
        },
      }
    }

  },

});

export { siteTheme };
