import React from 'react';
import { Box, Button, IconButton, List, ListItemButton, Menu, MenuItem, Popover, Switch, TextField, Tooltip, Typography } from '@mui/material';
import { MAX_VARIABLE_DESCRIPTION_LENGTH } from '../../defaults';
import { Check, Close, Delete, KeyboardArrowDown } from '@mui/icons-material';
import { EditorError, useEditor } from '../../editor';
import { scrollToItem } from '../../utils/ScrollUtils';
import { FormattedMessage } from 'react-intl';
import { matchItemByKeyword } from '../../utils/SearchUtils';
import CodeMirror from '../code/CodeMirror';
import { validateId } from '../../utils/ValidateUtils';
import { useBackend } from '../../backend/useBackend';
import { ChangeIdResult } from '../../backend/types';
import { ContextVariable, ContextVariableType, DialobItem, Variable } from '../../types';
import { useComposer } from '../../dialob';
import { useSave } from '../../dialogs/contexts/saving/useSave';

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
  const { changeItemId } = useBackend();
  const { form, setForm, setRevision } = useComposer();
  const { setErrors } = useEditor();
  const { changeVariableId } = useSave();
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
            if (result.form.variables) {
              // this is necessary to keep the saving state in sync, but will discard unsaved changes
              changeVariableId(result.form.variables);
            }
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
            <Tooltip title={<FormattedMessage id='dialogs.change.id.tooltip' />}>
              <IconButton onClick={handleChangeName}><Check color='success' /></IconButton>
            </Tooltip>
            <IconButton onClick={handleCloseChange}><Close color='error' /></IconButton>
          </>
        )
      }} />
  );
}

export const DescriptionField: React.FC<{ variable: Variable | ContextVariable }> = ({ variable }) => {
  const { updateVariableDescription } = useSave();
  const [description, setDescription] = React.useState<string | undefined>(variable.description);

  const handleBlur = () => {
    if (description && description !== variable.description) {
      updateVariableDescription(variable.name, description);
    }
  }

  return (
    <TextField
      value={description || ''}
      onChange={(e) => setDescription(e.target.value)}
      onBlur={handleBlur}
      variant='standard'
      InputProps={{ disableUnderline: true }}
      inputProps={{ maxLength: MAX_VARIABLE_DESCRIPTION_LENGTH }}
      fullWidth
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
  const [defaultValue, setDefaultValue] = React.useState<string>(variable.defaultValue);

  const handleBlur = () => {
    if (defaultValue !== variable.defaultValue) {
      updateContextVariable(variable.name, undefined, defaultValue);
    }
  }

  const handleClear = () => {
    setDefaultValue('');
    updateContextVariable(variable.name, undefined, '');
  }

  return (
    <TextField
      value={defaultValue || ''}
      onChange={(e) => setDefaultValue(e.target.value)}
      onBlur={handleBlur}
      variant='standard'
      InputProps={{ disableUnderline: true, endAdornment: <IconButton onClick={handleClear}><Close /></IconButton> }}
      fullWidth
    />
  );
}

export const ExpressionField: React.FC<{ variable: Variable, errors?: EditorError[] }> = ({ variable, errors }) => {
  const { updateExpressionVariable } = useSave();
  const [expression, setExpression] = React.useState<string>(variable.expression);

  const handleBlur = () => {
    if (expression !== variable.expression) {
      updateExpressionVariable(variable.name, expression);
    }
  }

  return (
    <Box sx={{ p: 1 }}>
      <CodeMirror value={expression ?? ''} onChange={(e) => setExpression(e)} onBlur={handleBlur} errors={errors} />
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
