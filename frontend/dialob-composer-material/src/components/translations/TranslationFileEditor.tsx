import React, { useCallback, useMemo, useRef, useState } from 'react';
import { Alert, Box, Button, Typography } from '@mui/material';
import Papa from 'papaparse';
import FileSaver from 'file-saver';
import { LocalizedString, useComposer } from '../../dialob';
import FileDownloadIcon from '@mui/icons-material/FileDownload';
import FileUploadIcon from '@mui/icons-material/FileUpload';
import { FormattedMessage } from 'react-intl';
import { getAllItemTranslations, getGlobalValueSetTranslations, parse } from '../../utils/TranslationUtils';

interface TranslationData {
	[key: string]: LocalizedString | undefined | string;
}

interface MetadataEntry {
	description: string;
	richText?: boolean;
	pageId: string;
	parent: string;
}

interface Metadata {
	key: { [key: string]: MetadataEntry };
}


interface ItemTranslations {
	translations: TranslationData;
	metadata: Metadata;
}

interface ScrollableSectionProps {
	content?: string[];
}

interface ParsedImportData {
	missingInCsv: string[];
	missingInForm: string[];
}

const ScrollableSection: React.FC<ScrollableSectionProps> = ({ content }) => {
	return (
		<Box sx={{ display: "flex", flexDirection: "column", height: "100%", overflowY: "auto" }}>
			{content?.map((item, index) => <Typography key={index} style={{ margin: "2px 0px" }}>{item}</Typography>)}
		</Box>
	);
}

const TranslationFileEditor: React.FC = () => {
	const fileInputRef = useRef<HTMLInputElement>(null);
	const [translationOverviewOpen, setTranslationOverviewOpen] = useState<boolean>(false);
	const [parsedImportData, setParsedImportData] = useState<string[][] | undefined>(undefined);
	const [overviewData, setOverviewData] = useState<ParsedImportData | undefined>(undefined);
	const [uploadedFileName, setUploadedFileName] = useState<string | undefined>(undefined);
	const [validationError, setValidationError] = useState<boolean>(false);
	const { form, addLanguage, updateItem, updateValueSetEntryOld, setValidationMessage } = useComposer();
	const formLabel = form.metadata.label;
	const formItems = form.data;
	const valueSets = form.valueSets;

	const languages = useMemo(() => {
		return form.metadata.languages || [];
	}, [form.metadata.languages])

	const validateParsedFileHeaders = (data: string[][]) => {
		if (data[0][0] !== formLabel || data[1][0] !== 'Item ID' || data[1][1] !== 'PageID' || data[1][2] !== 'ParentID ItemType' || data[1][3] !== 'Description')
			return false
		for (let i = 4; i < data[1].length; i++) {
			if (data[1][i].length !== 2)
				return false
		}
		return true
	}

	const validateParsedFileData = (data: string[][]) => {
		let itemIDs: string[] = Object.keys(formItems);
		let valueSetIDs: string[] = []
		if (valueSets) {
			valueSetIDs = valueSets.map((valueSet) => valueSet.id)
		}
		const parsedDataIds: Set<string> = new Set();
		const missingInForm: string[] = [];

		for (let i = 2; i < data.length; i++) {
			const firstColumn = data[i][0];
			const itemID = firstColumn.split(':')[1]
			parsedDataIds.add(itemID);
		}

		parsedDataIds.forEach((parsedDataId) => {
			if (itemIDs?.includes(parsedDataId)) {
				itemIDs = itemIDs.filter(itemID => itemID !== parsedDataId && itemID !== "questionnaire");
			} else if (valueSetIDs?.includes(parsedDataId)) {
				valueSetIDs = valueSetIDs.filter(valueSetID => valueSetID !== parsedDataId);
			} else {
				missingInForm.push(parsedDataId);
			}
		});

		const missingInCsv: string[] = [...itemIDs, ...valueSetIDs];

		return { missingInCsv, missingInForm }
	}

	const updateTranslation = useCallback((key: string, language: string, text: string) => {
		const keyTokens = key.split(':');
		if (keyTokens[0] === 'i') {
			// Item
			if (keyTokens[2] === 'l') {
				updateItem(keyTokens[1], 'label', text, language);
			} else if (keyTokens[2] === 'd') {
				updateItem(keyTokens[1], 'description', text, language);
			} else if (keyTokens[2] === 'v') {
				setValidationMessage(keyTokens[1], parseInt(keyTokens[3]), language, text);
			}
		} else if (keyTokens[0] === 'v') {
			// ValueSet
			updateValueSetEntryOld(keyTokens[1], parseInt(keyTokens[2]), undefined, text, language);
		}
	}, [setValidationMessage, updateItem, updateValueSetEntryOld])


	const handleConfirmTranslation = useCallback(() => {
		if (parsedImportData && parsedImportData.length > 1) {
			// checking if there is additional languages inside CSV
			if (parsedImportData[1].length - 4 > languages.length) {
				for (let i = 4; i < parsedImportData[1].length; i++)
					if (!languages.includes(parsedImportData[1][i])) {
						addLanguage(parsedImportData[1][i]);
					}
			}
			// translating
			for (let i = 2; i < parsedImportData.length; i++) {
				for (let j = 4; j < parsedImportData[i].length; j++) {
					if (parsedImportData[i].length !== 0 && parsedImportData[i][0]) {
						if (!overviewData?.missingInForm) {
							updateTranslation(parsedImportData[i][0], parsedImportData[1][j], parsedImportData[i][j].trim());
						} else {
							const key = parsedImportData[i][0].split(':')[1];
							if (!overviewData?.missingInForm.includes(key)) {
								updateTranslation(parsedImportData[i][0], parsedImportData[1][j], parsedImportData[i][j].trim());
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
			if (validateParsedFileHeaders(data)) {
				const dataValidationResult = validateParsedFileData(data);
				setParsedImportData(data)
				setOverviewData(dataValidationResult);
				setValidationError(false);
			} else {
				setValidationError(true);
				setOverviewData(undefined);
			}
		}
	};

	const createTranslationCSVRow = useCallback((value: MetadataEntry, key: string, translations: ItemTranslations) => {
		const row = [];
		row.push(key)
		row.push(value?.pageId);
		row.push(value?.parent);
		row.push(`${value.description} for ${key.split(":")[1]}`)
		languages.forEach(l => {
			const name = translations.translations[key];
			if (typeof name === "object") {
				row.push(name[l])
			} else {
				row.push("")
			}
		})
		return row;
	}, [languages])

	const createTranslationCSVformat = useCallback((allItemTranslations: ItemTranslations, globalValueSetTranslations: ItemTranslations | undefined, result: (string | undefined)[][]) => {
		for (const [key, value] of Object.entries(allItemTranslations.metadata.key)) {
			const row = createTranslationCSVRow(value, key, allItemTranslations);
			result.push(row)
		}
		if (globalValueSetTranslations) {
			for (const [key, value] of Object.entries(globalValueSetTranslations.metadata.key)) {
				const row = createTranslationCSVRow(value, key, globalValueSetTranslations);
				result.push(row)
			}
		}
		return result
	}, [createTranslationCSVRow])

	const downloadFormData = useCallback(() => {
		const allItemTranslations = getAllItemTranslations(form);
		const globalValueSetTranslations = getGlobalValueSetTranslations(form);

		let result = [];
		const firstRow = [formLabel]
		result.push(firstRow)
		const secondRow = ["Item ID", "PageID", "ParentID ItemType", "Description"];
		languages.forEach(l => {
			secondRow.push(l);
		})
		result.push(secondRow)
		result = createTranslationCSVformat(allItemTranslations, globalValueSetTranslations, result)

		const csv = Papa.unparse(result);
		const blob = new Blob([csv], { type: 'text/csv' });
		FileSaver.saveAs(blob, `translation_${formLabel}.csv`);
	}, [createTranslationCSVformat, form, formLabel, languages])

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
			<Box sx={{ display: "flex", mb: 3, alignItems: "center" }}>
				<Button sx={{ mr: 3 }} onClick={downloadFormData} variant='outlined' endIcon={<FileDownloadIcon />}>
					<Typography><FormattedMessage id='dialogs.translations.files.download' /></Typography>
				</Button>
				<Button sx={{ mr: 3 }} onClick={handleUpload} variant='outlined' endIcon={<FileUploadIcon />}>
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
				translationOverviewOpen && <Box sx={{ border: "1px solid black", borderRadius: "10px", padding: 2, maxHeight: "80%" }}>
					<Typography variant="h3"><FormattedMessage id="dialogs.translations.files.overview" /></Typography>
					<Box display="flex" height="80%" mt={2}>
						{showMissingInCsv && <Box width="50%">
							<Typography sx={{ mb: 2 }} variant="h6"><FormattedMessage id="dialogs.translations.files.missing" /></Typography>
							<ScrollableSection content={overviewData?.missingInCsv} />
						</Box>}
						{showMissingInForm && <Box width="50%">
							<Typography sx={{ mb: 2 }} variant="h6"><FormattedMessage id="dialogs.translations.files.missing.form" /></Typography>
							<ScrollableSection content={overviewData?.missingInForm} />
						</Box>}
						{showSuccessAlert &&
							<Alert severity="success" variant="outlined" sx={{ width: "100%", mb: 2 }}>
								<FormattedMessage id="dialogs.translations.files.success" />
							</Alert>}
						{showError && <Alert severity="error" variant="outlined" sx={{ width: "100%", mb: 2 }}>
							<FormattedMessage id="dialogs.translations.files.error" />
						</Alert>}
					</Box>
					<Box display="flex" justifyContent="flex-end" gap={1}>
						<Button onClick={handleClose} variant='text' color='error'><FormattedMessage id='buttons.cancel' /></Button>
						<Button onClick={handleClick} variant='contained' disabled={!!validationError}><FormattedMessage id='buttons.confirm' /></Button>
					</Box>
				</Box>
			}
		</Box>
	);
};

export { TranslationFileEditor };
