import React from "react";
import { Box, IconButton, Table, TableBody, TableCell, TableRow, TextField, Tooltip, Typography, alpha, useTheme } from '@mui/material';
import { ArrowDownward, ArrowUpward, Close, Visibility } from "@mui/icons-material";
import { useComposer } from "../dialob";
import ChoiceDeleteDialog from "../dialogs/ChoiceDeleteDialog";
import { FormattedMessage } from "react-intl";
import { useErrorColor } from "../utils/ErrorUtils";
import { useEditor } from "../editor";
import CodeMirror from "./code/CodeMirror";
import { LocalizedString, ValueSetEntry } from "../types";
import { useSave } from "../dialogs/contexts/saving/useSave";
import { useBackend } from "../backend/useBackend";
import { TranslationEntry, TranslationResponse } from "../backend/types";
import AITranslationIndicator from "./translations/AITranslationIndicator";
import TranslateButton from "./translations/TranslateButton";
import { useAITranslation } from "./translations";
import { buildValueSetEntryId } from "../utils/TranslationUtils";


export interface ChoiceItemProps {
  entry: ValueSetEntry,
  index: number,
  valueSetId?: string,
  isGlobal?: boolean,
  onRuleEdit: (entry: ValueSetEntry, rule: string) => void,
  onTextEdit: (entry: ValueSetEntry, label: LocalizedString) => void,
  onDelete: (entry: ValueSetEntry) => void,
  onUpdateId: (entry: ValueSetEntry, id: string) => void,
  onMove?: (entry: ValueSetEntry, direction: 'up' | 'down') => void
}

const OverflowTooltipTextField: React.FC<React.ComponentProps<typeof TextField>> = ({ value, ...props }) => {
  const inputRef = React.useRef<HTMLInputElement>(null);
  const [isOverflowing, setIsOverflowing] = React.useState(false);

  React.useEffect(() => {
    const checkOverflow = () => {
      if (inputRef.current) {
        const el = inputRef.current;
        const overflow = el.scrollWidth > (el.clientWidth + 1);
        setIsOverflowing(overflow);
      }
    };

    checkOverflow();

    const resizeObserver = new ResizeObserver(() => {
      checkOverflow();
    });

    if (inputRef.current) {
      resizeObserver.observe(inputRef.current);
    }

    return () => resizeObserver.disconnect();
  }, [value]);

  const textField = (
    <TextField
      {...props}
      inputRef={inputRef}
      value={value}
    />
  );

  return isOverflowing ? (
    <Tooltip title={value + ''} placement='top' arrow>
      {textField}
    </Tooltip>
  ) : (
    textField
  );
};

const ChoiceItem: React.FC<ChoiceItemProps> = (props) => {
  const { entry, index, valueSetId, isGlobal, onRuleEdit, onTextEdit, onDelete, onUpdateId, onMove } = props;
  const { form } = useComposer();
  const { editor } = useEditor();
  const { savingState, moveValueSetEntry, applyTranslations } = useSave();
  const { translateEntries, config } = useBackend();
  const theme = useTheme();
  const formLanguages = form.metadata.languages;
  const languageNo = formLanguages?.length || 0;
  const error = editor.errors?.find(e => e.itemId === valueSetId && e.index == index);
  const errorColor = useErrorColor(error);
  const backgroundColor = errorColor || theme.palette.background.paper;
  const [entryExpanded, setEntryExpanded] = React.useState(false);
  const [open, setOpen] = React.useState(false);
  const [localId, setLocalId] = React.useState(entry.id);
  const [localizedString, setLocalizedString] = React.useState(entry.label || {});
  const [localRule, setLocalRule] = React.useState(entry.when ?? '');
  const [translatingLanguage, setTranslatingLanguage] = React.useState<string | null>(null);
  const inputRef = React.useRef<HTMLInputElement>(null);
  const length = savingState.valueSets?.find(v => v.id === valueSetId)?.entries?.length || 0;

  const getEntryId = (): string => {
    return buildValueSetEntryId(valueSetId!, index, entry.id);
  };

  const { isAITranslated, getMetadata: getAITranslationMetadata, removeFlag } = useAITranslation(getEntryId());

  const handleUpdate = (value: string, language: string) => {
    setLocalizedString(prev => ({ ...prev, [language]: value }));
  }

  const handleClearLanguage = (language: string) => {
    const updated = { ...localizedString };
    delete updated[language];
    setLocalizedString(updated);
    onTextEdit(entry, updated);
    if (isAITranslated(language)) {
      removeFlag(language);
    }
  };

  const handleBlurText = (language: string) => {
    const currentValue = localizedString[language] || '';
    const originalValue = entry.label?.[language] || '';
    if (currentValue !== originalValue) {
      onTextEdit(entry, localizedString);
      
      // Remove AI translation flag if user manually edits
      if (isAITranslated(language)) {
        removeFlag(language);
      }
    }
  }

  const handleUpdateRule = (value: string) => {
    setLocalRule(value);
  }

  const handleBlurRule = (value: string) => {
    if (value !== entry.when) {
      onRuleEdit(entry, value);
    }
  }

  const handleChangeId = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setLocalId(e.target.value);
  }

  const handleBlurId = () => {
    if (localId !== entry.id) {
      onUpdateId(entry, localId);
    }
  }

  React.useEffect(() => {
    setLocalId(entry.id);
  }, [entry.id]);

  React.useEffect(() => {
    setLocalizedString(entry.label || {});
  }, [entry.label]);

  React.useEffect(() => {
    setLocalRule(entry.when ?? '');
  }, [entry.when]);

  const handleMove = (direction: 'up' | 'down') => {
    if (valueSetId) {
      const newIndex = direction === 'up' ? index - 1 : index + 1;
      moveValueSetEntry(valueSetId, index, newIndex);
      onMove?.(entry, direction);
    }
  }

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


  return (
    <>
      <ChoiceDeleteDialog open={open} itemId={entry.id} onClick={() => onDelete(entry)} onClose={() => setOpen(false)} />
      <Table>
        <TableBody>
          <TableRow sx={{ backgroundColor: alpha(backgroundColor, 0.1) }}>
            <TableCell align='center' width='15%'>
              <IconButton sx={{ p: 0.5 }} onClick={() => setOpen(true)}><Close color='error' /></IconButton>
              {!isGlobal && <IconButton onClick={() => setEntryExpanded(!entryExpanded)}><Visibility color={entry.when ? 'primary' : 'inherit'} /></IconButton>}
              <IconButton sx={{ p: 0.5 }} onClick={() => handleMove('up')} disabled={index === 0}>
                <ArrowUpward />
              </IconButton>
              <IconButton sx={{ p: 0.5 }} onClick={() => handleMove('down')} disabled={index === length - 1}>
                <ArrowDownward />
              </IconButton>
            </TableCell>
            <TableCell width='20%' sx={{ p: 0.5 }}>
              <TextField value={localId} onChange={handleChangeId} onBlur={handleBlurId}
                variant='standard' fullWidth inputRef={inputRef}
                InputProps={{
                  disableUnderline: true
                }} />
            </TableCell>
            {formLanguages?.map(lang => {
              const hasValue = localizedString && localizedString[lang] !== undefined;
              const sourceLanguage = editor.activeFormLanguage;
              const hasSourceValue = localizedString && localizedString[sourceLanguage];
              const canTranslate = !hasValue && hasSourceValue && lang !== sourceLanguage && config.translationServiceUrl;
              const isTranslating = translatingLanguage === lang;
              const aiMetadata = isAITranslated(lang) ? getAITranslationMetadata(lang) : null;

              return (
                <TableCell key={lang} width={formLanguages ? `${65 / formLanguages.length}%` : 0} sx={{ p: 0.5 }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <OverflowTooltipTextField 
                      value={localizedString ? localizedString[lang] : ''} 
                      variant="standard" 
                      fullWidth 
                      InputProps={{ disableUnderline: true }} 
                      onChange={(e) => handleUpdate(e.target.value, lang)}
                      onBlur={() => handleBlurText(lang)}
                    />
                    {hasValue && (
                      <Tooltip title={<FormattedMessage id="buttons.clear" />}>
                        <span>
                          <IconButton
                            disabled={!hasValue}
                            onClick={() => handleClearLanguage(lang)}
                            size="small"
                            sx={{ p: 0.25 }}
                          >
                            <Close fontSize="small" />
                          </IconButton>
                        </span>
                      </Tooltip>
                    )}
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
          {entryExpanded && !isGlobal && <TableRow>
            <TableCell colSpan={2 + languageNo}>
              {!isGlobal && <Box sx={{ display: 'flex', flexDirection: 'column', p: 1 }}>
                <Typography color='text.hint' variant='caption'><FormattedMessage id='dialogs.options.rules.visibility' /></Typography>
                <CodeMirror value={localRule} onChange={handleUpdateRule} onBlur={() => handleBlurRule(localRule)} />
              </Box>}
            </TableCell>
          </TableRow>}
        </TableBody>
      </Table >
    </>
  );
}

export default ChoiceItem;
