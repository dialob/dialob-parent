import React from "react";
import { IconButton, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { Add } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { BorderedTable } from "../TableEditorComponents";
import { isContextVariable } from "../../utils/ItemUtils";
import { Variable } from "../../types";
import ExpressionVariableRow from "./ExpressionVariableRow";
import { useSave } from "../../dialogs/contexts/saving/useSave";


const ExpressionVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { savingState, createVariable } = useSave();

  const handleAdd = () => {
    createVariable(false);
  }

  return (
    <BorderedTable sx={{ tableLayout: 'fixed' }}>
      <TableHead>
        <TableRow>
          <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
            <IconButton sx={{ p: 0.5 }} onClick={handleAdd}><Add color='success' /></IconButton>
          </TableCell>
          <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.published' /></Typography>
          </TableCell>
          <TableCell width='25%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='20%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.expression' /></Typography>
          </TableCell>
          <TableCell width='20%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.description' /></Typography>
          </TableCell>
          <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.users' /></Typography>
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {(savingState.variables?.filter(v => !isContextVariable(v)) as Variable[]).map((item, index) => (
          <ExpressionVariableRow key={index} index={index} item={item} onClose={onClose} />
        ))}
      </TableBody>
    </BorderedTable>
  )
}

export default ExpressionVariables;
