import { IconButton, TableBody, TableCell, TableHead, TableRow } from "@mui/material"
import { Variable, isContextVariable, useComposer } from "../../dialob"
import { VariablesTable } from "./ContextVariables";
import { Add } from "@mui/icons-material";
import ExpressionVariableRow from "./ExpressionVariableRow";

const ExpressionVariables: React.FC = () => {
  const { form, createVariable } = useComposer();
  const expressionVariables = form.variables?.filter(v => !isContextVariable(v)) as Variable[];

  const handleAdd = () => {
    createVariable(false);
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
          <TableCell sx={{ fontWeight: 'bold', width: '40%' }}>
            ID
          </TableCell>
          <TableCell sx={{ fontWeight: 'bold', width: '50%' }}>
            Expression
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {expressionVariables?.map(variable => (
          <ExpressionVariableRow key={variable.name} variable={variable} />
        ))}
      </TableBody>
    </VariablesTable>
  )
}

export default ExpressionVariables;
