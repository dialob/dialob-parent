import React, { useState } from 'react';
import { AppBar, Toolbar, Button, Menu, MenuItem, Container, Grid, Box, Typography } from '@mui/material';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import { THEMES } from './theme';

interface AppHeaderProps {
	setThemeIndex: (index: number) => void;
	themeIndex: number;
}

export const AppHeader: React.FC<AppHeaderProps> = ({ setThemeIndex, themeIndex }) => {
	const [themeSelAnchorEl, setThemeSelAnchorEl] = useState<null | HTMLElement>(null);

	const handleThemeMenuOpen = (event: React.MouseEvent<HTMLButtonElement>) => {
		setThemeSelAnchorEl(event.currentTarget);
	}

	const handleThemeMenuClose = () => {
		setThemeSelAnchorEl(null);
	}

	const handleThemeSelect = (idx: number) => {
		handleThemeMenuClose();
		setThemeIndex(idx);
	}

	const selectTheme = (
		<Button aria-controls='theme-menu' aria-haspopup='true' onClick={handleThemeMenuOpen} color='secondary'>
			<Typography>{THEMES[themeIndex].name}</Typography>
			<ArrowDropDownIcon fontSize='small' />
		</Button>
	);

	const menuTheme = (
		<Menu id='theme-menu' anchorEl={themeSelAnchorEl} keepMounted open={Boolean(themeSelAnchorEl)} onClose={handleThemeMenuClose}>
			{THEMES.map((theme, idx) =>
				<MenuItem key={idx} selected={idx === themeIndex} onClick={() => handleThemeSelect(idx)}>
					{THEMES[idx].name}
				</MenuItem>)}
		</Menu>
	);

	const title = (<Box sx={{ flexGrow: 1 }}><Typography variant='h6'>Dialob Admin View</Typography></Box>);
	return (
		<Container maxWidth='xl'>
			<Grid container>
				<Grid item xs={12}>
					<AppBar position="sticky" color="inherit" elevation={1}>
						<Toolbar sx={{ pt: 2, pr: 1, pb: 2, pl: 1 }}>{title}{selectTheme}{menuTheme}</Toolbar>
					</AppBar>
				</Grid>
			</Grid>
		</Container>
	);
}
