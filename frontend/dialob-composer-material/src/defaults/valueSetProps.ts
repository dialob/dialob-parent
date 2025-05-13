import { Box } from '@mui/material';
import type { Component, FunctionComponent } from 'react';

interface ValueSetProp {
  title: string,
  name: string,
  editor: Component | FunctionComponent,
}

export const DEFAULT_VALUESET_PROPS: ValueSetProp[] = [
  {
    title: 'Custom attribute',
    name: 'attr',
    editor: Box,
  }
];
