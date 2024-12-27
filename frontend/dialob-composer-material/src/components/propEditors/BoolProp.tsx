import { Switch } from "@mui/material";

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const BoolProp = (props: any) => {
  const { value, setValue } = props;

  return (
    <Switch checked={value} onChange={(e) => setValue(e.target.checked)} />
  );
}
