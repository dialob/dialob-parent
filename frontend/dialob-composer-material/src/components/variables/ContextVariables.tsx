import { IconButton, Table, TableBody, TableCell, TableHead, TableRow, styled } from "@mui/material"
import { ContextVariable, isContextVariable, useComposer } from "../../dialob"
import { Add } from "@mui/icons-material";
import ContextVariableRow from "./ContextVariableRow";

export const VariablesTable = styled(Table)(({ theme }) => ({
  '& .MuiTableCell-root': {
    border: `1px solid ${theme.palette.divider}`,
    padding: theme.spacing(1),
  },
}));

const ContextVariables: React.FC = () => {
  const { form, createVariable } = useComposer();
  const contextVariables = form.variables?.filter(v => isContextVariable(v)) as ContextVariable[];

  const handleAdd = () => {
    createVariable(true);
  }

  return (
    <VariablesTable>
      <TableHead>
        <TableRow>
          <TableCell sx={{ width: '5%', alignItems: 'center' }}>
            <IconButton sx={{ p: 1, m: 1 }} onClick={handleAdd}><Add /></IconButton>
          </TableCell>
          <TableCell sx={{ fontWeight: 'bold', width: '5%' }}>
            Published
          </TableCell>
          <TableCell sx={{ fontWeight: 'bold', width: '30%' }}>
            ID
          </TableCell>
          <TableCell sx={{ fontWeight: 'bold', width: '10%' }}>
            Type
          </TableCell>
          <TableCell sx={{ fontWeight: 'bold', width: '50%' }}>
            Default value
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {contextVariables?.map(variable => (
          <ContextVariableRow key={variable.name} variable={variable} />
        ))}
      </TableBody>
    </VariablesTable>
  )
}

export default ContextVariables;
