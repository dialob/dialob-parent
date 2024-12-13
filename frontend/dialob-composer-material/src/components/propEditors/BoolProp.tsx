import { Switch } from "@mui/material";

export const BoolProp = (
  props: any
) => {
  const { value, setValue } = props;

  return (
    <Switch checked={value} onChange={(e) => setValue(e.target.checked)} />
  );
}
