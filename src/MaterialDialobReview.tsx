import React from 'react';
import { DialobContext, DialobContextType } from './context/DialobContext';
import { DEFAULT_ITEM_CONFIG, ItemconfigType } from './defaults/itemConfig';
import { Questionnaire } from './components/Questionnaire';
import messages from './intl';
import { IntlProvider } from 'react-intl';

export interface MaterialDialobReviewProps {
  formData: any;
  sessionData: any;
  title?: string;
  locale?: string;
  itemConfig?: ItemconfigType,
}

export const MaterialDialobReview: React.FC<MaterialDialobReviewProps> = ({ formData, sessionData, title, locale, itemConfig }) => {

  const language = locale || sessionData.metadata.language;
  const documentTitle = title || formData.metadata.label;

  const rootItem = (() => {
    for (let id in formData.data) {
      if (formData.data[id].type === 'questionnaire') return formData.data[id];
    }
    return null;
  })();

  const dialobContext = new DialobContextType(sessionData, formData, language, itemConfig || DEFAULT_ITEM_CONFIG, 'component');

  return (
    <IntlProvider locale={language} key={language} messages={messages[language]}>
      <DialobContext.Provider value={dialobContext}>
        <Questionnaire item={rootItem} title={documentTitle} />
      </DialobContext.Provider>
    </IntlProvider>
  );
}