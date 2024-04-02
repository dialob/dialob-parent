import { IconButton, Table, TableBody, TableCell, TableHead, TableRow, Typography, styled } from "@mui/material"
import { ContextVariable, isContextVariable, useComposer } from "../../dialob"
import { Add } from "@mui/icons-material";
import ContextVariableRow from "./ContextVariableRow";
import { FormattedMessage } from "react-intl";

export const VariablesTable = styled(Table)(({ theme }) => ({
  '& .MuiTableCell-root': {
    border: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1),
  },
}));

const ContextVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { form, createVariable } = useComposer();
  const contextVariables = form.variables?.filter(v => isContextVariable(v)) as ContextVariable[];

  const handleAdd = () => {
    createVariable(true);
  }

  return (
    <VariablesTable>
      <TableHead>
        <TableRow>
          <TableCell sx={{ width: '5%' }} align='center'>
            <IconButton sx={{ p: 1, m: 1 }} onClick={handleAdd}><Add /></IconButton>
          </TableCell>
          <TableCell width='5%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.published' /></Typography>
          </TableCell>
          <TableCell width='30%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='10%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.type' /></Typography>
          </TableCell>
          <TableCell width='25%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.default' /></Typography>
          </TableCell>
          <TableCell width='20%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.description' /></Typography>
          </TableCell>
          <TableCell width='5%' align='center'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.users' /></Typography>
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {contextVariables?.map(variable => (
          <ContextVariableRow key={variable.name} variable={variable} onClose={onClose} />
        ))}
      </TableBody>
    </VariablesTable>
  )
}

export default ContextVariables;
