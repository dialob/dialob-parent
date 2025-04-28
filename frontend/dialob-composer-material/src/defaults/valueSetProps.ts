import { Box } from '@mui/material';

interface ValueSetProp {
  title: string,
  name: string,
  editor: any
}

export const DEFAULT_VALUESET_PROPS: ValueSetProp[] = [
  {
    title: 'Custom attribute',
    name: 'attr',
    editor: Box,
  }
];
