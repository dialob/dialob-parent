import { createTheme } from '@mui/material/styles';
import type { PaletteOptions } from '@mui/material/styles';
import { tableCellClasses } from '@mui/material';

const palette = {
  mode: 'light',
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

export const theme1 = createTheme({
	palette: palette as PaletteOptions,
	spacing: 8,
	typography: {
		fontFamily: "Campton-Light, Arial, sans-serif",
		body1: {
			fontSize: 18,
			color: palette.text.primary,
		},
		subtitle1: {
			fontSize: 18,
			color: palette.text.primary,
			textTransform: "none",
		},
		h1: {
			fontSize: 44,
			color: palette.text.primary,
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h2: {
			fontSize: 36,
			color: palette.text.primary,
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h3: {
			fontSize: 30,
			color: palette.text.primary,
		},
		h4: {
			fontSize: 26,
			color: palette.text.primary,
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
		h6: {
			fontSize: 16,
			color: palette.text.primary,
			fontFamily: "Campton-SemiBold, Arial, sans-serif",
		},
	},
	components: {
		MuiCssBaseline: {
			styleOverrides: {
				"@font-face": {
					fontFamily: "Campton-SemiBold, Campton-Light",
					src: 'url("/CamptonSemiBold.otf"), url("/CamptonLight.otf")',
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
				},
				{
					props: { color: 'secondary' },
					style: {
						backgroundColor: palette.success.main
					}
				},
				{
					props: { color: 'warning' },
					style: {
						background: palette.common.white,
						color: palette.text.primary,
						"&:hover": {
							background: palette.common.white,
							'& > svg > svg': {
								color: palette.text.primary
							}
						}
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
		MuiTabs: {
			styleOverrides: {
				root: {
					'& .MuiTabs-indicator': {
						backgroundColor: palette.success.main,
					},
					"& .MuiTab-root.Mui-selected": {
						color: palette.success.main
					}
				}
			}
		},
		MuiTab: {
			styleOverrides: {
				root: {
					fontSize: 15,
					fontWeight: 550,
					color: palette.text.primary,
					'&:hover': {
						backgroundColor: palette.text.disabled,
						color: palette.success.main,
					},
				}
			}
		},
		MuiStepper: {
			styleOverrides: {
				root: {
					margin: "0px 24px",
					"& .MuiSvgIcon-root.MuiStepIcon-root.Mui-active": {
						color: palette.secondary.dark,
					},
					"& .MuiSvgIcon-root.MuiStepIcon-root.Mui-completed": {
						color: palette.success.dark,
					}
				}
			}
		},
		MuiListItemButton: {
			styleOverrides: {
				root: {
					paddingRight: 0,
					"&:hover": {
						backgroundColor: palette.secondary.dark,
						"&>div>svg, &>div>span": {
							color: palette.common.white
						},
					},
					"&.Mui-selected": {
						backgroundColor: palette.common.white,
						"&:hover": {
							backgroundColor: palette.secondary.dark,
						}
					}
				}
			}
		},
		MuiListItemText: {
			styleOverrides: {
				root: {
					paddingTop: "4px"
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
						'& .MuiSvgIcon-root': {
							color: palette.common.white,
						}
					},
					'&.Mui-disabled': {
						backgroundColor: palette.primary.light,
						color: palette.text.primary,
					}
				}
			}
		},
		MuiLink: {
			variants: [
				{
					props: { color: 'secondary' },
					style: {
						color: palette.secondary.main,
						'&:hover': {
							color: palette.secondary.dark,
						},
					}
				}
			],
			styleOverrides: {
				root: {
					textDecoration: 'none',
					color: palette.text.primary,
					'&:hover': {
						color: palette.success.main,
					},
				}
			}
		}
	},
});
