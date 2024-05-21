import React from "react";
import { DialobItem, DialobItemTemplate, DialobItemType, DialobItems, useComposer } from "../dialob";
import { Box, Button, Divider, IconButton, Menu, MenuItem, Table, Tooltip, Typography, styled } from "@mui/material";
import {
  Close, ContentCopy, Description, KeyboardArrowDown, KeyboardArrowRight,
  Menu as MenuIcon, Note, Rule, Tune, Visibility, Gavel, Place, Public, EditNote
} from "@mui/icons-material";
import { DEFAULT_ITEMTYPE_CONFIG, ItemTypeConfig } from "../defaults";
import { FormattedMessage, useIntl } from "react-intl";
import { OptionsTabType, useEditor } from "../editor";
import * as Defaults from "../defaults";


const MAX_LABEL_LENGTH_WITH_INDICATORS = 45;
const MAX_LABEL_LENGTH_WITHOUT_INDICATORS = 65;
const MAX_RULE_LENGTH = 80;

const ItemHeaderButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
  justifyContent: 'flex-start',
  textTransform: 'none',
  width: '100%',
}));

const FullWidthButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
  justifyContent: 'space-between',
  textTransform: 'none',
  width: '100%',
}));


export const StyledTable = styled(Table, {
  shouldForwardProp: (prop) => prop !== 'errorBorderColor',
})<{ errorBorderColor?: string }>(({ errorBorderColor }) => (
  errorBorderColor && {
    border: 1.5,
    borderStyle: 'solid',
    borderColor: errorBorderColor,
  }
));


const findItemTypeConfig = (itemTypes: ItemTypeConfig, type: DialobItemType) => {
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    const resultConfig = c.items.find(v => v.config.view === type);
    if (resultConfig) {
      return resultConfig;
    }
  }
  for (const idx in itemTypes.categories) {
    const c = itemTypes.categories[idx];
    const resultConfig = c.items.find(v => v.config.type === type);
    if (resultConfig) {
      return resultConfig;
    }
  }
  return null;
}

const getItemConversions = (item: DialobItem): { text: string, value: DialobItemTemplate }[] => {
  const thisItemType = findItemTypeConfig(DEFAULT_ITEMTYPE_CONFIG, item.type as DialobItemType);
  const options: { text: string, value: DialobItemTemplate }[] = [];

  if (thisItemType && thisItemType.convertible) {
    thisItemType.convertible.forEach(t => {
      const toItemType = findItemTypeConfig(DEFAULT_ITEMTYPE_CONFIG, t);
      if (toItemType) {
        options.push({
          text: toItemType.title,
          value: toItemType.config,
        });
      }
    });
  }
  return options;
}

const resolveTypeName = (type: string): string => {
  const items = Defaults.DEFAULT_ITEMTYPE_CONFIG.categories.flatMap(c => c.items);
  const item = items.find(i => i.config.view === type || i.config.type === type);
  if (item) {
    return item.title;
  }
  return type;
}

const isPage = (items: DialobItems, item: DialobItem): boolean => {
  return Object.values(items).find(i => i.type === 'questionnaire' && i.items && i.items.includes(item.id)) !== undefined;
}

export const IdField: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { setActiveItem, setItemOptionsActiveTab } = useEditor();

  const handleClick = () => {
    setActiveItem(item);
    setItemOptionsActiveTab('id');
  }

  return (
    <ItemHeaderButton variant='text' color='inherit' onClick={handleClick}>
      <Typography>{item.id}</Typography>
    </ItemHeaderButton>
  );
}

export const Label: React.FC<{ item: DialobItem }> = ({ item }) => {
  const intl = useIntl();
  const { form } = useComposer();
  const { editor } = useEditor();
  const [label, setLabel] = React.useState<string>('');
  const hasIndicators = item.description || item.valueSetId || item.validations || item.required || item.defaultValue;
  const maxLabelLength = hasIndicators ? MAX_LABEL_LENGTH_WITH_INDICATORS : MAX_LABEL_LENGTH_WITHOUT_INDICATORS;
  const placeholderId = isPage(form.data, item) ? 'page.label' : Defaults.DEFAULT_ITEM_CONFIG.items.find(i => i.matcher(item))?.props.placeholder;
  const placeholder = intl.formatMessage({ id: placeholderId });

  React.useEffect(() => {
    const localizedLabel = item && item.label && item.label[editor.activeFormLanguage];
    const formattedLabel = localizedLabel && localizedLabel.length > maxLabelLength ?
      localizedLabel.substring(0, maxLabelLength) + '...' :
      localizedLabel;
    setLabel(formattedLabel || '');
  }, [item, editor.activeFormLanguage, maxLabelLength]);

  return (
    <Typography textTransform='none' color={label ? 'text.primary' : 'text.hint'}>
      {label ? label : placeholder}
    </Typography>
  );
}

export const LabelField: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { setActiveItem, setItemOptionsActiveTab } = useEditor();

  const handleClick = (): void => {
    setActiveItem(item);
    setItemOptionsActiveTab('label');
  }

  return (
    <ItemHeaderButton variant='text' color='inherit' onClick={handleClick}>
      <Label item={item} />
    </ItemHeaderButton>
  );
}

export const Indicators: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { form } = useComposer();
  const { setActiveItem, setItemOptionsActiveTab } = useEditor();
  const globalValueSets = form.metadata.composer?.globalValueSets;
  const isGlobalValueSet = globalValueSets && globalValueSets.find(v => v.valueSetId === item.valueSetId);

  const handleClick = (e: React.MouseEvent<HTMLElement>, dialogType?: OptionsTabType): void => {
    e.stopPropagation();
    setActiveItem(item);
    setItemOptionsActiveTab(dialogType as OptionsTabType);
  }

  return (
    <Box sx={{ display: 'flex', justifyContent: 'flex-end', flexDirection: 'row' }}>
      {item.description &&
        <Tooltip placement='top' title={<FormattedMessage id='tooltips.description' />}>
          <IconButton onClick={(e) => handleClick(e, 'description')}><Description fontSize='small' sx={{ color: 'info.main' }} /></IconButton>
        </Tooltip>}
      {item.valueSetId &&
        <Tooltip placement='top' title={<FormattedMessage id={`tooltips.${isGlobalValueSet ? 'global' : 'local'}`} />}>
          <IconButton onClick={(e) => handleClick(e, 'choices')}>
            {isGlobalValueSet ? <Public fontSize='small' sx={{ color: 'success.light' }} /> : <Place fontSize='small' sx={{ color: 'success.light' }} />}
          </IconButton>
        </Tooltip>}
      {item.validations && item.validations.length > 0 &&
        <Tooltip placement='top' title={<FormattedMessage id='tooltips.validations' />}>
          <IconButton onClick={(e) => handleClick(e, 'validations')}><Rule fontSize='small' sx={{ color: 'warning.light' }} /></IconButton>
        </Tooltip>}
      {item.required &&
        <Tooltip placement='top' title={<FormattedMessage id='tooltips.requirement' />}>
          <IconButton onClick={(e) => handleClick(e, 'rules')}><Gavel fontSize='small' sx={{ color: 'error.light' }} /></IconButton>
        </Tooltip>}
      {item.defaultValue &&
        <Tooltip placement='top' title={<FormattedMessage id='tooltips.default' />}>
          < IconButton onClick={(e) => handleClick(e, 'defaults')}><EditNote fontSize='small' sx={{ color: 'info.light' }} /></IconButton>
        </Tooltip>}
      {item.type === 'note' && item.activeWhen &&
        <Tooltip placement='top' title={<FormattedMessage id='tooltips.visibility' />}>
          < IconButton onClick={(e) => handleClick(e, 'rules')}><Visibility fontSize='small' sx={{ color: 'primary.light' }} /></IconButton>
        </Tooltip>}
    </Box >
  );
}

export const ConversionMenu: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { changeItemType } = useComposer();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const conversions = getItemConversions(item);
  const [typeName, setTypeName] = React.useState<string>(resolveTypeName(item.view || item.type))

  const handleClick = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(e.currentTarget);
    e.stopPropagation();
  };

  const handleClose = (e: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(null);
    e.stopPropagation();
  };

  const handleConvert = (e: React.MouseEvent<HTMLElement>, config: DialobItemTemplate) => {
    handleClose(e);
    changeItemType(item.id, config);
    setTypeName(resolveTypeName(config.type));
  }

  React.useEffect(() => {
    setTypeName(resolveTypeName(item.view || item.type));
  }, [item]);

  return (
    <>
      <Button onClick={handleClick} component='span' sx={{ ml: 1 }} variant='text'
        endIcon={<KeyboardArrowDown />}
        disabled={conversions.length === 0}
      >
        <Typography variant='subtitle2'>
          {typeName}
        </Typography>
      </Button>
      <Menu open={open} onClose={handleClose} anchorEl={anchorEl} disableScrollLock={true}>
        <MenuItem key='hint' onClick={handleClose} disabled>
          <Typography><FormattedMessage id='menus.conversions.hint' /></Typography>
        </MenuItem>
        {conversions.length > 0 && conversions.map((c, index) => (
          <MenuItem key={index} onClick={(e) => handleConvert(e, c.value)}>
            <Typography>{resolveTypeName(c.text)}</Typography>
          </MenuItem>
        ))}
      </Menu>
    </>
  );
}

export const OptionsMenu: React.FC<{ item: DialobItem, isPage?: boolean, light?: boolean }> = ({ item, isPage, light }) => {
  const { form, addItem } = useComposer();
  const { setConfirmationDialogType, setActiveItem, setItemOptionsActiveTab } = useEditor();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const [categoriesAnchorEl, setCategoriesAnchorEl] = React.useState<null | HTMLElement>(null);
  const [itemsAnchorEl, setItemsAnchorEl] = React.useState<null | HTMLElement>(null);
  const [chosenCategory, setChosenCategory] = React.useState<string | null>('');
  const open = Boolean(anchorEl);
  const categoriesOpen = Boolean(categoriesAnchorEl);
  const itemCategories = Defaults.DEFAULT_ITEMTYPE_CONFIG.categories;

  const handleClick = (e: React.MouseEvent<HTMLElement>, level: number, category?: string) => {
    e.stopPropagation();
    if (level === 2) {
      setCategoriesAnchorEl(e.currentTarget);
    } else if (level === 3 && category) {
      setItemsAnchorEl(e.currentTarget);
      setChosenCategory(category);
    } else {
      setAnchorEl(e.currentTarget);
    }
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleClose = (e: any, level: number) => {
    e.stopPropagation();
    if (level === 2) {
      setCategoriesAnchorEl(null);
    } else if (level === 3) {
      setItemsAnchorEl(null);
      setChosenCategory(null);
    } else {
      setAnchorEl(null);
    }
  };

  const handleCloseAll = () => {
    setAnchorEl(null);
    setCategoriesAnchorEl(null);
    setItemsAnchorEl(null);
    setChosenCategory(null);
  }

  const handleOptions = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    handleClose(e, 1);
    setActiveItem(item);
    setItemOptionsActiveTab('label');
  }

  const handleDelete = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    handleClose(e, 1);
    setActiveItem(item);
    setConfirmationDialogType('delete');
  }

  const handleDuplicate = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    handleClose(e, 1);
    setActiveItem(item);
    setConfirmationDialogType('duplicate');
  }

  const handleCreate = (e: React.MouseEvent<HTMLElement>, itemTemplate: DialobItemTemplate) => {
    e.stopPropagation();
    const items = Object.values(form.data);
    const parentItemId = items.find(i => i.items && i.items.includes(item.id))!.id;
    addItem(itemTemplate, parentItemId, item.id);
    handleCloseAll();
  }

  return (
    <>
      <IconButton onClick={(e) => handleClick(e, 1)} onMouseDown={(e) => e.stopPropagation()}>
        <MenuIcon sx={{ color: light ? 'white' : 'inherit' }} />
      </IconButton>
      <Menu open={open} onClose={(e) => handleClose(e, 1)} anchorEl={anchorEl} disableScrollLock={true}>
        <MenuItem onClick={(e) => handleOptions(e)}>
          <Tune sx={{ mr: 1 }} fontSize='small' />
          <FormattedMessage id='menus.options' />
        </MenuItem>
        <MenuItem onClick={(e) => handleDelete(e)}>
          <Close sx={{ mr: 1 }} fontSize='small' />
          <FormattedMessage id='menus.delete' />
        </MenuItem>
        <MenuItem onClick={(e) => handleDuplicate(e)}>
          <ContentCopy sx={{ mr: 1 }} fontSize='small' />
          <FormattedMessage id='menus.duplicate' />
        </MenuItem>
        {!isPage && <Divider />}
        {!isPage && <MenuItem onClick={(e) => handleClick(e, 2)}>
          <FormattedMessage id='menus.insert.below' />
          <KeyboardArrowRight sx={{ ml: 1 }} fontSize='small' />
        </MenuItem>}
        <Menu open={categoriesOpen} onClose={(e) => handleClose(e, 2)} anchorEl={categoriesAnchorEl}
          disableScrollLock={true} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
          {itemCategories.map((c, index) => (
            <MenuItem key={index} onClick={(e) => handleClick(e, 3, c.title)}>
              <Typography>{c.title}</Typography>
              <KeyboardArrowRight sx={{ ml: 1 }} fontSize='small' />
              <Menu open={c.title === chosenCategory} onClose={(e) => handleClose(e, 3)} anchorEl={itemsAnchorEl}
                disableScrollLock={true} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
                {c.items.map((i, index) => (
                  <MenuItem key={index} onClick={(e) => handleCreate(e, i.config)}>
                    <Typography>{i.title}</Typography>
                  </MenuItem>
                ))}
              </Menu>
            </MenuItem>
          ))}
        </Menu>
      </Menu>
    </>
  );
}

export const AddItemMenu: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { addItem } = useComposer();
  const [categoriesAnchorEl, setCategoriesAnchorEl] = React.useState<null | HTMLElement>(null);
  const [itemsAnchorEl, setItemsAnchorEl] = React.useState<null | HTMLElement>(null);
  const [chosenCategory, setChosenCategory] = React.useState<string | null>('');
  const categoriesOpen = Boolean(categoriesAnchorEl);
  const itemCategories = Defaults.DEFAULT_ITEMTYPE_CONFIG.categories;

  const handleClick = (e: React.MouseEvent<HTMLElement>, category?: string) => {
    e.stopPropagation();
    if (category) {
      setItemsAnchorEl(e.currentTarget);
      setChosenCategory(category);
    } else {
      setCategoriesAnchorEl(e.currentTarget);
    }
  };

  const handleClose = (e: React.MouseEvent<HTMLElement>) => {
    e.stopPropagation();
    setCategoriesAnchorEl(null);
    setItemsAnchorEl(null);
    setChosenCategory(null);
  };

  const handleCreate = (e: React.MouseEvent<HTMLElement>, itemTemplate: DialobItemTemplate) => {
    handleClose(e);
    addItem(itemTemplate, item.id);
  }

  return (
    <>
      <Button onClick={handleClick} variant='contained' color='inherit' endIcon={<KeyboardArrowDown />}>
        <Typography><FormattedMessage id='menus.add' /></Typography>
      </Button>
      <Menu open={categoriesOpen} onClose={handleClose} anchorEl={categoriesAnchorEl}
        disableScrollLock={true} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
        {itemCategories.map((c, index) => (
          <MenuItem key={index} onClick={(e) => handleClick(e, c.title)}>
            <Typography>{c.title}</Typography>
            <KeyboardArrowRight sx={{ ml: 1 }} fontSize='small' />
            <Menu open={c.title === chosenCategory} onClose={handleClose} anchorEl={itemsAnchorEl}
              disableScrollLock={true} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
              {c.items.map((i, index) => (
                <MenuItem key={index} onClick={(e) => handleCreate(e, i.config)}>
                  <Typography>{i.title}</Typography>
                </MenuItem>
              ))}
            </Menu>
          </MenuItem>
        ))}
      </Menu>
    </>
  )
}

export const VisibilityField: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { setActiveItem, setItemOptionsActiveTab } = useEditor();

  const handleClick = (): void => {
    setActiveItem(item);
    setItemOptionsActiveTab('rules');
  }

  return (
    <FullWidthButton
      variant='text'
      color='inherit'
      endIcon={<Visibility color={item.activeWhen ? 'primary' : 'disabled'} sx={{ mr: 1 }} />}
      onClick={handleClick}
    >
      {item.activeWhen ?
        <Typography fontFamily='monospace' textTransform='none'>
          {/* eslint-disable-next-line formatjs/no-literal-string-in-jsx */}
          {item.activeWhen.length > MAX_RULE_LENGTH ? item.activeWhen.substring(0, MAX_RULE_LENGTH) + '...' : item.activeWhen}
        </Typography> :
        <Typography color='text.hint'>
          <FormattedMessage id='buttons.visibility' />
        </Typography>
      }
    </FullWidthButton>
  );
}

export const NoteField: React.FC<{ item: DialobItem }> = ({ item }) => {
  const { setActiveItem, setItemOptionsActiveTab } = useEditor();

  const handleClick = () => {
    setActiveItem(item);
    setItemOptionsActiveTab('label');
  }

  return (
    <FullWidthButton
      variant='text'
      color='inherit'
      endIcon={<Note sx={{ mr: 1, color: 'text.main' }} />}
      onClick={handleClick}
    >
      <Label item={item} />
    </FullWidthButton>
  );
}
