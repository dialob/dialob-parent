import { Alert, Box, TextField, Typography } from "@mui/material";
import React from "react";
import { FormattedMessage } from "react-intl";
import { useEditor } from "../../editor";
import { useComposer } from "../../dialob";
import { getErrorSeverity } from "../../utils/ErrorUtils";
import { Warning } from "@mui/icons-material";
import { ErrorMessage } from "../ErrorComponents";

const DefaultValueEditor: React.FC = () => {
  const { editor, setActiveItem } = useEditor();
  const { updateItem } = useComposer();
  const item = editor.activeItem;
  const itemErrors = editor.errors?.filter(e => e.itemId === item?.id && e.message === 'INVALID_DEFAULT_VALUE');
  const [defaultValue, setDefaultValue] = React.useState<string>(item?.defaultValue || '');

  React.useEffect(() => {
    setDefaultValue(item?.defaultValue || '');
  }, [item]);

  React.useEffect(() => {
    if (item && defaultValue !== item?.defaultValue) {
      if (defaultValue === '' && item?.defaultValue === undefined) {
        return;
      }
      const id = setTimeout(() => {
        updateItem(item?.id, 'defaultValue', defaultValue);
        setActiveItem({ ...item, defaultValue });
      }, 300);
      return () => clearTimeout(id);
    }
  }, [defaultValue]);

  if (!item) {
    return null;
  }

  return (
    <Box>
      <Typography color="text.hint"><FormattedMessage id='dialogs.options.default' /></Typography>
      <TextField variant='outlined' value={defaultValue} onChange={(e) => setDefaultValue(e.target.value)} />
      {itemErrors?.map((error, index) => <Alert severity={getErrorSeverity(error)} sx={{ mt: 2 }} icon={<Warning />}>
        <Typography key={index} color={error.level.toLowerCase()}><ErrorMessage error={error} /></Typography>
      </Alert>)}
    </Box>
  );
}

export { DefaultValueEditor };
