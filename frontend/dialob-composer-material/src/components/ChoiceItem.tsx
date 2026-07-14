import React from "react";
import { Box, IconButton, TableCell, TableRow, TextField, Typography, alpha, useTheme, Tooltip } from '@mui/material';
import { Close, Edit, PostAdd } from "@mui/icons-material";
import { FormattedMessage } from "react-intl";
import { useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";
import { CodeEditorWithClear } from "./code/CodeEditorWithClear";
import { TextEditorWithClear } from "./editors/TextEditorWithClear";
import { LocalizedString, ValueSetEntry } from "../types";
import { useSave } from "../dialogs/contexts/saving/useSave";
import { useBackend } from "../backend/useBackend";
import { TranslationEntry, TranslationResponse } from "../backend/types";
import AITranslationIndicator from "./translations/AITranslationIndicator";
import TranslateButton from "./translations/TranslateButton";
import { useAITranslation } from "./translations";
import { buildValueSetEntryId } from "../utils/TranslationUtils";
import { DragHandle, DragHandleProps } from "./DragHandle";
import { SortableHandleProps } from "./useSortableRow";


export interface ChoiceItemProps {
  entry: ValueSetEntry,
  index: number,
  valueSetId?: string,
  isGlobal?: boolean,
  sortableId: string,
  style?: React.CSSProperties,
  handleProps?: SortableHandleProps,
  onRuleEdit: (index: number, rule: string) => void,
  onTextEdit: (index: number, label: LocalizedString) => void,
  onDelete: (index: number) => void,
  onUpdateId: (index: number, id: string) => void,
  onInsertBelow: (index: number) => void,
  setNodeRef?: (node: HTMLElement | null) => void,
}

type ExpandedField = 'id' | 'rule' | string | null;

const truncate = (value: string | undefined, maxLen = 28) => {
  if (!value) return '';
  return value.length > maxLen ? value.substring(0, maxLen) + '…' : value;
};

const ChoiceItem: React.FC<ChoiceItemProps> = (props) => {
  const {
    entry, index, valueSetId, isGlobal, onRuleEdit, onTextEdit, onDelete, onUpdateId, onInsertBelow,
    setNodeRef, style, handleProps,
  } = props;
  const { form } = useComposer();
  const { editor } = useEditor();
  const { applyTranslations } = useSave();
  const { translateEntries, config } = useBackend();
  const theme = useTheme();
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors?.find(e => e.itemId === valueSetId && e.index == index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const [expandedField, setExpandedField] = React.useState<ExpandedField>(null);
  const [open, setOpen] = React.useState(false);
  const [localId, setLocalId] = React.useState(entry.id);
  const [localizedString, setLocalizedString] = React.useState<LocalizedString>(entry.label || {});
  const [localRule, setLocalRule] = React.useState(entry.when ?? '');
  const [translatingLanguage, setTranslatingLanguage] = React.useState<string | null>(null);

  const getEntryId = (): string => {
    return buildValueSetEntryId(valueSetId!, index, entry.id);
  };

  const { isAITranslated, getMetadata: getAITranslationMetadata, removeFlag } = useAITranslation(getEntryId());

  React.useEffect(() => {
    setLocalId(entry.id);
  }, [entry.id]);

  React.useEffect(() => {
    setLocalizedString(entry.label || {});
  }, [entry.label]);

  React.useEffect(() => {
    setLocalRule(entry.when ?? '');
  }, [entry.when]);

  const toggleField = (field: ExpandedField) => {
    setExpandedField(prev => prev === field ? null : field);
  };

  const handleUpdate = (value: string, language: string) => {
    const updated = { ...localizedString, [language]: value };
    setLocalizedString(updated);
    onTextEdit(index, updated);
    if (isAITranslated(language)) {
      removeFlag(language);
    }
  };

  const handleClearLanguage = (language: string) => {
    const updated = { ...localizedString };
    delete updated[language];
    setLocalizedString(updated);
    onTextEdit(index, updated);
    if (isAITranslated(language)) {
      removeFlag(language);
    }
  };

  const handleChangeId = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newId = e.target.value;
    setLocalId(newId);
    onUpdateId(index, newId);
  };

  const handleUpdateRule = (value: string) => {
    setLocalRule(value);
    onRuleEdit(index, value);
  };

  const handleTranslate = async (targetLanguage: string) => {
    const sourceLanguage = editor.activeFormLanguage;
    const sourceText = localizedString[sourceLanguage];

    if (!sourceText) {
      return;
    }

    setTranslatingLanguage(targetLanguage);

    try {
      const entryId = getEntryId();
      const translationEntry: TranslationEntry = {
        id: entryId,
        text: sourceText
      };

      const response = await translateEntries({
        sourceLanguage,
        targetLanguage,
        entries: [translationEntry]
      });

      if (response.success && response.result) {
        const translationResponse = response.result as TranslationResponse;
        if (translationResponse.translations && translationResponse.translations.length > 0) {
          applyTranslations(translationResponse.translations, sourceLanguage, targetLanguage);
          const translation = translationResponse.translations[0];
          setLocalizedString(prev => ({ ...prev, [targetLanguage]: translation.translatedText }));
        }
      } else if (response.apiError) {
        console.error('Translation failed:', response.apiError.message);
      }
    } catch (error) {
      console.error('Translation error:', error);
    } finally {
      setTranslatingLanguage(null);
    }
  };

  const editBtnSx = (active: boolean, color: string) => ({
    p: 0.5, ml: 0.5,
    ...(active && { border: `1px solid ${color}` }),
  });

  const expandedBg =
    expandedField === 'id' ? alpha(theme.palette.warning.main, 0.06) :
      expandedField === 'rule' ? alpha(theme.palette.primary.main, 0.06) :
        undefined;

  const colSpan = isGlobal ? 2 + languageNo : 3 + languageNo;

  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(index)} onClose={() => setOpen(false)} />
      <TableRow
        ref={setNodeRef}
        style={style}
        data-choice-item-row
        sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}
      >
        <TableCell align='center' width='15%'>
          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <IconButton sx={{ p: 0.5 }} onClick={() => setOpen(true)}><Close color='error' /></IconButton>
            <Tooltip title={<FormattedMessage id='menus.insert.below' />}>
              <IconButton sx={{ p: 0.5 }} onClick={() => onInsertBelow(index)}>
                <PostAdd color='success' />
              </IconButton>
            </Tooltip>
            <DragHandle {...handleProps as DragHandleProps} />
          </Box>
        </TableCell>

        <TableCell width={isGlobal ? '20%' : '15%'} sx={{ p: 0.5 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant='body2' noWrap sx={{ flex: 1 }}>{truncate(localId)}</Typography>
            <IconButton size='small' sx={editBtnSx(expandedField === 'id', theme.palette.warning.main)} onClick={() => toggleField('id')}>
              <Edit fontSize='small' color={expandedField === 'id' ? 'warning' : 'inherit'} />
            </IconButton>
          </Box>
        </TableCell>

        {!isGlobal && (
          <TableCell width='15%' sx={{ p: 0.5 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant='body2' noWrap sx={{ flex: 1, color: entry.when ? 'text.primary' : 'text.disabled' }}>
                {truncate(entry.when)}
              </Typography>
              <IconButton size='small' sx={editBtnSx(expandedField === 'rule', theme.palette.primary.main)} onClick={() => toggleField('rule')}>
                <Edit fontSize='small' color={expandedField === 'rule' ? 'primary' : 'inherit'} />
              </IconButton>
            </Box>
          </TableCell>
        )}

        {formLanguages?.map(lang => {
          const hasValue = localizedString && localizedString[lang] !== undefined;
          const sourceLanguage = editor.activeFormLanguage;
          const hasSourceValue = localizedString && localizedString[sourceLanguage];
          const canTranslate = !hasValue && hasSourceValue && lang !== sourceLanguage && config.translationServiceUrl;
          const isTranslating = translatingLanguage === lang;
          const aiMetadata = isAITranslated(lang) ? getAITranslationMetadata(lang) : null;

          return (
            <TableCell key={lang} width={formLanguages ? `${(isGlobal ? 65 : 55) / formLanguages.length}%` : 0} sx={{ p: 0.5 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                <Typography variant='body2' noWrap sx={{ flex: 1 }}>{truncate(localizedString?.[lang])}</Typography>
                <IconButton size='small' sx={editBtnSx(expandedField === lang, theme.palette.action.active)} onClick={() => toggleField(lang)}>
                  <Edit fontSize='small' color='inherit' />
                </IconButton>
                {canTranslate && (
                  <TranslateButton
                    sourceLanguage={sourceLanguage}
                    targetLanguage={lang}
                    isTranslating={isTranslating}
                    onClick={() => handleTranslate(lang)}
                  />
                )}
                {aiMetadata && (
                  <AITranslationIndicator
                    metadata={aiMetadata}
                    onClick={() => removeFlag(lang)}
                  />
                )}
              </Box>
            </TableCell>
          );
        })}
      </TableRow>

      {expandedField && (
        <TableRow>
          <TableCell colSpan={colSpan} sx={{ p: 1, backgroundColor: expandedBg }}>
            {expandedField === 'id' && (
              <TextField
                value={localId}
                onChange={handleChangeId}
                variant='standard'
                fullWidth
                InputProps={{ disableUnderline: true }}
                autoFocus
              />
            )}
            {expandedField === 'rule' && (
              <CodeEditorWithClear value={localRule || undefined} onClear={() => handleUpdateRule('')}>
                <CodeMirror value={localRule} onChange={handleUpdateRule} />
              </CodeEditorWithClear>
            )}
            {formLanguages?.includes(expandedField) && (
              <TextEditorWithClear
                value={localizedString?.[expandedField]}
                onChange={(value) => handleUpdate(value, expandedField)}
                onClear={localizedString?.[expandedField] !== undefined ? () => handleClearLanguage(expandedField) : undefined}
                variant='standard'
                fullWidth
                multiline
                InputProps={{ disableUnderline: true }}
                autoFocus
                sx={{ pt: 0.5 }}
              />
            )}
          </TableCell>
        </TableRow>
      )}
    </>
  );
};

export default ChoiceItem;
