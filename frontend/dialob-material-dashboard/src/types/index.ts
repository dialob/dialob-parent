export interface FormConfiguration {
	id: string;
	metadata: Metadata;
}

export interface FormConfigurationFilters {
	label: string | undefined;
	latestTagDate: Date | undefined;
	lastSaved: Date | undefined;
	latestTagName: string | undefined;
}

export interface FormConfigurationTag {
	latestTagName: string;
	latestTagDate: Date;
}

export interface Metadata {
	label?: string,
	created: Date,
	lastSaved: Date,
	tenantId: string,
	modified: string,
	modifiedBy: string
}

export interface DefaultForm {
	name: string;
	data: FormData;
	metadata: FormMetadata;
}

export interface FormData {
	questionnaire: FormQuestionnaire
}

export interface FormQuestionnaire {
	id: string;
	type: string;
	items: any
}

export interface FormMetadata {
	label: string;
	languages: string[]
}

// Builtin structure for minimum form data
export const DEFAULT_FORM: DefaultForm = {
	name: '',
	data: {
		questionnaire: {
			id: 'questionnaire',
			type: 'questionnaire',
			items: []
		}
	},
	metadata: {
		label: '',
		languages: [
			'fi',
			'en'
		]
	}
};

export const DEFAULT_CONFIGURATION_FILTERS: FormConfigurationFilters = {
	label: undefined,
	latestTagDate: undefined,
	lastSaved: undefined,
	latestTagName: undefined
}
