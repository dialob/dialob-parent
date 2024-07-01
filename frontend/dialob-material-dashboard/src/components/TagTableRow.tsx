import React, { useEffect, useMemo, useState } from 'react'
import EditIcon from '@mui/icons-material/Edit';
import CloseIcon from '@mui/icons-material/Close';
import { Theme, Tooltip } from '@mui/material';
import { StyledTableCell, StyledTableRow, StyledIcon, StyledIconButton } from '../style';
import { checkHttpResponse, handleRejection } from '../middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters, FormConfigurationTag } from '../types';
import { getAdminFormConfigurationTags } from '../backend';
import { useIntl } from 'react-intl';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { dateOptions } from '../util/constants';
import { downloadAsJSON, extractDate } from '../util/helperFunctions';
import DownloadIcon from '@mui/icons-material/Download';
import { DialobAdminConfig } from '..';

interface TagTableRowProps {
	filters: FormConfigurationFilters;
	formConfiguration: FormConfiguration;
	deleteFormConfiguration: (formConfiguration: FormConfiguration) => void;
	copyFormConfiguration: (formConfiguration: FormConfiguration) => void;
	dialobForm: any;
	config: DialobAdminConfig;
}

export const TagTableRow: React.FC<TagTableRowProps> = ({
	filters,
	formConfiguration,
	copyFormConfiguration,
	deleteFormConfiguration,
	dialobForm,
	config
}) => {
	const [tags, setTags] = useState<FormConfigurationTag[]>([]);
	const intl = useIntl();

	useEffect(() => {
		getAdminFormConfigurationTags(config, formConfiguration.id)
			.then((response: Response) => checkHttpResponse(response, config.setLoginRequired))
			.then((response: { json: () => any; }) => response.json())
			.then((tags: any) => {
				setTags(tags?.map((tag: any) => {
					return {
						latestTagDate: tag.created,
						latestTagName: tag.name
					}
				}))
			})
			.catch((ex: any) => {
				handleRejection(ex, config.setTechnicalError);
			});
	},
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[])

	const latestTag: FormConfigurationTag | undefined = useMemo(() => {
		if (tags.length === 0) {
			return undefined;
		} else {
			tags.sort((a, b) => { return new Date(b.latestTagDate).getTime() - new Date(a.latestTagDate).getTime() });
			return tags[0];
		}
	}, [tags])

	const filteredRow: FormConfigurationFilters | undefined = useMemo(() => {
		const result: FormConfigurationFilters = {
			...DEFAULT_CONFIGURATION_FILTERS,
			lastSaved: formConfiguration.metadata.lastSaved,
			label: formConfiguration.metadata.label || "",
			latestTagName: latestTag?.latestTagName || "",
			latestTagDate: latestTag?.latestTagDate
		};

		if (filters.label && !result.label?.toLowerCase().includes(filters.label.toLowerCase())) {
			return undefined;
		}

		if (filters.latestTagName && !result.latestTagName?.toLowerCase().includes(filters.latestTagName.toLowerCase())) {
			return undefined;
		}

		if (filters.latestTagDate) {
			if (!latestTag) {
				return undefined;
			} else {
				const filterDate = extractDate(filters.latestTagDate);
				const tagDate = extractDate(latestTag?.latestTagDate);
				if (filterDate && tagDate && filterDate.getTime() !== tagDate.getTime()) {
					return undefined;
				}
			}
		}

		if (filters.lastSaved) {
			const filterDate = extractDate(filters.lastSaved);
			const savedDate = extractDate(formConfiguration.metadata.lastSaved);
			if (filterDate && savedDate && filterDate.getTime() !== savedDate.getTime()) {
				return undefined;
			}
		}

		return result;
	}, [filters, formConfiguration.metadata.label, formConfiguration.metadata.lastSaved, latestTag]);

	const downloadFormConfiguration = () => {
		downloadAsJSON(dialobForm);
	}

	return (
		<>
			{filteredRow && (
				<StyledTableRow key={formConfiguration.id}>
					<StyledTableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.edit" })} placement='top-end' arrow>
							<StyledIconButton
								onClick={function (e: any) {
									e.preventDefault();
									window.location.replace(`${config.dialobApiUrl}/composer/${formConfiguration.id}`);
								}}
							>
								<StyledIcon fontSize="small"><EditIcon /></StyledIcon>
							</StyledIconButton>
						</Tooltip>
					</StyledTableCell>
					<StyledTableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.copy" })} placement='top-end' arrow>
							<StyledIconButton
								onClick={function (e: any) {
									e.preventDefault();
									copyFormConfiguration(formConfiguration)
								}}
							>
								<StyledIcon fontSize="small"><ContentCopyIcon /></StyledIcon>
							</StyledIconButton>
						</Tooltip>
					</StyledTableCell>
					<StyledTableCell>{formConfiguration.metadata.label || intl.formatMessage({ id: "adminUI.dialog.emptyTitle" })}</StyledTableCell>
					<StyledTableCell>{latestTag?.latestTagName}</StyledTableCell>
					<StyledTableCell>{latestTag && new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(latestTag.latestTagDate))}</StyledTableCell>
					<StyledTableCell>{new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(formConfiguration.metadata.lastSaved))}</StyledTableCell>
					<StyledTableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.delete" })} placement='top-end' arrow>
							<StyledIconButton
								onClick={function (e: any) {
									e.preventDefault();
									deleteFormConfiguration(formConfiguration);
								}}
								sx={{ '&:hover': { backgroundColor: (theme: Theme) => theme.palette.error.main } }}
							>
								<StyledIcon fontSize="small"><CloseIcon /></StyledIcon>
							</StyledIconButton>
						</Tooltip>
					</StyledTableCell>
					<StyledTableCell sx={{ textAlign: "center" }}>
						<Tooltip title={intl.formatMessage({ id: "download" })} placement='top-end' arrow>
							<StyledIconButton
								onClick={function (e: any) {
									e.preventDefault();
									downloadFormConfiguration();
								}}
							>
								<StyledIcon fontSize="small"><DownloadIcon /></StyledIcon>
							</StyledIconButton>
						</Tooltip>
					</StyledTableCell>
				</StyledTableRow>
			)}
		</>
	);
}
