import { Button, Dialog, SvgIcon, Typography, TableRow, TableCell, tableCellClasses, OutlinedInput, IconButton } from '@mui/material';
import { styled } from '@mui/material/styles';

// Buttons

export const StyledTagEditDialogButton = styled(Button)(({ theme }) => ({
	fontSize: theme.spacing(2),
	padding: '6px 15px',
	color: theme.palette.text.primary,
	backgroundColor: theme.palette.success.main,
	textTransform: 'none',
	'&:hover': {
		backgroundColor: theme.palette.success.dark,
		color: theme.palette.common.white,
	},
}))

// SvgIcons

export const StyledIcon = styled(SvgIcon)(({ theme }) => ({
	color: theme.palette.text.primary,
}))

// IconButtons

export const StyledIconButton = styled(IconButton)(({ theme }) => ({
	borderRadius: theme.spacing(1),
	'&:hover': {
		backgroundColor: theme.palette.success.dark,
		'&> svg': {
			color: theme.palette.common.white
		}
	}
}))

export const ActionIconButton = styled(IconButton)(({ theme }) => ({
	borderRadius: '5px',
	width: "50px",
	height: "50px",
	backgroundColor: theme.palette.success.main,
	'&:hover': {
		backgroundColor: theme.palette.success.dark,
		'&> svg': {
			color: theme.palette.common.white
		}
	}
}))

// TableCells

export const StyledTableCell = styled(TableCell)(({ theme }) => ({
	border: `1px solid ${theme.palette.primary.light}`,
	margin: "1px",
	padding: theme.spacing(1),
	[`&.${tableCellClasses.head}`]: {
		backgroundColor: theme.palette.success.main,
		color: theme.palette.text.primary,
		fontWeight: 550,
		fontSize: theme.typography.body1.fontSize,
		fontFamily: "Campton-SemiBold, Arial, sans-serif",
	},
	[`&.${tableCellClasses.body}`]: {
		fontSize: theme.spacing(2),
		fontFamily: "Campton-Light, Arial, sans-serif",
	},
}));

// TableRows

export const StyledTableRow = styled(TableRow)(({ theme }) => ({
	'&:nth-of-type(even)': {
		backgroundColor: theme.palette.text.disabled,
	},
}));

// OutlineInput

export const StyledOutlinedInput = styled(OutlinedInput)(({ theme }) => ({
	height: "40px",
	'&: hover .MuiOutlinedInput-notchedOutline': {
		borderColor: theme.palette.success.main,
	},
	"&.Mui-focused .MuiOutlinedInput-notchedOutline": {
		border: `1px solid ${theme.palette.success.main}`,
	},
}))

// Dialogs

export const StyledDialog = styled(Dialog)(({ theme }) => ({
	'& .MuiDialogContent-root': {
		padding: "0 20px 20px 20px",
		border: 'none',
		height: "50%",
		top: theme.spacing(9)
	},
}));

// Typographies

export const StyledModalTypography = styled(Typography)(({ theme }) => ({
	fontSize: "22px",
	fontWeight: 500,
	color: theme.palette.text.primary,
}));
