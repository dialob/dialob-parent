import React, { useEffect, useRef, useState } from 'react'
import AddIcon from '@mui/icons-material/Add';
import { Box, TableContainer, Typography, Table, TableRow, TableHead, TableBody, Tooltip, IconButton, SvgIcon, OutlinedInput, TableCell, Button } from '@mui/material';
import { Spinner } from './components/Spinner';
import { checkHttpResponse, handleRejection } from './middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters } from './types';
import { addAdminFormConfiguration, editAdminFormConfiguration, getAdminFormConfiguration, getAdminFormConfigurationList } from './backend';
import { FormattedMessage, useIntl } from 'react-intl';
import { CreateDialog } from './components/CreateDialog';
import { DeleteDialog } from './components/DeleteDialog';
import { TagTableRow } from './components/TagTableRow';
import DownloadIcon from '@mui/icons-material/Download';
import { downloadAsJSON } from './util/helperFunctions';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { DialobAdminConfig } from './index';
import CustomDatePicker from './components/CustomDatePicker';

export interface DialobAdminViewProps {
	config: DialobAdminConfig;
	showSnackbar?: (message: string, severity: 'success' | 'error') => void;
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

	const handleChangeInput = (e: any) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[e.target.name]: e.target.value
		}));
	}

	const handleDateChange = (name: string, date: Date | null) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[name]: date
		}));
	};

	const handleDateClear = (name: string) => {
		setFilters(prevFilters => ({
			...prevFilters,
			[name]: null
		}));
	};

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
								<Button onClick={handleUploadClick} sx={{ width: '50px', height: '50px' }}>
									<SvgIcon fontSize="small" >
										<FileUploadIcon />
									</SvgIcon>
								</Button>
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
						<Table size="small">
							<TableHead>
								<TableRow>
									<TableCell width="3%" sx={{ textAlign: "center" }}>
										<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.add" })} placement='top-end' arrow>
											<IconButton
												onClick={function (e: any) {
													e.preventDefault();
													addFormConfiguration();
												}}
											>
												<SvgIcon fontSize="medium"><AddIcon /></SvgIcon>
											</IconButton>
										</Tooltip>
									</TableCell>
									<TableCell width="3%"></TableCell>
									<TableCell width="25%">
										<FormattedMessage id={"adminUI.formConfiguration.label"} />
									</TableCell>
									<TableCell width="25%">
										<FormattedMessage id={"adminUI.formConfiguration.latestTagName"} />
									</TableCell>
									<TableCell width="19%">
										<FormattedMessage id={"adminUI.formConfiguration.latestTagDate"} />
									</TableCell>
									<TableCell width="19%">
										<FormattedMessage id={"adminUI.formConfiguration.lastSaved"} />
									</TableCell>
									<TableCell width="3%" />
									<TableCell width="3%" sx={{ textAlign: "center" }}>
										<Tooltip title={intl.formatMessage({ id: "download.all" })} placement='top-end' arrow>
											<IconButton
												onClick={function (e: any) {
													e.preventDefault();
													downloadAllFormConfigurations();
												}}
											>
												<SvgIcon fontSize="small"><DownloadIcon /></SvgIcon>
											</IconButton>
										</Tooltip>
									</TableCell>
								</TableRow>
							</TableHead>
							<TableBody>
								<TableRow>
									<TableCell />
									<TableCell />
									<TableCell>
										<OutlinedInput
											sx={{ height: '40px' }}
											name='label'
											onChange={handleChangeInput}
											value={filters.label}
										/>
									</TableCell>
									<TableCell>
										<OutlinedInput
											sx={{ height: '40px' }}
											name='latestTagName'
											onChange={handleChangeInput}
											value={filters.latestTagName}
										/>
									</TableCell>
									<TableCell>
										<CustomDatePicker
											value={filters.latestTagDate}
											onChange={(date) => handleDateChange('latestTagDate', date)}
											handleDateClear={() => handleDateClear('latestTagDate')}
										/>
									</TableCell>
									<TableCell>
										<CustomDatePicker
											value={filters.lastSaved}
											onChange={(date) => handleDateChange('lastSaved', date)}
											handleDateClear={() => handleDateClear('lastSaved')}
										/>
									</TableCell>
									<TableCell />
									<TableCell />
								</TableRow>
								{formConfigurations.map((formConfiguration: FormConfiguration, index: number) =>
									<TagTableRow
										key={index}
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
