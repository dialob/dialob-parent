import { tableCellClasses } from '@mui/material';
import { createTheme } from '@mui/material/styles';
import { PaletteOptions } from '@mui/material/styles/createPalette';

const palette = {
	type: 'light',
	primary: {
		main: '#2e1441',
		light: '#dedede'
	},
	secondary: {
		main: '#9a6bba',
		dark: '#652b8e',
		light: '#f2c4cc',
	},
	common: {
		white: '#ffffff',
		black: "#000000"
	},
	text: {
		primary: '#575757',
		secondary: '#c6c6c6',
		disabled: '#f7f7f7',
	},
	success: {
		main: '#00ef7f',
		dark: '#067e46',
		light: '#4df4a5',
	},
	error: {
		main: '#ff0000',
		contrastText: '#9b0a25',
		light: '#ffebed',
	},
};

export const popTheme = createTheme({
	palette: palette as PaletteOptions,
	typography: {
		fontFamily: "Campton-Light, Arial, sans-serif",
		body1: {
			fontSize: 18,
			color: palette.text.primary,
			fontFamily: "Campton-Light, Arial, sans-serif",
		},
		subtitle1: {
			fontSize: 18,
			color: palette.text.primary,
			textTransform: "none",
			fontFamily: "Campton-Light, Arial, sans-serif",
		},
		h1: {
			fontSize: 44,
			color: palette.text.primary,
			margin: "22px 0px",
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h2: {
			fontSize: 36,
			color: palette.text.primary,
			margin: "16px 0px 18px",
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h3: {
			fontSize: 30,
			color: palette.text.primary,
			fontFamily: "Campton-Light, Arial, sans-serif",
		},
		h4: {
			fontSize: 26,
			color: palette.text.primary,
			margin: "16px 0px 12px",
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h6: {
			fontSize: 16,
			margin: "16px 0px 8px 16px",
			color: palette.text.primary,
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
	},
	components: {
		MuiCssBaseline: {
			styleOverrides: {
				"@font-face": {
					fontFamily: "Campton-SemiBold, Campton-Light",
					src: 'url("/fonts/CamptonSemiBold.otf"), url("/fonts/CamptonLight.otf")',
				},
			}
		},
		MuiTableRow: {
			styleOverrides: {
				root: {
					'&:nth-of-type(even)': {
						backgroundColor: palette.text.disabled,
					}
				}
			}
		},
		MuiTableCell: {
			styleOverrides: {
				root: {
					border: `1px solid ${palette.primary.light}`,
					margin: "1px",
					padding: "8px",
					[`&.${tableCellClasses.head}`]: {
						backgroundColor: palette.success.main,
						color: palette.text.primary,
						fontWeight: 550,
						fontSize: "18px",
						fontFamily: "Campton-SemiBold, Arial, sans-serif",
					},
					[`&.${tableCellClasses.body}`]: {
						fontSize: "16px",
						fontFamily: "Campton-Light, Arial, sans-serif",
					}
				}
			}
		},
		MuiTable: {
			styleOverrides: {
				root: {
					borderCollapse: 'separate',
					borderSpacing: '2px 2px'
				}
			}
		},
		MuiIconButton: {
			variants: [
				{
					props: { color: 'error' },
					style: {
						'&:hover': { backgroundColor: palette.error.main }
					}
				}
			],
			styleOverrides: {
				root: {
					borderRadius: '5px',
					backgroundColor: "unset",
					'&:hover': {
						backgroundColor: palette.success.dark,
						'& > svg > svg': {
							color: palette.common.white
						}
					}

				}
			}
		},
		MuiSvgIcon: {
			styleOverrides: {
				root: {
					color: palette.text.primary
				}
			}
		},
		MuiOutlinedInput: {
			styleOverrides: {
				root: {
					'&: hover .MuiOutlinedInput-notchedOutline': {
						borderColor: palette.success.main,
					},
					"&.Mui-focused .MuiOutlinedInput-notchedOutline": {
						border: `1px solid ${palette.success.main}`,
					},
				}
			}
		},
		MuiButton: {
			variants: [
				{
					props: { color: 'error' },
					style: {
						color: palette.common.white,
						backgroundColor: palette.error.main,
					}
				}
			],
			styleOverrides: {
				root: {
					fontSize: "16px",
					padding: '6px 15px',
					color: palette.text.primary,
					backgroundColor: palette.success.main,
					textTransform: 'none',
					'&:hover': {
						backgroundColor: palette.success.dark,
						color: palette.common.white,
					}
				}
			}
		},
	},
	spacing: [1, 2, 4, 6, 8, 12, 16, 24, 36, 48, 64, 72],
});
