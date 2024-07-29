import { DialobAdmin } from './DialobAdmin'

interface CsrfShape {
	'key': string,
	'value': string
}

interface DialobAdminConfig {
	dialobApiUrl: string;
	setLoginRequired: () => void;
	setTechnicalError: () => void;
	language: string;
	csrf: CsrfShape | undefined;
}

export { DialobAdmin, type DialobAdminConfig }
