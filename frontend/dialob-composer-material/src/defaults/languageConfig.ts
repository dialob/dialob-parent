interface LanguageConfig {
  code: string,
  name: string,
  flag: string
}

export const DEFAULT_LANGUAGE_CONFIG: LanguageConfig[] = [
  {
    code: 'en',
    name: 'English',
    flag: 'uk'
  },
  {
    code: 'fi',
    name: 'Finnish',
    flag: 'fi'
  },
  {
    code: 'sv',
    name: 'Swedish',
    flag: 'se'
  },
  {
    code: 'et',
    name: 'Estonian',
    flag: 'ee'
  }
];
