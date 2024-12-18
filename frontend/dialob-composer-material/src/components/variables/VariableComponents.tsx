import React from 'react';
import { Button, IconButton, List, ListItemButton, Menu, MenuItem, Popover, Switch, TextField, Tooltip, Typography } from '@mui/material';
import { MAX_VARIABLE_DESCRIPTION_LENGTH } from '../../defaults';
import { ContextVariable, ContextVariableType, DialobItem, Variable, useComposer } from '../../dialob';
import { Check, Close, Delete, KeyboardArrowDown } from '@mui/icons-material';
import { EditorError, useEditor } from '../../editor';
import { scrollToItem } from '../../utils/ScrollUtils';
import { FormattedMessage } from 'react-intl';
import { TreeItem } from '@atlaskit/tree';
import { TreeDraggableProvided } from '@atlaskit/tree/dist/types/components/TreeItem/TreeItem-types';
import { matchItemByKeyword } from '../../utils/SearchUtils';
import CodeMirror from '../code/CodeMirror';
import { validateId } from '../../utils/ValidateUtils';
import { useBackend } from '../../backend/useBackend';
import { ChangeIdResult } from '../../backend/types';

const VARIABLE_TYPES: ContextVariableType[] = [
  'text',
  'boolean',
  'number',
  'decimal',
  'date',
  'time'
]

export interface VariableProps {
  item: TreeItem,
  provided: TreeDraggableProvided,
  onClose: () => void
}

export const DeleteButton: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { deleteVariable } = useComposer();
  return (
    <IconButton sx={{ p: 0.5 }} onClick={() => deleteVariable(variable.name)}><Delete color='error' /></IconButton>
  );
}

export const PublishedSwitch: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { updateVariablePublishing } = useComposer();
  return (
    <Switch checked={variable.published} onChange={(e) => updateVariablePublishing(variable.name, e.target.checked)} />
  );
}

export const NameField: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { changeItemId } = useBackend();
  const { form, setForm, setRevision } = useComposer();
  const { setErrors } = useEditor();
  const [editMode, setEditMode] = React.useState(false);
  const [idError, setIdError] = React.useState(false);

  const handleChangeName = () => {
    if (name !== variable.name) {
      if (validateId(name, form.data, form.variables)) {
        changeItemId(form, variable.name, name).then((response) => {
          const result = response.result as ChangeIdResult;
          if (response.success) {
            setForm(result.form);
            setErrors(result.errors);
            setIdError(false);
            setRevision(result.rev);
          } else if (response.apiError) {
            setErrors([{ level: 'FATAL', message: response.apiError.message }]);
          }
          setEditMode(false);
        });
      } else {
        setIdError(true);
      }
    }
  }

  const handleCloseChange = () => {
    setEditMode(false);
    setIdError(false);
    setName(variable.name);
  }

  const [name, setName] = React.useState<string>(variable.name);
  return (
    <TextField value={name} onChange={(e) => setName(e.target.value)} variant='standard' fullWidth error={idError}
      onFocus={() => setEditMode(true)} helperText={editMode && <FormattedMessage id='dialogs.change.id.tip' />} InputProps={{
        disableUnderline: true,
        endAdornment: (
          editMode && <>
            <IconButton onClick={handleChangeName}><Check color='success' /></IconButton>
            <IconButton onClick={handleCloseChange}><Close color='error' /></IconButton>
          </>
        )
      }} />
  );
}

export const DescriptionField: React.FC = () => {
  const [description, setDescription] = React.useState<string | undefined>(); // TODO: add description to context
  return (
    <TextField
      value={description || ''}
      onChange={(e) => setDescription(e.target.value)}
      variant='standard'
      InputProps={{ disableUnderline: true }}
      inputProps={{ maxLength: MAX_VARIABLE_DESCRIPTION_LENGTH }}
      fullWidth
    />
  );
}

export const ContextTypeMenu: React.FC<{ variable: ContextVariable }> = ({ variable }) => {
  const { updateContextVariable } = useComposer();
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
    updateContextVariable(variable.name, type, variable.defaultValue);
  }

  return (
    <>
      <Button onClick={handleClick} component='span' endIcon={<KeyboardArrowDown />} variant='text'>
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
  const { updateContextVariable } = useComposer();
  const [defaultValue, setDefaultValue] = React.useState<string | undefined>(variable.defaultValue);

  React.useEffect(() => {
    const id = setTimeout(() => {
      updateContextVariable(variable.name, variable.contextType, defaultValue);
    }, 300);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [defaultValue]);

  return (
    <TextField
      value={defaultValue || ''}
      onChange={(e) => setDefaultValue(e.target.value)}
      variant='standard'
      InputProps={{ disableUnderline: true, endAdornment: <IconButton onClick={() => setDefaultValue('')}><Close /></IconButton> }}
      fullWidth
    />
  );
}

export const ExpressionField: React.FC<{ variable: Variable, errors?: EditorError[] }> = ({ variable, errors }) => {
  const { updateExpressionVariable } = useComposer();
  const [expression, setExpression] = React.useState<string>(variable.expression);

  React.useEffect(() => {
    const id = setTimeout(() => {
      updateExpressionVariable(variable.name, expression);
    }, 300);
    return () => clearTimeout(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [expression]);

  return (
    <CodeMirror value={expression} onChange={(e) => setExpression(e)} errors={errors} />
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
