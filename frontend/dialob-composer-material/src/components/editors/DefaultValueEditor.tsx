import { Alert, Box, Typography } from "@mui/material";
import React from "react";
import { FormattedMessage } from "react-intl";
import { useEditor } from "../../editor";
import { getErrorSeverity } from "../../utils/ErrorUtils";
import { Warning } from "@mui/icons-material";
import { ErrorMessage } from "../ErrorComponents";
import { useSave } from "../../dialogs/contexts/saving/useSave";
import { TextEditorWithClear } from "./TextEditorWithClear";

const DefaultValueEditor: React.FC = () => {
  const { savingState, updateItem } = useSave();
  const { editor } = useEditor();
  const item = savingState.item;
  const itemErrors = editor.errors?.filter(e => e.itemId === item?.id && e.message === 'INVALID_DEFAULT_VALUE');

  if (!item) {
    return null;
  }

  const handleUpdate = (value: string) => {
    if (value !== item.defaultValue) {
      updateItem(item.id, 'defaultValue', value);
    }
  }

  const handleClear = () => {
    updateItem(item.id, 'defaultValue', undefined);
  };

  return (
    <Box>
      <Typography color="text.hint"><FormattedMessage id='dialogs.options.default' /></Typography>
      <TextEditorWithClear 
        variant='outlined' 
        value={item.defaultValue} 
        onChange={handleUpdate}
        onClear={handleClear}
      />
      {itemErrors?.map((error, index) => <Alert key={index} severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </Box>
  );
}

export { DefaultValueEditor };
