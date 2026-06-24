import React from "react";
import { IconButton, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { Add } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { BorderedTable } from "../TableEditorComponents";
import { isContextVariable } from "../../utils/ItemUtils";
import { Variable } from "../../types";
import ExpressionVariableRow from "./ExpressionVariableRow";
import { useSave } from "../../dialogs/contexts/saving/useSave";
import { SortableFlatList } from "../SortableFlatList";
import { useSortableRow } from "../useSortableRow";
import { scrollToVariableRow } from "../../utils/ScrollUtils";

const expressionSortableId = (index: number) => `expression-${index}`;

const SortableExpressionVariableRow: React.FC<{
  item: Variable;
  index: number;
  sortableId: string;
  onClose: () => void;
  onInsertBelow: (index: number) => void;
}> = ({ sortableId, ...rowProps }) => {
  const { setNodeRef, style, handleProps } = useSortableRow(sortableId);
  return (
    <ExpressionVariableRow
      {...rowProps}
      setNodeRef={setNodeRef}
      style={style}
      handleProps={handleProps}
    />
  );
};

const ExpressionVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { savingState, createVariable, moveVariable } = useSave();

  const expressionVariableRows = React.useMemo(() => {
    if (!savingState.variables) {
      return [];
    }
    const rowgroups = Object.values(savingState.items || {}).filter(item => item.type === 'rowgroup');
    const scopedVariableIds = new Set(rowgroups.flatMap(rg => rg.items || []));

    return savingState.variables
      .map((variable, absoluteIndex) => ({ variable, absoluteIndex }))
      .filter((row): row is { variable: Variable; absoluteIndex: number } =>
        !isContextVariable(row.variable) && !scopedVariableIds.has(row.variable.name)
      );
  }, [savingState.variables, savingState.items]);

  const itemIds = expressionVariableRows.map((_, index) => expressionSortableId(index));

  const handleAdd = () => {
    createVariable(false);
    scrollToVariableRow();
  };

  const handleInsertBelow = (index: number) => {
    const row = expressionVariableRows[index];
    if (!row) {
      return;
    }
    createVariable(false, row.absoluteIndex);
    scrollToVariableRow(index + 1);
  };

  const handleReorder = (activeId: string, overId: string) => {
    const fromFilteredIndex = itemIds.indexOf(activeId);
    const toFilteredIndex = itemIds.indexOf(overId);
    const row = expressionVariableRows[fromFilteredIndex];
    if (fromFilteredIndex >= 0 && toFilteredIndex >= 0 && row) {
      moveVariable(row.variable.name, toFilteredIndex, false);
    }
  };

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
          <TableCell width='20%' sx={{ p: 0.5 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='25%' sx={{ p: 0.5 }}>
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
      <SortableFlatList itemIds={itemIds} onReorder={handleReorder}>
        <TableBody>
          {expressionVariableRows.map(({ variable }, index) => (
            <SortableExpressionVariableRow
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

export default ExpressionVariables;
