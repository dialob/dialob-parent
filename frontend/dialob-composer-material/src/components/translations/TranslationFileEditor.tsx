import React, { useCallback, useMemo, useRef, useState } from 'react';
import { Alert, AlertColor, Box, Button, Paper, Typography } from '@mui/material';
import { useComposer } from '../../dialob';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { FormattedMessage } from 'react-intl';
import { ParsedImportData, downloadFormData, overwiewTextFormatter, parse, validateParsedFileData, validateParsedFileHeaders } from '../../utils/TranslationUtils';

interface OverviewSectionProps {
	content?: string[];
	title: string;
}

interface OverviewAlertProps {
	severity: AlertColor | undefined;
	title: string;
}

const OverviewSection: React.FC<OverviewSectionProps> = ({ content, title }) => {
	return (
		<Box width="50%">
			<Typography sx={{ mb: 2 }} variant="h6"><FormattedMessage id={title} /></Typography>
			<Box sx={{ display: "flex", flexDirection: "column" }}>
				{content?.map((item, index) =>
					<Typography key={index} style={{ margin: "2px 0px" }}>{overwiewTextFormatter(item)}</Typography>
				)}
			</Box>
		</Box>
	);
}

const OverviewAlert: React.FC<OverviewAlertProps> = ({ severity, title }) => {
	return (
		<Alert severity={severity} variant="outlined" sx={{ width: "100%", mb: 2 }}>
			<FormattedMessage id={title} />
		</Alert>
	)
}

const TranslationFileEditor: React.FC = () => {
	const fileInputRef = useRef<HTMLInputElement>(null);
	const [translationOverviewOpen, setTranslationOverviewOpen] = useState<boolean>(false);
	const [parsedImportData, setParsedImportData] = useState<string[][] | undefined>(undefined);
	const [overviewData, setOverviewData] = useState<ParsedImportData | undefined>(undefined);
	const [uploadedFileName, setUploadedFileName] = useState<string | undefined>(undefined);
	const [validationError, setValidationError] = useState<boolean>(false);
	const { form, addLanguage, updateItem, updateValueSetEntryLabel, setValidationMessage } = useComposer();

	const languages = useMemo(() => {
		return form.metadata.languages || [];
	}, [form.metadata.languages])

	const updateTranslation = useCallback((key: string, language: string, text: string) => {
		const keyTokens = key.split(':');
		const id = keyTokens[1];
		if (keyTokens[0] === 'i') {
			// Item
			if (keyTokens[2] === 'l') {
				updateItem(id, 'label', text, language);
			} else if (keyTokens[2] === 'd') {
				updateItem(id, 'description', text, language);
			} else if (keyTokens[2] === 'v') {
				setValidationMessage(id, parseInt(keyTokens[3]), language, text);
			}
		} else if (keyTokens[0] === 'v') {
			// ValueSet
			updateValueSetEntryLabel(id, parseInt(keyTokens[2]), text, language);
		}
	}, [setValidationMessage, updateItem, updateValueSetEntryLabel])


	const handleConfirmTranslation = useCallback(() => {
		if (parsedImportData && parsedImportData.length > 1) {
			// checking if there is additional languages inside CSV
			if (parsedImportData[1].length - 4 > languages.length) {
				for (let i = 4; i < parsedImportData[1].length; i++) {
					const language = parsedImportData[1][i];
					if (!languages.includes(language)) {
						addLanguage(language);
					}
				}
			}
			// translating
			// Outer loop starts from 2 to skip headers
			for (let i = 2; i < parsedImportData.length; i++) {
				// Inner loop is looping through each row languages
				for (let j = 4; j < parsedImportData[i].length; j++) {
					const parsedKey = parsedImportData[i][0];
					const language = parsedImportData[1][j];
					const text = parsedImportData[i][j].trim();
					if (parsedImportData[i].length !== 0 && parsedKey) {
						if (!overviewData?.missingInForm) {
							updateTranslation(parsedKey, language, text);
						} else {
							if (!overviewData?.missingInForm.includes(parsedKey)) {
								updateTranslation(parsedKey, language, text);
							}
						}
					}
				}
			}
		}
	}, [addLanguage, languages, overviewData?.missingInForm, parsedImportData, updateTranslation])

	const fileChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
		if (event.target.files) {
			// eslint-disable-next-line @typescript-eslint/no-explicit-any
			const csvResult: any = await parse(event.target.files[0]);
			const { data } = csvResult;
			setTranslationOverviewOpen(true);
			const fileName = data[0][0];
			setUploadedFileName(`${fileName}.csv`);
			if (validateParsedFileHeaders(data, form)) {
				const dataValidationResult = validateParsedFileData(data, form);
				setParsedImportData(data)
				setOverviewData(dataValidationResult);
				setValidationError(false);
			} else {
				setValidationError(true);
				setOverviewData(undefined);
			}
		}
	};

	const handleDownload = useCallback(() => {
		downloadFormData(form);
	}, [form])

	const handleClose = useCallback(() => {
		setTranslationOverviewOpen(false);
		setParsedImportData(undefined);
		setOverviewData(undefined);
		setUploadedFileName(undefined);
		setValidationError(false);
	}, [])

	const handleClick = useCallback(() => {
		handleConfirmTranslation();
		handleClose();
	}, [handleClose, handleConfirmTranslation])

	const resetFileInput = () => {
		if (fileInputRef.current) {
			fileInputRef.current.value = '';
		}
	};

	const handleUpload = useCallback(() => {
		if (fileInputRef.current) {
			fileInputRef.current.click();
			resetFileInput();
		}
	}, [])

	const showMissingInCsv = useMemo(() => {
		return overviewData && overviewData.missingInCsv.length > 0;
	}, [overviewData])

	const showMissingInForm = useMemo(() => {
		return overviewData && overviewData.missingInForm.length > 0;
	}, [overviewData])

	const showSuccessAlert = useMemo(() => {
		return overviewData && overviewData.missingInCsv.length === 0 && overviewData.missingInForm.length === 0;
	}, [overviewData])

	const showError = useMemo(() => {
		return !overviewData && validationError;
	}, [overviewData, validationError])

	return (
		<Box>
			<Box sx={{ display: "flex", mb: 3, alignItems: "center", gap: 3 }}>
				<Button onClick={handleDownload} variant='outlined' endIcon={<FileDownloadIcon />} sx={{ textTransform: 'none'}}>
					<Typography><FormattedMessage id='dialogs.translations.files.download' /></Typography>
				</Button>
				<Button onClick={handleUpload} variant='outlined' endIcon={<FileUploadIcon />} sx={{ textTransform: 'none'}}>
					<Typography><FormattedMessage id='dialogs.translations.files.upload' /></Typography>
				</Button>
				<Typography>{uploadedFileName}</Typography>
				<input
					ref={fileInputRef}
					type='file'
					accept='text/csv'
					hidden
					onChange={(e) => fileChange(e)}
				/>
			</Box>
			{
				translationOverviewOpen && <Paper elevation={4} sx={{ borderRadius: "10px", padding: 2 }}>
					<Typography variant="h3"><FormattedMessage id="dialogs.translations.files.overview" /></Typography>
					<Box display="flex" maxHeight="35vh" mt={2} sx={{ overflowY: "auto" }}>
						{showMissingInCsv && <OverviewSection content={overviewData?.missingInCsv} title="dialogs.translations.files.missing" />}
						{showMissingInForm && <OverviewSection content={overviewData?.missingInForm} title="dialogs.translations.files.missing.form" />}
						{showSuccessAlert && <OverviewAlert severity="success" title="dialogs.translations.files.success" />}
						{showError && <OverviewAlert severity="error" title="dialogs.translations.files.error" />}
					</Box>
					<Box display="flex" justifyContent="flex-end" gap={1} mt={2}>
						<Button onClick={handleClose} variant='text' color='error'><FormattedMessage id='buttons.cancel' /></Button>
						<Button onClick={handleClick} variant='contained' disabled={!!validationError}><FormattedMessage id='buttons.confirm' /></Button>
					</Box>
				</Paper>
			}
		</Box>
	);
};

export { TranslationFileEditor };
