import { DialobAdminView, DialobAdminViewProps } from "./DialobAdminView";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { IntlProvider } from "react-intl";
import messages from './intl';

import svLocale from 'date-fns/locale/sv';
import fiLocale from 'date-fns/locale/fi';
import etLocale from 'date-fns/locale/et';
import enLocale from 'date-fns/locale/en-GB';
import msLocale from 'date-fns/locale/ms';

const localeMap: { [key: string]: any } = {
	en: enLocale,
	et: etLocale,
	fi: fiLocale,
	sv: svLocale,
	ms: msLocale,
};

export const DialobAdmin: React.FC<DialobAdminViewProps> = ({ config, showSnackbar }) => {
	return (
		<LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={localeMap[config.language]}>
			<IntlProvider locale={config.language || 'en'} messages={messages[config.language]}>
				<DialobAdminView config={config} showSnackbar={showSnackbar} />
			</IntlProvider>
		</LocalizationProvider>
	);
}
