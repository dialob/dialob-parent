import { useBackend } from "../backend/useBackend";
import { OptionsTabType } from "../editor";

export type DocsType = OptionsTabType | 'options' | 'lists' | 'translations' | 'valuesets' | 'variables' | 'versioning' | 'tagging' | 'del' | 'general';

export const useDocs = (type: DocsType): string => {
  const { config } = useBackend();
  const docsBaseUrl = config.documentationUrl;

  if (docsBaseUrl && !docsBaseUrl.includes('dialob-parent/wiki')) {
    return docsBaseUrl;
  }

  const baseUrl = docsBaseUrl || 'https://github.com/dialob/dialob-parent/wiki/';

  switch (type) {
    case 'id':
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#unique-identifiers';
    case 'label':
      return 'https://www.markdownguide.org/basic-syntax/';
    case 'description':
      return 'https://www.markdownguide.org/basic-syntax/';
    case 'rules':
      return baseUrl + 'Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL#visibility-and-requirement-rules';
    case 'validations':
      return baseUrl + 'Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL#validation-rules';
    case 'choices':
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#creating-a-local-list';
    case 'properties':
      return baseUrl + 'Dialob-composer:-08-customization';
    case 'del':
      return baseUrl + 'Dialob-composer:-05%E2%80%90Dialob-Expression-Language-%E2%80%93-DEL';
    case 'options':
      return baseUrl + 'Dialob-composer:-06%E2%80%90Options-and-settings#dialog-options';
    case "lists":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#lists';
    case "translations":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#localisation';
    case "valuesets":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#what-is-a-valueset';
    case "variables":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#custom-variables-and-expressions';
    case "versioning":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#lifecycle-management';
    case "tagging":
      return baseUrl + 'Dialob-composer:-03%E2%80%90Advanced-operations#how-to-create-a-tag';
    case "general":
      return baseUrl;
    default:
      return baseUrl;
  }
}