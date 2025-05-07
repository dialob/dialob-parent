import React, { useMemo } from 'react'
import CloseIcon from '@mui/icons-material/Close';
import { Box, IconButton, Link, SvgIcon, TableCell, TableRow, Tooltip } from '@mui/material';
import { checkHttpResponse, handleRejection } from '../middleware/checkHttpResponse';
import { DEFAULT_CONFIGURATION_FILTERS, FormConfiguration, FormConfigurationFilters, LabelAction } from '../types';
import { editAdminFormConfiguration } from '../backend';
import { useIntl } from 'react-intl';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { dateOptions } from '../util/constants';
import { downloadAsJSON, extractDate } from '../util/helperFunctions';
import DownloadIcon from '@mui/icons-material/Download';
import { DialobAdminConfig } from '..';
import { LabelChips } from './LabelChips';

interface TagTableRowProps {
  filters: FormConfigurationFilters;
  formConfiguration: FormConfiguration;
  deleteFormConfiguration: (formConfiguration: FormConfiguration) => void;
  copyFormConfiguration: (formConfiguration: FormConfiguration) => void;
  config: DialobAdminConfig;
  getDialobForm: (formName: string) => Promise<any>;
  setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
}

export const TagTableRow: React.FC<TagTableRowProps> = ({
  filters,
  formConfiguration,
  copyFormConfiguration,
  deleteFormConfiguration,
  config,
  getDialobForm,
  setFetchAgain
}) => {
  const intl = useIntl();
  const tenantParam = config.tenantId ? `?tenantId=${config.tenantId}` : "";

  const filteredRow: FormConfigurationFilters | undefined = useMemo(() => {
    const result: FormConfigurationFilters = {
      ...DEFAULT_CONFIGURATION_FILTERS,
      lastSaved: formConfiguration.metadata.lastSaved,
      label: formConfiguration.metadata.label || "",
      latestTagName: formConfiguration?.latestTagName || "",
      latestTagDate: formConfiguration?.latestTagDate,
      labels: ""
    };

    if (filters.label && !result.label?.toLowerCase().includes(filters.label.toLowerCase())) {
      return undefined;
    }

    if (filters.latestTagName && !result.latestTagName?.toLowerCase().includes(filters.latestTagName.toLowerCase())) {
      return undefined;
    }

    if (filters.latestTagDate) {
      if (!formConfiguration?.latestTagDate) {
        return undefined;
      } else {
        const filterDate = extractDate(filters.latestTagDate);
        const tagDate = extractDate(formConfiguration?.latestTagDate);
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

    if (filters.labels) {
      let exists = false;
      if (formConfiguration.metadata.labels) {
        formConfiguration.metadata.labels.forEach((label: any) => {
          if (label.toLowerCase().includes(filters.labels!.toLowerCase())) {
            exists = true;
          }
        })
      }
      if (!exists) {
        return undefined;
      }
    }

    return result;
  }, [filters, formConfiguration.metadata.label, formConfiguration.metadata.lastSaved, formConfiguration?.latestTagDate, formConfiguration?.latestTagName]);

  const downloadFormConfiguration = async () => {
    try {
      const form = await getDialobForm(formConfiguration.id);
      downloadAsJSON(form);
    } catch (error) {
      console.error("Error fetching the form:", error);
    }
  }

  const updateLabels = async (label: any, action: LabelAction) => {
    const updatedLabels =
      action === LabelAction.DELETE
        ? formConfiguration.metadata.labels.filter((l: any) => l !== label)
        : [...(formConfiguration.metadata.labels || []), label];

    const form = await getDialobForm(formConfiguration.id);
    const json = {
      ...form,
      metadata: {
        ...form.metadata,
        labels: updatedLabels,
      },
    };
    delete json._id;
    delete json._rev;

    try {
      const response = await editAdminFormConfiguration(json, config);
      await checkHttpResponse(response, config.setLoginRequired);
      await response.json();
      setFetchAgain(prevState => !prevState);
    } catch (ex: any) {
      handleRejection(ex, config.setTechnicalError);
    }
  };

  return (
    <>
      {filteredRow && (
        <TableRow>
          <TableCell>
            <Link
              target="_blank"
              rel="noopener noreferrer"
              href={`${config.dialobApiUrl}/composer/${formConfiguration.id}${tenantParam}`}
              onClick={function (e) {
                e.preventDefault();
                window.location.replace(`${config.dialobApiUrl}/composer/${formConfiguration.id}${tenantParam}`);
              }}
            >
              {formConfiguration.metadata.label || intl.formatMessage({ id: "adminUI.dialog.emptyTitle" })}
            </Link>
          </TableCell>
          <TableCell>{formConfiguration?.latestTagName}</TableCell>
          <TableCell>{formConfiguration?.latestTagDate && new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(formConfiguration?.latestTagDate))}</TableCell>
          <TableCell>{new Intl.DateTimeFormat(config.language, dateOptions).format(new Date(formConfiguration.metadata.lastSaved))}</TableCell>
          <TableCell>
            <LabelChips labels={formConfiguration.metadata.labels} onUpdate={updateLabels} />
          </TableCell>
          <TableCell sx={{ textAlign: "center" }}>
            <Box display="flex">
              <Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.copy" })} placement='top-end' arrow>
                <IconButton
                  onClick={function (e) {
                    e.preventDefault();
                    copyFormConfiguration(formConfiguration)
                  }}
                >
                  <SvgIcon fontSize="small"><ContentCopyIcon /></SvgIcon>
                </IconButton>
              </Tooltip>
              <Tooltip title={intl.formatMessage({ id: "adminUI.table.tooltip.delete" })} placement='top-end' arrow>
                <IconButton
                  onClick={function (e) {
                    e.preventDefault();
                    deleteFormConfiguration(formConfiguration);
                  }}
                  color='error'
                >
                  <SvgIcon fontSize="small"><CloseIcon /></SvgIcon>
                </IconButton>
              </Tooltip>
              <Tooltip title={intl.formatMessage({ id: "download" })} placement='top-end' arrow>
                <IconButton
                  onClick={function (e) {
                    e.preventDefault();
                    downloadFormConfiguration();
                  }}
                >
                  <SvgIcon fontSize="small"><DownloadIcon /></SvgIcon>
                </IconButton>
              </Tooltip>
            </Box>
          </TableCell>
        </TableRow>
      )}
    </>
  );
}
