import { Box, TextField, Typography } from "@mui/material";
import React from "react";
import { FormattedMessage } from "react-intl";
import { useEditor } from "../editor";
import { useComposer } from "../dialob";

const DefaultValueEditor: React.FC = () => {
  const { editor } = useEditor();
  const { updateItem } = useComposer();
  const item = editor.activeItem;
  const [defaultValue, setDefaultValue] = React.useState<string>(item?.defaultValue || '');

  const handleUpdateDefaultValue = (value: string) => {
    setDefaultValue(value);
    if (item && value !== '') {
      updateItem(item.id, 'defaultValue', value);
    }
  }

  if (!item) {
    return null;
  }

  return (
    <Box>
      <Typography><FormattedMessage id='dialogs.options.default.set' /></Typography>
      <TextField variant='outlined' value={defaultValue} onChange={(e) => handleUpdateDefaultValue(e.target.value)} />
    </Box>
  );
}

export default DefaultValueEditor;
