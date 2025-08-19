import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { IntlProvider } from "react-intl";
import { DialobAdminView, messages, DialobAdminProps, DialobDashboardFetchProvider, DialobDashboardStateProvider } from '@dialob/dashboard-material';

import {
  sv as svLocale,
  fi as fiLocale,
  et as etLocale,
  enGB as enLocale,
  ms as msLocale
} from 'date-fns/locale';

const localeMap: { [key: string]: Locale } = {
  en: enLocale,
  et: etLocale,
  fi: fiLocale,
  sv: svLocale,
  ms: msLocale,
};

export const DialobAdmin: React.FC<DialobAdminProps> = ({ config, showNotification, onOpenForm }) => {
  return (
    <DialobDashboardFetchProvider>
      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={localeMap[config.language]}>
        <IntlProvider locale={config.language || 'en'} messages={messages[config.language]}>
          <DialobDashboardStateProvider config={config} showNotification={showNotification} onOpenForm={onOpenForm}>
            <DialobAdminView />
          </DialobDashboardStateProvider>
        </IntlProvider>
      </LocalizationProvider>
    </DialobDashboardFetchProvider>
  );
}
