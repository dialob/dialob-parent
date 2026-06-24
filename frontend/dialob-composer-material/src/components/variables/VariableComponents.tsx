import React from 'react';
import { Box, Button, IconButton, List, ListItemButton, Menu, MenuItem, Popover, Switch, TextField, Tooltip, Typography } from '@mui/material';
import { MAX_VARIABLE_DESCRIPTION_LENGTH } from '../../defaults';
import { Delete, KeyboardArrowDown } from '@mui/icons-material';
import { EditorError, useEditor } from '../../editor';
import { scrollToItem } from '../../utils/ScrollUtils';
import { FormattedMessage } from 'react-intl';
import { matchItemByKeyword } from '../../utils/SearchUtils';
import CodeMirror from '../code/CodeMirror';
import { validateId } from '../../utils/ValidateUtils';
import { CodeEditorWithClear } from '../code/CodeEditorWithClear';
import { ContextVariable, ContextVariableType, DialobItem, Variable } from '../../types';
import { SortableHandleProps } from '../useSortableRow';
import { useComposer } from '../../dialob';
import { useSave } from '../../dialogs/contexts/saving/useSave';
import { TextEditorWithClear } from '../editors/TextEditorWithClear';

const VARIABLE_TYPES: ContextVariableType[] = [
  'text',
  'boolean',
  'number',
  'decimal',
  'date',
  'time'
]

export interface VariableProps {
  index: number;
  item: Variable | ContextVariable;
  onClose: () => void;
}

export interface SortableVariableRowProps extends VariableProps {
  onInsertBelow: (index: number) => void;
  setNodeRef?: (node: HTMLElement | null) => void;
  style?: React.CSSProperties;
  handleProps?: SortableHandleProps;
}

export const DeleteButton: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { deleteVariable } = useSave();
  return (
    <IconButton sx={{ p: 0.5 }} onClick={() => deleteVariable(variable.name)}><Delete color='error' /></IconButton>
  );
}

export const PublishedSwitch: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { updateVariablePublishing } = useSave();
  return (
    <Switch checked={variable.published ?? false} onChange={(e) => updateVariablePublishing(variable.name, e.target.checked)} />
  );
}

export const NameField: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { form } = useComposer();
  const { updateVariableName, savingState } = useSave();
  const originalNameRef = React.useRef(variable.name);
  const [name, setName] = React.useState<string>(variable.name);
  const [idError, setIdError] = React.useState(false);

  // After a save the pending renames are cleared. At that point variable.name is the
  // newly-committed name, so originalNameRef must be updated to it. Without this,
  // a second rename in the same session would dispatch with a stale "from" value that
  // no longer exists in the form, causing the backend rename to fail.
  React.useEffect(() => {
    const hasPendingRename = savingState.pendingVariableRenames?.some(r => r.to === variable.name);
    if (!hasPendingRename && originalNameRef.current !== variable.name) {
      originalNameRef.current = variable.name;
      setName(variable.name);
      setIdError(false);
    }
  }, [savingState.pendingVariableRenames, variable.name]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newName = e.target.value;
    setName(newName);

    const otherSavingVars = savingState.variables?.filter(v => v.name !== variable.name);
    const isValid = newName === originalNameRef.current || validateId(newName, form.data, otherSavingVars);

    if (isValid) {
      setIdError(false);
      if (newName !== variable.name) {
        updateVariableName(variable.name, originalNameRef.current, newName);
      }
    } else {
      setIdError(true);
    }
  };

  return (
    <TextField
      value={name}
      onChange={handleChange}
      variant='standard'
      fullWidth
      error={idError}
      helperText={idError ? <FormattedMessage id='dialogs.change.id.tip' /> : undefined}
      InputProps={{ disableUnderline: true }}
      autoFocus
    />
  );
}

export const DescriptionField: React.FC<{ variable: Variable | ContextVariable }> = ({ variable }) => {
  const { updateVariableDescription } = useSave();
  const [description, setDescription] = React.useState<string | undefined>(variable.description);

  React.useEffect(() => {
    setDescription(variable.description);
  }, [variable.name, variable.description]);

  const handleChange = (value: string) => {
    setDescription(value);
    updateVariableDescription(variable.name, value);
  };

  const handleClear = () => {
    setDescription(undefined);
    updateVariableDescription(variable.name, undefined);
  };

  return (
    <TextEditorWithClear
      value={description}
      onChange={handleChange}
      variant='standard'
      InputProps={{ 
        disableUnderline: true,
      }}
      onClear={handleClear}
      inputProps={{ maxLength: MAX_VARIABLE_DESCRIPTION_LENGTH }}
      fullWidth
      autoFocus
      multiline
    />
  );
}

export const ContextTypeMenu: React.FC<{ variable: ContextVariable }> = ({ variable }) => {
  const { updateContextVariable } = useSave();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(e.currentTarget);
    e.stopPropagation();
  };

  const handleClose = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(null);
    e.stopPropagation();
  };

  const handleConvertType = (e: React.MouseEvent<HTMLElement>, type: ContextVariableType) => {
    handleClose(e);
    updateContextVariable(variable.name, type, undefined);
  }

  return (
    <>
      <Button onClick={handleClick} component='span' endIcon={<KeyboardArrowDown />} variant='text' sx={{ p: 0 }}>
        <Typography variant='subtitle2'>
          {variable.contextType}
        </Typography>
      </Button>
      <Menu open={open} onClose={handleClose} anchorEl={anchorEl}>
        {VARIABLE_TYPES.length > 0 && VARIABLE_TYPES.filter(type => type !== variable.contextType)
          .map((type, index) => (
            <MenuItem key={index} onClick={(e) => handleConvertType(e, type)}>
              <Typography textTransform='capitalize'>{type}</Typography>
            </MenuItem>
          ))}
      </Menu>
    </>
  );
}

export const DefaultValueField: React.FC<{ variable: ContextVariable }> = ({ variable }) => {
  const { updateContextVariable } = useSave();
  const [defaultValue, setDefaultValue] = React.useState<string | undefined>(variable.defaultValue);

  React.useEffect(() => {
    setDefaultValue(variable.defaultValue);
  }, [variable.name, variable.defaultValue]);

  const handleChange = (value: string) => {
    setDefaultValue(value);
    updateContextVariable(variable.name, undefined, value);
  };

  const handleClear = () => {
    setDefaultValue(undefined);
    updateContextVariable(variable.name, undefined, undefined);
  }

  return (
    <TextEditorWithClear
      value={defaultValue}
      onChange={handleChange}
      variant='standard'
      InputProps={{ 
        disableUnderline: true, 
      }}
      onClear={handleClear}
      fullWidth
      autoFocus
    />
  );
}

export const ExpressionField: React.FC<{ variable: Variable, errors?: EditorError[] }> = ({ variable, errors }) => {
  const { updateExpressionVariable } = useSave();
  const [expression, setExpression] = React.useState<string | undefined>(variable.expression);

  React.useEffect(() => {
    setExpression(variable.expression);
  }, [variable.name, variable.expression]);

  const handleChange = (value: string) => {
    setExpression(value);
    updateExpressionVariable(variable.name, value);
  };

  const handleClear = () => {
    setExpression(undefined);
    updateExpressionVariable(variable.name, undefined);
  };

  return (
    <Box sx={{ p: 1 }}>
      <CodeEditorWithClear value={expression} onClear={handleClear}>
        <CodeMirror value={expression ?? ''} onChange={handleChange} errors={errors} />
      </CodeEditorWithClear>
    </Box>
  );
}

export const UsersField: React.FC<{ variable: ContextVariable | Variable, onClose: () => void }> = ({ variable, onClose }) => {
  const { form } = useComposer();
  const { editor, setHighlightedItem, setActivePage } = useEditor();
  const users = Object.values(form.data).filter(item => matchItemByKeyword(item, form.metadata.languages, variable.name));
  const [anchorEl, setAnchorEl] = React.useState<HTMLElement | null>(null);

  const handleScroll = (item: DialobItem) => {
    onClose();
    setAnchorEl(null);
    setHighlightedItem(item);
    scrollToItem(item.id, Object.values(form.data), editor.activePage, setActivePage);
  }

  return (
    <>
      <Tooltip title={<FormattedMessage id='dialogs.variables.users.tooltip' />}>
        <Button variant='text' onClick={(e) => setAnchorEl(e.currentTarget)}>
          <Typography fontWeight='bold' color='primary.main'>
            {users.length}
          </Typography>
        </Button>
      </Tooltip>
      <Popover open={Boolean(anchorEl)} anchorEl={anchorEl} onClose={() => setAnchorEl(null)} anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'left',
      }}>
        <List>
          {users.map(i => (
            <ListItemButton key={i.id}
              sx={{ justifyContent: 'flex-start', color: 'text.primary' }}
              onClick={() => handleScroll(i)}
            >
              {i.id}
            </ListItemButton>
          ))}
        </List>
      </Popover>
    </>
  );
}
