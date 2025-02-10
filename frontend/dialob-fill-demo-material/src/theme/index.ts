import { createTheme } from '@mui/material';
import { altTheme } from './altTheme';
import { appTheme } from './appTheme';

export { appTheme } from './appTheme';
export { altTheme } from './altTheme';

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
];
