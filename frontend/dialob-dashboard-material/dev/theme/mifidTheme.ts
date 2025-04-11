import { tableCellClasses, radioClasses, PaletteOptions, Palette } from '@mui/material';
import { createTheme } from '@mui/material/styles';

const palette: Palette = {
	type: 'light',
	primary: {
		main: '#2e1441',
		light: '#dedede'
	},
	secondary: {
		main: '#9a6bba',
		dark: '#888888',
		light: '#f2c4cc',
	},
	common: {
		white: "#ffffff",
		black: "#000000"
	},
	text: {
		primary: '#575757',
		secondary: '#c6c6c6',
		disabled: '#f7f7f7',
	},
	success: {
		main: '#00EF7F',
		dark: '#067E46',
		light: '#4df4a5',
	},
	error: {
		main: '#ff0000',
		contrastText: '#9b0a25',
		light: '#ffebed',
	},
};

const theme = createTheme({
	breakpoints: {
		keys: ["xs", "sm", "md", "lg", "xl"],
		values: { xs: 0, sm: 530, md: 870, lg: 1250, xl: 1920 }
	}
});

export const mifidTheme = createTheme(theme, {
	palette: palette,
	typography: {
		fontFamily: "Campton-Light, Arial, sans-serif",
		color: palette.text.primary,
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
			color: palette.error.main,
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
		MuiOutlinedInput: {
			styleOverrides: {
				root: {
					height: 46,
					fontFamily: "Campton-Light, Arial, sans-serif",
					color: palette.text.main,
					"&.Mui-focused .MuiOutlinedInput-notchedOutline": {
						borderColor: palette.success.light,
						'::selection': {
							backgroundColor: palette.success.light
						}
					},
					"& .MuiInputBase-input, .MuiOutlinedInput-input": {
						padding: "0px 10px",
					},
					"&.Mui-disabled": {
						color: palette.primary.light,
						borderColor: `1px solid ${palette.primary.light}`
					},
				}
			}
		},
		MuiButton: {
			styleOverrides: {
				root: {
					padding: "9px 18px",
					margin: "0px 0px 5px",
					borderRadius: "5px",
					fontSize: 18,
					textTransform: "none",
					backgroundColor: palette.success.main,
					color: palette.text.primary,
					minHeight: 50,
					minWidth: 36,
					fontFamily: "Campton-Light, Arial, sans-serif",
					'&:hover': {
						backgroundColor: palette.success.dark,
						color: palette.common.white,
					},
					'&.Mui-disabled': {
						backgroundColor: palette.text.secondary,
						color: palette.common.white,
						cursor: "not-allowed",
					},
				}
			}
		},
		MuiToolbar: {
			styleOverrides: {
				root: {
					backgroundColor: palette.secondary.dark,
					borderBottom: "none",
					clear: "both",
					height: "80px",
					minWidth: "100%",
					display: "flex",
					fontFamily: "Campton-Light, Arial, sans-serif",
					justifyContent: "space-between",
					'@media (max-width: 1550px)': {
						paddingLeft: 24,
					},
				}
			}
		},

		MuiFormHelperText: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					fontSize: 18,
					fontWeight: 450,
					color: "#DC143C"
				}
			}
		},
		MuiIcon: {
			styleOverrides: {
				root: {
					fontSize: 14,
				}
			}
		},
		MuiSelect: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					borderRadius: '5px',
					"&>fieldset": {
						textAlign: 'left'
					},
					'&.Mui-focused .MuiOutlinedInput-notchedOutline': {
						color: palette.common.black,
						border: `1px solid ${palette.success.main}`,
					},
					"&.Mui-focused, .MuiFormLabel-root": {
						color: palette.common.black,
					},
				}
			}
		},
		MuiAutocomplete: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					borderRadius: '5px',
					width: '275px',
					"& .MuiAutocomplete-root, .MuiOutlinedInput-root, .MuiAutocomplete-input": {
						padding: "0 0 0 2px"
					},
					"&>fieldset": {
						textAlign: 'left'
					},
					'&.Mui-focused .MuiOutlinedInput-notchedOutline': {
						color: palette.common.black,
						border: `1px solid ${palette.success.main}`,
					},
					"&.Mui-focused, .MuiFormLabel-root": {
						color: palette.common.black,
					},
					"& .MuiButtonBase-root, .MuiIconButton-root, .MuiAutocomplete-popupIndicator": {
						backgroundColor: 'transparent',
						width: 'auto',
						height: 'auto',
						color: 'rgba(0, 0, 0, 0.54)',

						'&:hover': {
							backgroundColor: 'transparent',
						},
						"& .MuiSvgIcon-root .MuiSvgIcon-fontSizeSmall .MuiSvgIcon-root": {
							transform: 'scale(1,1)',
							width: '20px',
							height: '20px',
							fontSize: '20px'

						}

					}

				}
			}
		},
		MuiList: {
			styleOverrides: {
				root: {
					paddingTop: 0,
					paddingBottom: 0,
					marginTop: "4px",
				}
			}
		},
		MuiMenuItem: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					fontSize: 16,
					padding: "10px",
					height: "30px",
					borderRight: `1px solid ${palette.success.light}`,
					borderLeft: `1px solid ${palette.success.light}`,
					'&:first-child': {
						borderTop: `1px solid ${palette.success.light}`,
					},
					'&:last-child': {
						borderBottom: `1px solid ${palette.success.light}`,
					},
					'&:hover': {
						backgroundColor: palette.text.secondary,
						color: palette.text.primary,
					},
					'&.Mui-selected': {
						backgroundColor: palette.text.secondary,
						color: palette.text.primary,
						'&:hover': {
							backgroundColor: palette.text.secondary,
							color: palette.text.primary,
						},
					}
				}
			}
		},
		MuiTableCell: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					fontSize: 18,
					lineHeight: "36px",
					border: `1px solid ${palette.primary.light}`,
					[`&.${tableCellClasses.head}`]: {
						fontFamily: "Campton-SemiBold, Arial, sans-serif",
						backgroundColor: palette.success.main,
						color: palette.text.primary,
						padding: '7px 10px',
					},
					[`&.${tableCellClasses.body}`]: {
						padding: '7px 10px',
						color: palette.text.primary,
					},
				}
			}
		},
		MuiTableRow: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					'&:nth-of-type(odd)': {
						backgroundColor: palette.common.white,
					},
					'&:nth-of-type(even)': {
						backgroundColor: palette.text.disabled,
					},
				}
			}
		},
		MuiIconButton: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					borderRadius: 4,
					width: 50,
					height: 50,
					backgroundColor: palette.success.light,
					color: palette.common.white,
					'&:hover': {
						backgroundColor: palette.success.main,
					},
				}
			}
		},
		MuiSvgIcon: {
			styleOverrides: {
				root: {

				}
			}
		},
		MuiLink: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					textDecoration: 'none',
				}
			}
		},
		MuiRadio: {
			defaultProps: {
				size: 'medium',
			},
			styleOverrides: {
				root: {
					[`&.${radioClasses.checked}`]: {
						color: palette.text.primary,
					},
				}
			}
		},
		MuiFormControlLabel: {
			styleOverrides: {
				root: {
					fontFamily: "Campton-Light, Arial, sans-serif",
					'&.MuiFormControlLabel-root': {
						display: "flex",
						alignItems: "center",
					}
				}
			}
		},
	},
});
