import React from "react";
import { IconButton, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import { useComposer } from "../../dialob"
import { Add } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import Tree, { TreeData, TreeDestinationPosition, TreeSourcePosition, moveItemOnTree } from "@atlaskit/tree";
import { INIT_TREE, buildTreeFromVariables } from "../../utils/TreeUtils";
import { BorderedTable } from "../TableEditorComponents";
import ContextVariableRow from "./ContextVariableRow";
import { VariableProps } from "./VariableComponents";
import { isContextVariable } from "../../utils/ItemUtils";
import { ContextVariable } from "../../types";

const renderVariableItem = (props: VariableProps) => {
  const { item, provided, onClose } = props;
  return (
    <ContextVariableRow item={item} provided={provided} onClose={onClose} />
  );
}

const ContextVariables: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const { form, createVariable, moveVariable } = useComposer();
  const [tree, setTree] = React.useState<TreeData>(INIT_TREE);

  React.useEffect(() => {
    const contextVariables = form.variables ? form.variables.filter(v => isContextVariable(v)) as ContextVariable[] : [];
    setTree(buildTreeFromVariables(contextVariables));
  }, [form.variables]);

  const handleAdd = () => {
    createVariable(true);
  }

  const onDragEnd = (
    source: TreeSourcePosition,
    destination?: TreeDestinationPosition,
  ) => {
    if (!destination || !destination.index) {
      return;
    }
    const sourceVariable = Object.values(tree.items).find(i => i.data?.index === source.index)?.data.variable as ContextVariable;
    const destinationVariable = Object.values(tree.items).find(i => i.data?.index === destination.index)?.data.variable as ContextVariable;
    const newTree = moveItemOnTree(tree, source, destination);
    setTree(newTree);
    moveVariable(sourceVariable, destinationVariable);
  };

  return (
    <BorderedTable sx={{ tableLayout: 'fixed' }}>
      <TableHead>
        <TableRow>
          <TableCell width='5%' align='center' sx={{ p: 0.5 }}>
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
          <TableCell width='20%' sx={{ p: 0.5 }}>
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
      <TableBody>
        <TableRow>
          <TableCell colSpan={7} sx={{ border: 'none' }}>
            <Tree
              tree={tree}
              renderItem={(props) => renderVariableItem({ ...props, onClose: onClose })}
              onDragEnd={onDragEnd}
              isDragEnabled
            />
          </TableCell>
        </TableRow>
      </TableBody>
    </BorderedTable>
  )
}

export default ContextVariables;
