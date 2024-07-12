import React from 'react';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { IconButton, InputAdornment, TextField } from '@mui/material';
import { ClearIcon } from '@mui/x-date-pickers';

interface CustomDatePickerProps {
	value: Date | undefined;
	onChange: (date: Date | null) => void;
	handleDateClear: () => void;
}

const datePickerSx = {
	"& .MuiInputBase-root, MuiOutlinedInput-root": {
		height: "40px",
		paddingRight: "12px"
	}
}

const CustomDatePicker: React.FC<CustomDatePickerProps> = ({ value, onChange, handleDateClear }) => {
	return (
		<DatePicker
			value={value}
			onChange={onChange}
			slots={{
				textField: (params) => (
					<TextField
						{...params}
						sx={datePickerSx}
						InputProps={{
							...params.InputProps,
							endAdornment: (
								<InputAdornment position="end">
									{!value ? (
										params.InputProps?.endAdornment
									) : (
										<IconButton onClick={handleDateClear}>
											<ClearIcon />
										</IconButton>
									)}
								</InputAdornment>
							)
						}}
					/>
				)
			}}
		/>
	);
};

export default CustomDatePicker;
