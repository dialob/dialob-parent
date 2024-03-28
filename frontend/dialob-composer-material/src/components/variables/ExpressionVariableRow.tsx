import React from 'react';
import { IconButton, Switch, TableCell, TableRow, TextField } from '@mui/material';
import { Delete } from '@mui/icons-material';
import { Variable, useComposer } from '../../dialob';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';

const ExpressionVariableRow: React.FC<{ variable: Variable }> = ({ variable }) => {
  const { updateExpressionVariable, deleteVariable } = useComposer();
  // name and published updates need to be connected to backend
  const [name, setName] = React.useState<string>(variable.name);
  const [published, setPublished] = React.useState<boolean>(variable.published ?? false);
  const [expression, setExpression] = React.useState<string>(variable.expression);

  React.useEffect(() => {
    const id = setTimeout(() => {
      updateExpressionVariable(variable.name, expression);
    }, 1000);
    return () => clearTimeout(id);
  }, [expression]);

  return (
    <TableRow key={variable.name}>
      <TableCell>
        <IconButton sx={{ p: 1, m: 1 }} onClick={() => deleteVariable(variable.name)}><Delete /></IconButton>
      </TableCell>
      <TableCell>
        <Switch sx={{ m: 1 }} checked={published} onChange={(e) => setPublished(e.target.checked)} />
      </TableCell>
      <TableCell>
        <TextField value={name} onChange={(e) => setName(e.target.value)} variant='standard' InputProps={{ disableUnderline: true }} fullWidth />
      </TableCell>
      <TableCell>
        <CodeMirror value={expression} onChange={(e) => setExpression(e)} extensions={[javascript({ jsx: true })]} />
      </TableCell>
    </TableRow>
  );
}

export default ExpressionVariableRow;
