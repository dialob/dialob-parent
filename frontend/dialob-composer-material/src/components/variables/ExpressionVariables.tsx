import React from "react";
import { IconButton, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { Variable, isContextVariable, useComposer } from "../../dialob"
import { Add } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import Tree, { TreeData, TreeDestinationPosition, TreeSourcePosition, moveItemOnTree } from "@atlaskit/tree";
import { INIT_TREE, buildTreeFromVariables } from "../../utils/TreeUtils";
import { renderVariableItem } from "./VariableComponents";
import { BorderedTable } from "../TableEditorComponents";

const ExpressionVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { form, createVariable, moveVariable } = useComposer();
  const [tree, setTree] = React.useState<TreeData>(INIT_TREE);

  React.useEffect(() => {
    const expressionVariables = form.variables?.filter(v => !isContextVariable(v)) as Variable[];
    setTree(buildTreeFromVariables(expressionVariables));
  }, [form.variables]);

  const handleAdd = () => {
    createVariable(false);
  }

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination || !destination.index) {
      return;
    }
    const sourceVariable = Object.values(tree.items).find(i => i.data?.index === source.index)?.data.variable as Variable;
    const destinationVariable = Object.values(tree.items).find(i => i.data?.index === destination.index)?.data.variable as Variable;
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
    moveVariable(sourceVariable, destinationVariable);
  };

  return (
    <BorderedTable sx={{ tableLayout: 'fixed' }}>
      <TableHead>
        <TableRow>
          <TableCell width='5%' align='center' sx={{ p: 1 }}>
            <IconButton sx={{ p: 1, m: 1 }} onClick={handleAdd}><Add /></IconButton>
          </TableCell>
          <TableCell width='7%' align='center' sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.published' /></Typography>
          </TableCell>
          <TableCell width='25%' sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.id' /></Typography>
          </TableCell>
          <TableCell width='35%' sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.type' /></Typography>
          </TableCell>
          <TableCell width='20%' sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.description' /></Typography>
          </TableCell>
          <TableCell width='8%' align='center' sx={{ p: 1 }}>
            <Typography fontWeight='bold'><FormattedMessage id='dialogs.variables.users' /></Typography>
          </TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        <TableRow>
          <TableCell colSpan={7}>
            <Tree
              tree={tree}
              renderItem={(props) => renderVariableItem({ ...props, onClose: onClose }, 'expression')}
              onDragEnd={onDragEnd}
              isDragEnabled
            />
          </TableCell>
        </TableRow>
      </TableBody>
    </BorderedTable>
  )
}

export default ExpressionVariables;
