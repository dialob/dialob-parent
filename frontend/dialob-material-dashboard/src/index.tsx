interface CsrfShape {
	'key': string,
	'value': string
}

export interface DialobAdminConfig {
	dialobApiUrl: string;
	setLoginRequired: () => void;
	setTechnicalError: () => void;
	language: string;
	csrf: CsrfShape;
}

export { DialobAdmin } from './DialobAdmin';
