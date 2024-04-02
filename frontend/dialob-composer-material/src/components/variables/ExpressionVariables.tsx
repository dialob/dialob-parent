import { IconButton, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { Variable, isContextVariable, useComposer } from "../../dialob"
import { VariablesTable } from "./ContextVariables";
import { Add } from "@mui/icons-material";
import ExpressionVariableRow from "./ExpressionVariableRow";
import { FormattedMessage } from "react-intl";

const ExpressionVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { form, createVariable } = useComposer();
  const expressionVariables = form.variables?.filter(v => !isContextVariable(v)) as Variable[];

  const handleAdd = () => {
    createVariable(false);
  }

  return (
    <VariablesTable>
      <TableHead>
        <TableRow>
          <TableCell width='5%' align='center'>
            <IconButton sx={{ p: 1, m: 1 }} onClick={handleAdd}><Add /></IconButton>
          </TableCell>
          <TableCell width='5%' align='center'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.published' /></Typography>
          </TableCell>
          <TableCell width='30%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='35%'>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.type' /></Typography>
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
        {expressionVariables?.map(variable => (
          <ExpressionVariableRow key={variable.name} variable={variable} onClose={onClose} />
        ))}
      </TableBody>
    </VariablesTable>
  )
}

export default ExpressionVariables;
