import { Box, TextField, Typography } from "@mui/material";
import React from "react";
import { FormattedMessage } from "react-intl";
import { useEditor } from "../../editor";
import { useComposer } from "../../dialob";

const DefaultValueEditor: React.FC = () => {
  const { editor, setActiveItem } = useEditor();
  const { updateItem } = useComposer();
  const item = editor.activeItem;
  const [defaultValue, setDefaultValue] = React.useState<string>(item?.defaultValue || '');

  React.useEffect(() => {
    setDefaultValue(item?.defaultValue || '');
  }, [item]);

  React.useEffect(() => {
    if (item && defaultValue !== '') {
      const id = setTimeout(() => {
        updateItem(item?.id, 'defaultValue', defaultValue);
        setActiveItem({ ...item, defaultValue });
      }, 1000);
      return () => clearTimeout(id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [defaultValue]);

  if (!item) {
    return null;
  }

  return (
    <Box>
      <Typography><FormattedMessage id='dialogs.options.default.set' /></Typography>
      <TextField variant='outlined' value={defaultValue} onChange={(e) => setDefaultValue(e.target.value)} />
    </Box>
  );
}

export { DefaultValueEditor };
