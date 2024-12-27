import { StyledTextField } from '../TableEditorComponents';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const InputProp = (props: any) => {
  const { value, setValue, ...inputProps } = props;

  return (
    <StyledTextField variant='standard' InputProps={{
      disableUnderline: true,
      inputProps: inputProps
    }} value={value} onChange={(e) => setValue(e.target.value)} />
  );
}
