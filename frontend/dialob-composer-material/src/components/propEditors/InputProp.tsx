import { StyledTextField } from '../TableEditorComponents';

export const InputProp = (
  props: any
) => {
  const { value, setValue, ...inputProps } = props;

  return (
    <StyledTextField variant='standard' InputProps={{
      disableUnderline: true,
      inputProps: inputProps
    }} value={value} onChange={(e) => setValue(e.target.value)} />
  );
}
