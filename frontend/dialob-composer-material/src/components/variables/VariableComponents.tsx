import React from 'react';
import { Button, IconButton, List, ListItemButton, Menu, MenuItem, Popover, Switch, TextField, Tooltip, Typography } from '@mui/material';
import { MAX_VARIABLE_DESCRIPTION_LENGTH } from '../../defaults';
import { ContextVariable, ContextVariableType, DialobItem, Variable, useComposer } from '../../dialob';
import { Close, Delete, KeyboardArrowDown } from '@mui/icons-material';
import CodeMirror from '@uiw/react-codemirror';
import { javascript } from '@codemirror/lang-javascript';
import { useEditor } from '../../editor';
import { scrollToItem } from '../../utils/ScrollUtils';
import { FormattedMessage } from 'react-intl';

const types: ContextVariableType[] = [
  'text',
  'boolean',
  'number',
  'decimal',
  'date',
  'time'
]

export const DeleteButton: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { deleteVariable } = useComposer();
  return (
    <IconButton sx={{ p: 1, m: 1 }} onClick={() => deleteVariable(variable.name)}><Delete /></IconButton>
  );
}

export const PublishedSwitch: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const { updateVariablePublishing } = useComposer();
  return (
    <Switch sx={{ m: 1 }} checked={variable.published} onChange={(e) => updateVariablePublishing(variable.name, e.target.checked)} />
  );
}

export const NameField: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  // name updates are considered an id change and will need to be connected to backend
  const [name, setName] = React.useState<string>(variable.name);
  return (
    <TextField value={name} onChange={(e) => setName(e.target.value)} variant='standard' InputProps={{ disableUnderline: true }} fullWidth />
  );
}

export const DescriptionField: React.FC<{ variable: ContextVariable | Variable }> = ({ variable }) => {
  const [description, setDescription] = React.useState<string | undefined>(); // add description to context
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
        {types.length > 0 && types.filter(type => type !== variable.contextType)
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
    }, 1000);
    return () => clearTimeout(id);
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

export const ExpressionField: React.FC<{ variable: Variable }> = ({ variable }) => {
  const { updateExpressionVariable } = useComposer();
  const [expression, setExpression] = React.useState<string>(variable.expression);

  React.useEffect(() => {
    const id = setTimeout(() => {
      updateExpressionVariable(variable.name, expression);
    }, 1000);
    return () => clearTimeout(id);
  }, [expression]);

  return (
    <CodeMirror value={expression} onChange={(e) => setExpression(e)} extensions={[javascript({ jsx: true })]} />
  );
}

export const UsersField: React.FC<{ variable: ContextVariable | Variable, onClose: () => void }> = ({ variable, onClose }) => {
  const { form } = useComposer();
  const { editor, setHighlightedItem, setActivePage } = useEditor();
  const users = Object.values(form.data).filter(item => matchByKeyword(item, form.metadata.languages, variable.name));
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
          <Typography fontWeight='bold' color='primary.main' variant='h4'>
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

// dummy implementation to check for string matches, will be replaced by a backend implementation
export const matchByKeyword = (item: DialobItem, languages?: string[], keyword?: string) => {
  if (!keyword || !languages) {
    return true;
  }
  for (const language of languages) {
    if (item.label && item.label[language] && item.label[language].toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.description && item.description[language] && item.description[language].toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.id.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.activeWhen?.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.required?.toLowerCase().includes(keyword.toLowerCase())) {
      return true;
    } else if (item.validations?.some((validation) => (validation.message && validation.message[language] &&
      validation.message[language].toLowerCase().includes(keyword.toLowerCase())) ||
      (validation.rule && validation.rule.toLowerCase().includes(keyword.toLowerCase())))) {
      return true;
    }
  }
  return false;
}

