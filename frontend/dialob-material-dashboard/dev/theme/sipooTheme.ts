import { createTheme, PaletteOptions, Theme } from "@mui/material/styles";
import { } from "@mui/styles";

declare module '@mui/material/Button' {
	interface ButtonPropsVariantOverrides {
		contained: true;
		outlined: true;
		text: true;
	}
}
declare module '@mui/styles/defaultTheme' {
	interface DefaultTheme extends Theme { }
}

declare module '@mui/material/styles' {
	interface Palette {
		neutral: Palette['primary'];
		grayBackground: Palette['primary'];
		sipooOrange: Palette['primary'];
		messages: Palette['primary'];
	}
	interface PaletteOptions {
		neutral: Palette['primary'];
		grayBackground: Palette['primary'];
		sipooOrange: Palette['primary'];
		messages: Palette['primary'];
	}
}


const palette = {
	mode: 'light',

	action: {
		active: '#6B7280', // medium gray
		focus: 'rgba(55, 65, 81, 0.12)',
		hover: 'rgba(55, 65, 81, 0.04)',
		selected: 'rgba(55, 65, 81, 0.08)',
		disabledBackground: 'rgba(55, 65, 81, 0.12)',
		disabled: 'rgba(55, 65, 81, 0.26)'
	},
	grayBackground: {
		main: '#F8F9FA', // light gray
		light: '#DEE2E6', // medium gray
		dark: '#CED4DA', // dark gray
		contrastText: '#2f3131', // charcoal gray, same as footer background
	},
	sipooOrange: {
		main: '#f77f00', // generic orange
		contrastText: '#2f3131'
	},
	messages: {
		main: '#F5F5F5', // received messages: gray
		dark: '#E2EAF4', // sent messages: blue
		contrastText: '#212529', // charcoal black
	},
	background: {
		default: '#F9FAFC',
		paper: '#FFFFFF'
	},
	divider: '#E6E8F0',
	primary: {
		main: '#00687a', // sipoo blue
		contrastText: '#fff'
	},
	secondary: {
		main: '#0B815A',
		contrastText: '#FFFFFF'
	},
	success: {
		main: '#14B8A6',
		light: '#43C6B7',
		dark: '#0E8074',
		contrastText: '#FFFFFF'
	},
	info: {
		main: '#2196F3',
		light: '#64B6F7',
		dark: '#0B79D0',
		contrastText: '#FFFFFF'
	},
	warning: {
		main: '#FFB020',
		light: '#FFBF4C',
		dark: '#B27B16',
		contrastText: '#FFFFFF'
	},
	error: {
		main: '#D14343',
		light: '#DA6868',
		dark: '#922E2E',
		contrastText: '#FFFFFF'
	},
	text: {
		primary: '#212529', // charcoal black
		secondary: '#65748B',
		disabled: 'rgba(55, 65, 81, 0.48)'
	}
}


const sipooTheme = createTheme({
	palette: palette as PaletteOptions,

	typography: {
		button: {
			fontWeight: 600
		},
		fontFamily: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji"',
		body1: {
			fontWeight: 400,
			lineHeight: 1.5,
		},
		body2: {
			fontWeight: 400,
			lineHeight: 1.57
		},
		subtitle1: {
			fontWeight: 500,
			lineHeight: 1.75
		},
		subtitle2: {
			fontWeight: 500,
			lineHeight: 1.57
		},
		overline: {
			fontWeight: 600,
			letterSpacing: '0.5px',
			lineHeight: 2.5,
			textTransform: 'uppercase'
		},
		caption: {
			fontWeight: 400,
			lineHeight: 1.66
		},
		h1: {
			fontSize: '20pt',
			fontWeight: 700,
			lineHeight: 1.375
		},
		h2: {
			fontSize: '18pt',
			fontWeight: 700,
			lineHeight: 1.375
		},
		h3: {
			fontSize: '16pt',
			fontWeight: 700,
			lineHeight: 1.375
		},
		h4: {
			fontSize: '14pt',
			fontWeight: 700,
			lineHeight: 1.375
		},
		h5: {
			fontSize: '12pt',
			fontWeight: 400,
			lineHeight: 1.375,
		},
		h6: {
			fontWeight: 400,
			lineHeight: 1.375
		}
	},
	components: {
		MuiButton: {
			defaultProps: {
				disableElevation: false,
				color: 'primary',
			},
			variants: [
				{
					props: { variant: 'contained' },
					style: {
						textTransform: 'capitalize',
						textAlign: 'left',
						backgroundColor: 'primary.dark',
						fontSize: '12pt',
						fontWeight: 600,
					},
				},
				{
					props: { variant: 'outlined' },
					style: {
						textTransform: 'capitalize',
						textAlign: 'left',
						fontSize: '12pt',
						fontWeight: 600,
					},
				},
				{
					props: { variant: 'text' },
					style: {
						textTransform: 'capitalize',
						textAlign: 'left',
						fontSize: '11pt',
						fontWeight: 600,
						color: 'text.primary'
					},
				},
			],

			styleOverrides: {
				contained: {
					backgroundColor: 'primary.dark'
				},
				outlined: {
					borderWidth: '2px',
					'&:hover': {
						borderWidth: '2px',
					}
				},
				root: {
					textTransform: 'none',
					borderRadius: 2,
				},
				sizeSmall: {
					padding: '6px 16px'
				},
				sizeMedium: {
					padding: '8px 20px'
				},
				sizeLarge: {
					padding: '11px 24px'
				},
				textSizeSmall: {
					padding: '7px 12px'
				},
				textSizeMedium: {
					padding: '9px 16px'
				},
				textSizeLarge: {
					padding: '12px 16px'
				}
			}
		},
		MuiButtonBase: {
			defaultProps: {
				disableRipple: false,
			},
		},
		MuiCardContent: {
			styleOverrides: {
				root: {
					padding: '32px 24px',
					'&:last-child': {
						paddingBottom: '32px'
					}
				}
			}
		},
		MuiCardHeader: {
			defaultProps: {
				titleTypographyProps: {
					variant: 'h6'
				},
				subheaderTypographyProps: {
					variant: 'body2'
				}
			},
			styleOverrides: {
				root: {
					padding: '32px 24px'
				}
			}
		},
		MuiCssBaseline: {
			styleOverrides: {
				'*': {
					boxSizing: 'border-box',
					margin: 0,
					padding: 0
				},
				html: {
					MozOsxFontSmoothing: 'grayscale',
					WebkitFontSmoothing: 'antialiased',
					display: 'flex',
					flexDirection: 'column',
					minHeight: '100%',
					width: '100%'
				},
				body: {
					display: 'flex',
					flex: '1 1 auto',
					flexDirection: 'column',
					minHeight: '100%',
					width: '100%'
				},
				'#__next': {
					display: 'flex',
					flex: '1 1 auto',
					flexDirection: 'column',
					height: '100%',
					width: '100%'
				}
			}
		},
		MuiOutlinedInput: {
			styleOverrides: {
				notchedOutline: {
					borderColor: '#E6E8F0'
				}
			}
		},
		MuiTableHead: {
			styleOverrides: {
				root: {
					backgroundColor: '#F3F4F6',
					'.MuiTableCell-root': {
						color: '#374151'
					},
					borderBottom: 'none',
					'& .MuiTableCell-root': {
						borderBottom: 'none',
						fontSize: '12px',
						fontWeight: 600,
						lineHeight: 1,
						letterSpacing: 0.5,
						textTransform: 'uppercase'
					},
					'& .MuiTableCell-paddingCheckbox': {
						paddingTop: 4,
						paddingBottom: 4
					}
				}
			}
		}
	},

	shape: {
		borderRadius: 8
	},
	shadows: [
		'none',
		'0px 1px 1px rgba(100, 116, 139, 0.06), 0px 1px 2px rgba(100, 116, 139, 0.1)',
		'0px 1px 2px rgba(100, 116, 139, 0.12)',
		'0px 1px 4px rgba(100, 116, 139, 0.12)',
		'0px 1px 5px rgba(100, 116, 139, 0.12)',
		'0px 1px 6px rgba(100, 116, 139, 0.12)',
		'0px 2px 6px rgba(100, 116, 139, 0.12)',
		'0px 3px 6px rgba(100, 116, 139, 0.12)',
		'0px 2px 4px rgba(31, 41, 55, 0.06), 0px 4px 6px rgba(100, 116, 139, 0.12)',
		'0px 5px 12px rgba(100, 116, 139, 0.12)',
		'0px 5px 14px rgba(100, 116, 139, 0.12)',
		'0px 5px 15px rgba(100, 116, 139, 0.12)',
		'0px 6px 15px rgba(100, 116, 139, 0.12)',
		'0px 7px 15px rgba(100, 116, 139, 0.12)',
		'0px 8px 15px rgba(100, 116, 139, 0.12)',
		'0px 9px 15px rgba(100, 116, 139, 0.12)',
		'0px 10px 15px rgba(100, 116, 139, 0.12)',
		'0px 12px 22px -8px rgba(100, 116, 139, 0.25)',
		'0px 13px 22px -8px rgba(100, 116, 139, 0.25)',
		'0px 14px 24px -8px rgba(100, 116, 139, 0.25)',
		'0px 10px 10px rgba(31, 41, 55, 0.04), 0px 20px 25px rgba(31, 41, 55, 0.1)',
		'0px 25px 50px rgba(100, 116, 139, 0.25)',
		'0px 25px 50px rgba(100, 116, 139, 0.25)',
		'0px 25px 50px rgba(100, 116, 139, 0.25)',
		'0px 25px 50px rgba(100, 116, 139, 0.25)'
	]
})


export { sipooTheme };
