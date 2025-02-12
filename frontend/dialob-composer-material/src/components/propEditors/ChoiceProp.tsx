import { Autocomplete, MenuItem, Select } from '@mui/material';
import { StyledTextField } from '../TableEditorComponents';
import { ArrowDropDown } from '@mui/icons-material';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const ChoiceProp = (props: any) => {
  const { value, setValue, ...inputProps } = props;

  if (inputProps.allowAdditions) {
    return (
      <Autocomplete
        value={value}
        onChange={(_e, newValue) => setValue(newValue)}
        options={inputProps.options}
        renderInput={(params) => (
          <StyledTextField variant='standard' {...params} 
            InputProps={{ ...params.InputProps, disableUnderline: true, endAdornment: <ArrowDropDown /> }}
          />
        )}
        freeSolo
        disableClearable
      />
    )
  }

  return (
    <Select value={value} onChange={(e) => setValue(e.target.value)} fullWidth variant='standard' disableUnderline sx={{ p: 1 }}>
      {inputProps.options.map((option: { key: string, label: string }) => (
        <MenuItem key={option.key} value={option.key}>{option.label}</MenuItem>
      ))}
    </Select>
  );
}
