import React from "react";
import { IconButton, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { Add } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { BorderedTable } from "../TableEditorComponents";
import { isContextVariable } from "../../utils/ItemUtils";
import { ContextVariable } from "../../types";
import { useSave } from "../../dialogs/contexts/saving/useSave";
import { SortableFlatList } from "../SortableFlatList";
import { scrollToVariableRow } from "../../utils/ScrollUtils";
import { SortableContextVariableRow } from "./SortableContextVariableRow";


const ContextVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { savingState, createVariable, moveVariable } = useSave();

  const contextVariableRows = React.useMemo(() => {
    if (!savingState.variables) {
      return [];
    }
    return savingState.variables
      .map((variable, absoluteIndex) => ({ variable, absoluteIndex }))
      .filter((row): row is { variable: ContextVariable; absoluteIndex: number } =>
        isContextVariable(row.variable)
      );
  }, [savingState.variables]);

  const itemIds = contextVariableRows.map((_, index) => `context-${index}`);

  const handleAdd = () => {
    createVariable(true);
    scrollToVariableRow();
  };

  const handleInsertBelow = (index: number) => {
    const row = contextVariableRows[index];
    if (!row) {
      return;
    }
    createVariable(true, row.absoluteIndex);
    scrollToVariableRow(index + 1);
  };

  const handleReorder = (activeId: string, overId: string) => {
    const fromFilteredIndex = itemIds.indexOf(activeId);
    const toFilteredIndex = itemIds.indexOf(overId);
    const row = contextVariableRows[fromFilteredIndex];
    if (fromFilteredIndex >= 0 && toFilteredIndex >= 0 && row) {
      moveVariable(row.variable.name, toFilteredIndex, true);
    }
  };

  return (
    <BorderedTable sx={{ tableLayout: 'fixed' }}>
      <TableHead>
        <TableRow>
          <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
            <IconButton sx={{ p: 0.5 }} onClick={handleAdd}><Add color='success' /></IconButton>
          </TableCell>
          <TableCell width='10%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.published' /></Typography>
          </TableCell>
          <TableCell width='25%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='10%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.type' /></Typography>
          </TableCell>
          <TableCell width='15%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.default' /></Typography>
          </TableCell>
          <TableCell width='20%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.description' /></Typography>
          </TableCell>
          <TableCell width='10%' align='center' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.users' /></Typography>
          </TableCell>
        </TableRow>
      </TableHead>
      <SortableFlatList itemIds={itemIds} onReorder={handleReorder}>
        <TableBody>
          {contextVariableRows.map(({ variable }, index) => (
            <SortableContextVariableRow
              key={itemIds[index]}
              sortableId={itemIds[index]}
              item={variable}
              index={index}
              onClose={onClose}
              onInsertBelow={handleInsertBelow}
            />
          ))}
        </TableBody>
      </SortableFlatList>
    </BorderedTable>
  )
}

export default ContextVariables;
