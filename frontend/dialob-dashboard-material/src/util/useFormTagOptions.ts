import { useMemo } from 'react';
import { checkHttpResponse, handleRejection } from '../middleware';
import { useAdminBackend } from '../backend';
import { extractDate, downloadAsJSON, DEFAULT_CONFIGURATION_FILTERS, LabelAction } from '../util';
import type { FormConfiguration, FormConfigurationFilters, DialobAdminConfig, DialobForm } from '../types';

export interface FormTagOptions {
  filteredRow: FormConfigurationFilters | undefined;
  downloadFormConfiguration: () => Promise<void>;
  updateLabels: (label: string, action: LabelAction) => Promise<void>;
}

export interface UseFormTagOptionsParams {
  formConfiguration: FormConfiguration;
  filters: FormConfigurationFilters;
  config: DialobAdminConfig;
  getDialobForm: (formName: string) => Promise<DialobForm | null>;
  setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>;
}

export function useFormTagOptions({
  formConfiguration,
  filters,
  config,
  getDialobForm,
  setFetchAgain
}: UseFormTagOptionsParams): FormTagOptions {

  const { editAdminFormConfiguration } = useAdminBackend(config);

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
        formConfiguration.metadata.labels.forEach((label: string) => {
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

  const updateLabels = async (label: string, action: LabelAction) => {
    const updatedLabels =
      action === LabelAction.DELETE
        ? formConfiguration.metadata.labels.filter((l: string) => l !== label)
        : [...(formConfiguration.metadata.labels || []), label];

    const form = await getDialobForm(formConfiguration.id);
    if (!form) {
      handleRejection(new Error('Form not found'), config.setTechnicalError);
      return;
    }
    delete form._id;
    delete form._rev;

    const json = {
      ...form,
      metadata: {
        ...form.metadata,
        labels: updatedLabels,
      },
    };

    try {
      const response = await editAdminFormConfiguration(json);
      await checkHttpResponse(response, config.setLoginRequired);
      await response.json();
      setFetchAgain(prevState => !prevState);
    } catch (ex: unknown) {
      handleRejection(ex, config.setTechnicalError);
    }
  };

  return { filteredRow, downloadFormConfiguration, updateLabels };
}