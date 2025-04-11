import { DialobAdminView, DialobAdminViewProps } from "./DialobAdminView";
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { IntlProvider } from "react-intl";
import messages from './intl';

import {
  sv as svLocale,
  fi as fiLocale,
  et as etLocale,
  enGB as enLocale,
  ms as msLocale
} from 'date-fns/locale';

const localeMap: { [key: string]: any } = {
  en: enLocale,
  et: etLocale,
  fi: fiLocale,
  sv: svLocale,
  ms: msLocale,
};

export const DialobAdmin: React.FC<DialobAdminViewProps> = ({ config, showNotification }) => {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={localeMap[config.language]}>
      <IntlProvider locale={config.language || 'en'} messages={messages[config.language]}>
        <DialobAdminView config={config} showNotification={showNotification} />
      </IntlProvider>
    </LocalizationProvider>
  );
}
