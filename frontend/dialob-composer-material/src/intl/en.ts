const en = {
  'locales.en': 'English',
  'locales.fi': 'Finnish',
  'locales.sv': 'Swedish',
  'locales.et': 'Estonian',

  'translations': 'Translations',
  'variables': 'Variables',
  'lists': 'Lists',
  'options': 'Options',
  'version': 'Version',
  'version.latest': 'LATEST',
  'search': 'Search',
  'preview': 'Preview',
  'help': 'Help',

  'page.label': 'Page label',

  'buttons.visibility': 'Visibility',
  'buttons.cancel': 'Cancel',
  'buttons.confirm': 'Confirm',
  'buttons.close': 'Close',
  'buttons.delete': 'Delete',
  'buttons.copy.clip': 'Copy to clipboard',

  'menus.options': 'Options',
  'menus.description': 'Description',
  'menus.change.id': 'Change ID',
  'menus.delete': 'Delete',
  'menus.duplicate': 'Duplicate',
  'menus.conversions.hint': 'Convert to:',
  'menus.add': 'Add item',
  'menus.insert.below': 'Insert below',

  'placeholders.group': 'Group label',
  'placeholders.surveygroup': 'Survey group label',
  'placeholders.rowgroup': 'Multi-row group label',
  'placeholders.survey': 'Survey field label',
  'placeholders.address': 'Address field label',
  'placeholders.text': 'Text field label',
  'placeholders.time': 'Time field label',
  'placeholders.date': 'Date field label',
  'placeholders.number': 'Number field label',
  'placeholders.decimal': 'Decimal field label',
  'placeholders.boolean': 'Boolean field label',
  'placeholders.list': 'List field label',
  'placeholders.multichoice': 'Multi-choice field label',
  'placeholders.validation': 'Validation message text',
  'placeholders.note': 'Note text',

  'tooltips.label': 'Label',
  'tooltips.description': 'Description',
  'tooltips.rules': 'Rules',
  'tooltips.validations': 'Validations',
  'tooltips.choices': 'Choices',
  'tooltips.global': 'Global choice list',
  'tooltips.local': 'Local choice list',
  'tooltips.requirement': 'Requirement rule',
  'tooltips.properties': 'Properties',
  'tooltips.default': 'Default value',

  'dialogs.confirmation.delete.title': 'Delete item',
  'dialogs.confirmation.delete.text': 'Are you sure you want to delete {itemId}?',
  'dialogs.confirmation.duplicate.title': 'Duplicate item',
  'dialogs.confirmation.duplicate.text': 'Are you sure you want to duplicate {itemId}?',
  'dialogs.confirmation.convert.title': 'Convert list',
  'dialogs.confirmation.convert.local.text': 'Are you sure you want to convert to a local list?',
  'dialogs.confirmation.convert.global.text': 'Are you sure you want to convert to a global list?',
  'dialogs.confirmation.delete.choice.title': 'Delete choice item',
  'dialogs.confirmation.delete.choice.text': `Are you sure you want to delete "{itemId}" as a choice option?`,

  'dialogs.options.label.add': 'Add label translation',
  'dialogs.options.description.add': 'Add description translation',
  'dialogs.options.key': 'Key',
  'dialogs.options.value': 'Value',
  'dialogs.options.text': 'Text',
  'dialogs.options.rule': 'Rule',
  'dialogs.options.rules.requirement': 'Requirement',
  'dialogs.options.rules.visibility': 'Visibility',
  'dialogs.options.validations.rule.tab': 'Rule {index}',
  'dialogs.options.validations.rule.delete': 'Delete rule',
  'dialogs.options.validations.rule.add': 'Add rule',
  'dialogs.options.validations.add': 'Add message translation',
  'dialogs.options.choices.text.add': 'Add choice translation',
  'dialogs.options.choices.convert.local': 'Convert to local list',
  'dialogs.options.choices.convert.global': 'Convert to global list',
  'dialogs.options.choices.edit.global': 'Edit global list',
  'dialogs.options.choices.create.local': 'Create local list',
  'dialogs.options.choices.select.global': 'Select global list',
  'dialogs.options.choices.divider': 'OR',
  'dialogs.options.properties.add': 'Add property',
  'dialogs.options.default.set': 'Set default value',

  'dialogs.rules.requirement.title': '{itemId}: requirement',
  'dialogs.rules.visibility.title': '{itemId}: visibility',
  'dialogs.rules.validation.title': '{itemId}: validation',
  'dialogs.rules.validation.rule': 'Rule {index}',
  'dialogs.rules.validation.message': 'Message',
  'dialogs.rules.validation.expression': 'Expression',
  'dialogs.rules.error': 'Invalid rule: {rule}',

  'dialogs.text.description.title': '{itemId}: description',
  'dialogs.text.label.title': '{itemId}: label',

  'dialogs.upload.valueset.title': 'Upload value set',
  'dialogs.upload.valueset.file': 'Choose file',
  'dialogs.upload.valueset.replace': 'Replace',
  'dialogs.upload.valueset.replace.desc': 'Replaces all valueset entries with values from file',
  'dialogs.upload.valueset.append': 'Append',
  'dialogs.upload.valueset.append.desc': 'Appends values from file to existing valueset entries',
  'dialogs.upload.valueset.update': 'Update',
  'dialogs.upload.valueset.update.desc': 'Updates existing entries by ID and adds new entries from file',

  'dialogs.translations.title': 'Translations',
  'dialogs.translations.types.label': 'Item label',
  'dialogs.translations.types.description': 'Item description',
  'dialogs.translations.types.valueset': 'Valueset entry',
  'dialogs.translations.types.validation': 'Validation message',
  'dialogs.translations.files.title': 'Manage translation files',
  'dialogs.translations.files.download': 'Download translations as CSV',
  'dialogs.translations.files.upload': 'Upload translations as CSV',
  'dialogs.translations.files.overview': 'Translations overview',
  'dialogs.translations.files.missing': 'Missing from the file',
  'dialogs.translations.files.missing.form': 'Missing from the form',
  'dialogs.translations.files.success': 'No missing translations, ready to apply changes',
  'dialogs.translations.files.error': 'CSV validation error, please re-upload the file with correct data ',
  'dialogs.translations.languages.title': 'Manage languages',
  'dialogs.translations.languages.add': 'Add language',
  'dialogs.translations.languages.add.desc': 'Add a new empty language to the translations',
  'dialogs.translations.languages.language': 'Language',
  'dialogs.translations.languages.delete': 'Delete',
  'dialogs.translations.languages.delete.confirm.title': 'Delete language',
  'dialogs.translations.languages.delete.confirm.desc': `Are you sure you want to delete "{lang}" and all of its translations?`,
  'dialogs.translations.languages.copy': 'Copy as new',
  'dialogs.translations.languages.active': 'Active',
  'dialogs.translations.missing.title': 'Missing translations',
  'dialogs.translations.missing.none': 'No missing translations',

  'dialogs.form.options.title': 'Form options',
  'dialogs.form.options.label': 'Form label',
  'dialogs.form.options.visibility': 'Question visibility during filling',
  'dialogs.form.options.visibility.ONLY_ENABLED': 'Show only active questions',
  'dialogs.form.options.visibility.SHOW_DISABLED': 'Show inactive pages',
  'dialogs.form.options.visibility.ALL': 'Show all questions',
  'dialogs.form.options.visibility.ONLY_ENABLED.desc': 'Only information about active elements is sent to filling side (default).',
  'dialogs.form.options.visibility.SHOW_DISABLED.desc': 'Information about inactive pages is sent to filling side, useful for navigation features.',
  'dialogs.form.options.visibility.ALL.desc': 'Information about all elements is sent to filling side, useful for debugging reasons.',
  'dialogs.form.options.required': 'Answers required by default',
  'dialogs.form.options.required.desc': 'Return {value} from requirement rule to make answer not required.',
  'dialogs.form.options.technical.name': 'Technical name',
  'dialogs.form.options.id': 'Instance ID',
  'dialogs.form.options.created': 'Created',
  'dialogs.form.options.saved': 'Saved',

  'errors.title': 'error',
  'errors.type.VARIABLE': 'Variable',
  'errors.type.VISIBILITY': 'Visibility',
  'errors.type.DEFAULT_VALUE': 'Default value',
  'errors.type.REQUIREMENT': 'Requirement',
  'errors.type.VALIDATION': 'Validation',
  'errors.type.VALUESET': 'List',
  'errors.type.VALUESET_ENTRY': 'List entry',
  'errors.type.CANADDROW': 'Add row',
  'errors.type.CANREMOVEROW': 'Remove row',
  'errors.message.RB_VARIABLE_NEEDS_EXPRESSION': 'Missing expression',
  'errors.message.INVALID_DEFAULT_VALUE': 'Invalid value',
  'errors.message.UNKNOWN_VARIABLE': 'Unknown variable',
  'errors.message.SYNTAX_ERROR': 'Syntax error',
  'errors.message.COULD_NOT_DEDUCE_TYPE': 'Can\'t deduce type ',
  'errors.message.NO_ORDER_RELATION_BETWEEN_TYPES': 'Can\'t compare these variables',
  'errors.message.NO_EQUALITY_RELATION_BETWEEN_TYPES': 'Can\'t compare these variables',
  'errors.message.BOOLEAN_EXPRESSION_EXPECTED': 'Boolean expression expected',
  'errors.message.VALUESET_EMPTY': 'Choice list is empty',
  'errors.message.VALUESET_DUPLICATE_KEY': `Choice list has duplicate key "{expression}"`,
  'errors.message.VALUESET_EMPTY_KEY': 'Choice list has empty key',
  'errors.message.CONTEXT_VARIABLE_UNDEFINED_TYPE': 'Context variable type not defined',
  'errors.message.VALUE_TYPE_NOT_SET': 'Value type not set',
  'errors.message.TAG_EXISTS': 'Tag already exists',
  'errors.message.MATCHER_REGEX_SYNTAX_ERROR': 'Invalid regular expression',
  'errors.message.MATCHER_DYNAMIC_REGEX': 'Dynamic regular expressions not supported',
  'errors.message.REDUCER_TARGET_MUST_BE_REFERENCE': 'Multirow aggregate function target must be directly multirow item',
  'errors.message.CANNOT_USE_REDUCER_INSIDE_SCOPE': 'Multirow aggregate function can\'t be used for non-multirow item',
  'errors.message.UNKNOWN_REDUCER_OPERATOR': 'Unknown multirow aggregate function',
  'errors.message.OPERATOR_CANNOT_REDUCE_TYPE': 'This aggregate function can\'t be used for this item type',
  'errors.message.UNKNOWN_FUNCTION': 'Undefined function',

};

export default en;
