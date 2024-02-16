import { Table, TextField, styled } from "@mui/material";

export const StyledTable = styled(Table)(({ theme }) => ({
  '& .MuiTableCell-root': {
    border: `1px solid ${theme.palette.divider}`,
  },
}));

export const StyledTextField = styled(TextField)(({ theme }) => ({
  '& .MuiInputBase-root': {
    padding: theme.spacing(1),
  },
}));
