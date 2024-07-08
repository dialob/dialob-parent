import React, { useCallback, useEffect, useRef, useState } from 'react'
import AddIcon from '@mui/icons-material/Add';
import { Box, TableContainer, Typography, Table, TableRow, TableHead, TableBody, Tooltip, useTheme } from '@mui/material';
import { Spinner } from './components/Spinner';
import { StyledTableCell, StyledTableRow, StyledIcon, StyledIconButton, StyledOutlinedInput, ActionIconButton } from './style';
import { checkHttpResponse, handleRejection } from './middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters } from './types';
import { addAdminFormConfiguration, editAdminFormConfiguration, getAdminFormConfiguration, getAdminFormConfigurationList } from './backend';
import { FormattedMessage,  useIntl } from 'react-intl';
import { CreateDialog } from './components/CreateDialog';
import { DeleteDialog } from './components/DeleteDialog';
import { TagTableRow } from './components/TagTableRow';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import DownloadIcon from '@mui/icons-material/Download';
import { downloadAsJSON } from './util/helperFunctions';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { DialobAdminConfig } from './index';

export interface DialobAdminViewProps {
	config: DialobAdminConfig;
	showSnackbar?: (message: string, severity: 'success' | 'error') => void;
}

const getDatePickerSx = (theme: any) => {
	return {
		"& .MuiInputBase-root, MuiOutlinedInput-root": {
			height: "40px",
			'&: hover .MuiOutlinedInput-notchedOutline': {
				borderColor: theme.palette.success.main,
			},
			"&.Mui-focused .MuiOutlinedInput-notchedOutline": {
				border: `1px solid ${theme.palette.success.main}`,
			}
		}
	}
}

export const DialobAdminView: React.FC<DialobAdminViewProps> = ({ config, showSnackbar }) => {
	const [formConfigurations, setFormConfigurations] = useState<FormConfiguration[]>([]);
	const [selectedFormConfiguration, setSelectedFormConfiguration] = useState<FormConfiguration | undefined>();
	const [dialobForms, setDialobForms] = useState<any>([]);
	const [filters, setFilters] = useState<FormConfigurationFilters>(DEFAULT_CONFIGURATION_FILTERS);
	const [createModalOpen, setCreateModalOpen] = useState<boolean>(false);
	const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);
	const [fetchAgain, setFetchAgain] = useState<boolean>(false);
	const fileInputRef = useRef<HTMLInputElement | null>(null);
	const theme = useTheme();
	const intl = useIntl();

	const handleCreateModalClose = () => {
		setSelectedFormConfiguration(undefined);
		setCreateModalOpen(false);
	}

	const handleDeleteModalClose = () => {
		setSelectedFormConfiguration(undefined);
		setDeleteModalOpen(false);
	}

	useEffect(() => {
		getAdminFormConfigurationList(config)
			.then((response: Response) => checkHttpResponse(response, config.setLoginRequired))
			.then((response: { json: () => any; }) => response.json())
			.then((formConfigurations: FormConfiguration[]) => {
				setFormConfigurations(formConfigurations);
			})
			.catch((ex: any) => {
				handleRejection(ex, config.setTechnicalError);
			});
	},
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[fetchAgain])

	const copyFormConfiguration = (formConfiguration: FormConfiguration) => {
		setSelectedFormConfiguration(formConfiguration);
		setCreateModalOpen(true);
	}

	const addFormConfiguration = () => {
		setCreateModalOpen(true);
	}

	const deleteFormConfiguration = (formConfiguration: FormConfiguration) => {
		setSelectedFormConfiguration(formConfiguration);
		setDeleteModalOpen(true);
	}

	const handleChangeInput = useCallback((e: any) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[e.target.name]: e.target.value
		}));
	}, [])

	const handleDateChange = useCallback((name: string, date: Date | null) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[name]: date instanceof Date ? date.toISOString() : date
		}));
	}, []);

	const handleDateClear = useCallback((name: string) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[name]: null
		}));
	}, []);

	useEffect(() => {
		let isCancelled = false;
		setDialobForms([]);
		const formNamesList = formConfigurations?.map((formConfiguration: FormConfiguration) => formConfiguration.id) || [];

		const fetchForms = async () => {
			const forms = [];
			for (const formName of formNamesList) {
				try {
					const response = await getAdminFormConfiguration(formName, config);
					await checkHttpResponse(response, config.setLoginRequired);
					const form = await response.json();
					forms.push(form);
				} catch (ex) {
					handleRejection(ex, config.setTechnicalError);
				}
			}
			if (!isCancelled) {
				setDialobForms(forms);
			}
		};
		fetchForms();

		return () => {
			isCancelled = true;
		};
	}, [config, config.setLoginRequired, config.setTechnicalError, formConfigurations]);

	const downloadAllFormConfigurations = () => {
		downloadAsJSON(dialobForms);
	}

	const handleUploadClick = () => {
		if (fileInputRef.current) {
			fileInputRef.current.click();
		}
	};

	const uploadDialogForm = (e: any) => {
		const file = e.target.files[0];

		const handleFileRead = async (event: ProgressEvent<FileReader>) => {
			const result = event.target?.result;
			if (typeof result === 'string') {
				try {
					const json = JSON.parse(result);
					if (!Array.isArray(json)) {
						const formNamesList = formConfigurations?.map((formConfiguration: FormConfiguration) => formConfiguration.id) || [];
						delete json._id;
						delete json._rev;

						const uploadPromise = formNamesList.includes(json.name)
							? editAdminFormConfiguration(json, config)
							: addAdminFormConfiguration(json, config);

						try {
							const response = await uploadPromise;
							await checkHttpResponse(response, config.setLoginRequired);
							await response.json();
							if (showSnackbar) {
								showSnackbar(`Uploaded ${formNamesList.includes(json.name) ? 'an existing' : 'a new'} form successfully.`, 'success');
							}
							setFetchAgain((prevState) => !prevState);
						} catch (ex: any) {
							if (showSnackbar) {
								showSnackbar(`Error while uploading ${formNamesList.includes(json.name) ? 'an existing' : 'a new'} form: ${ex}`, 'error');
							}
							handleRejection(ex, config.setTechnicalError);
						}
					} else {
						if (showSnackbar) {
							showSnackbar(`JSON needs to contain an object, not an array.`, 'error');
						}
					}
				} catch (error: any) {
					if (showSnackbar) {
						showSnackbar(`Error parsing JSON: ${error.message}`, 'error');
					}
				}
			}
		};

		const handleFileError = () => {
			if (showSnackbar) {
				showSnackbar(`Error reading file.`, 'error');
			}
		};

		if (file) {
			const reader = new FileReader();
			reader.onload = handleFileRead;
			reader.onerror = handleFileError;
			reader.readAsText(file);
		}

		if (fileInputRef.current) {
			fileInputRef.current.value = '';
		}
	};

	return (
				<Box pt={6}>
					{formConfigurations ? (
						<Box sx={{ padding: "0 50px" }}>
							<Box sx={{ display: "flex", justifyContent: "space-between" }}>
								<Typography sx={{ mb: 4 }} variant='h2'><FormattedMessage id={'adminUI.dialog.heading'} /></Typography>
								<Box>
									<Tooltip title={intl.formatMessage({ id: "upload" })} placement='top-end' arrow>
										<ActionIconButton onClick={handleUploadClick}>
											<StyledIcon fontSize="small" >
												<FileUploadIcon />
											</StyledIcon>
										</ActionIconButton>
									</Tooltip>
									<input
										ref={fileInputRef}
										type='file'
										accept='.json'
										hidden
										onChange={(e) => uploadDialogForm(e)}
									/>
								</Box>
							</Box>
							<TableContainer>
								<Table size="small" sx={{ borderCollapse: 'separate', borderSpacing: '2px 2px' }}>
									<TableHead>
										<TableRow>
											<StyledTableCell width="3%" sx={{ border: 'none', textAlign: "center" }}>
												<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.add" })} placement='top-end' arrow>
													<StyledIconButton
														onClick={function (e: any) {
															e.preventDefault();
															addFormConfiguration();
														}}
													>
														<StyledIcon fontSize="medium"><AddIcon /></StyledIcon>
													</StyledIconButton>
												</Tooltip>
											</StyledTableCell>
											<StyledTableCell width="3%" sx={{ border: 'none' }}></StyledTableCell>
											<StyledTableCell width="25%" sx={{ border: 'none' }}>
												<FormattedMessage id={"adminUI.formConfiguration.label"} />
											</StyledTableCell>
											<StyledTableCell width="25%" sx={{ border: 'none' }}>
												<FormattedMessage id={"adminUI.formConfiguration.latestTagName"} />
											</StyledTableCell>
											<StyledTableCell width="19%" sx={{ border: 'none' }}>
												<FormattedMessage id={"adminUI.formConfiguration.latestTagDate"} />
											</StyledTableCell>
											<StyledTableCell width="19%" sx={{ border: 'none' }}>
												<FormattedMessage id={"adminUI.formConfiguration.lastSaved"} />
											</StyledTableCell>
											<StyledTableCell width="3%" sx={{ border: 'none' }} />
											<StyledTableCell width="3%" sx={{ border: 'none', textAlign: "center" }}>
												<Tooltip title={intl.formatMessage({ id: "download.all" })} placement='top-end' arrow>
													<StyledIconButton
														onClick={function (e: any) {
															e.preventDefault();
															downloadAllFormConfigurations();
														}}
													>
														<StyledIcon fontSize="small"><DownloadIcon /></StyledIcon>
													</StyledIconButton>
												</Tooltip>
											</StyledTableCell>
										</TableRow>
									</TableHead>
									<TableBody>
										<StyledTableRow>
											<StyledTableCell />
											<StyledTableCell />
											<StyledTableCell>
												<StyledOutlinedInput
													name='label'
													onChange={handleChangeInput}
													value={filters.label}
												/>
											</StyledTableCell>
											<StyledTableCell>
												<StyledOutlinedInput
													name='latestTagName'
													onChange={handleChangeInput}
													value={filters.latestTagName}
												/>
											</StyledTableCell>
											<StyledTableCell>
												<DatePicker
													value={filters.latestTagDate}
													onChange={(date: Date | null) => handleDateChange('latestTagDate', date)}
													sx={getDatePickerSx(theme)}
													slotProps={{
														field: {
															clearable: true,
															onClear: () => handleDateClear("latestTagDate")
														}
													}}
												/>
											</StyledTableCell>
											<StyledTableCell>
												<DatePicker
													value={filters.lastSaved}
													onChange={(date: Date | null) => handleDateChange('lastSaved', date)}
													sx={getDatePickerSx(theme)}
													slotProps={{
														field: {
															clearable: true,
															onClear: () => handleDateClear("lastSaved")
														}
													}}
												/>
											</StyledTableCell>
											<StyledTableCell />
											<StyledTableCell />
										</StyledTableRow>
										{formConfigurations.map((formConfiguration: FormConfiguration) =>
											<TagTableRow
												filters={filters}
												formConfiguration={formConfiguration}
												deleteFormConfiguration={deleteFormConfiguration}
												copyFormConfiguration={copyFormConfiguration}
												dialobForm={dialobForms.find((dialobForm: any) => dialobForm.name === formConfiguration.id)}
												config={config}
											/>
										)}
									</TableBody>
								</Table>
							</TableContainer>
							<CreateDialog
								createModalOpen={createModalOpen}
								handleCreateModalClose={handleCreateModalClose}
								setFetchAgain={setFetchAgain}
								formConfiguration={selectedFormConfiguration}
								config={config}
							/>
							<DeleteDialog
								deleteModalOpen={deleteModalOpen}
								handleDeleteModalClose={handleDeleteModalClose}
								setFetchAgain={setFetchAgain}
								formConfiguration={selectedFormConfiguration}
								config={config}
							/>
						</Box>
					) : (
						<Spinner />
					)}
				</Box>
	);
}
