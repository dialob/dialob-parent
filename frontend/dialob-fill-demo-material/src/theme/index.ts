import { createTheme } from '@mui/material';
import { altTheme } from './altTheme';
import { appTheme } from './appTheme';
import { hbTheme } from './hbTheme';
import { pintTheme } from './pintTheme';
import { popTheme } from './popTheme';

export {appTheme} from './appTheme';
export {altTheme} from './altTheme';

export const THEMES = [
 {
  name: 'MUI Default',
  theme: createTheme({})
 },
 {
  name: 'Dialob',
  theme: altTheme
 },
 {
  name: 'Alternative',
  theme: appTheme
 },
 {
  name: 'HB',
  theme: hbTheme
 },
 {
  name: 'PINT',
  theme: pintTheme
 },
 {
  name: 'POP',
  theme: popTheme
 }
];
