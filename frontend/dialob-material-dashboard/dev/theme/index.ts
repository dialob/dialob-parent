import { createTheme } from '@mui/material';
import { popTheme } from './popTheme';
import { sipooTheme } from './sipooTheme';
import { mifidTheme } from './mifidTheme';

export const THEMES = [
	{
		name: 'MUI Default',
		theme: createTheme({})
	},
	{
		name: 'POP',
		theme: popTheme
	},
	{
		name: 'SIPOO',
		theme: sipooTheme
	},
	{
		name: "MIFID",
		theme: mifidTheme
	}
];
