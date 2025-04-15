import { createTheme } from '@mui/material';
import { theme1 } from './theme1';
import { theme2 } from './theme2';
import { theme3 } from './theme3';

export const THEMES = [
	{
		name: 'MUI Default',
		theme: createTheme({})
	},
	{
		name: 'Theme 1',
		theme: theme1
	},
	{
		name: 'Theme 2',
		theme: theme2
	},
	{
		name: "Theme 3",
		theme: theme3
	}
];
