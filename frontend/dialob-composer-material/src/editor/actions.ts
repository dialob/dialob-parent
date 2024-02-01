import { DialobItem } from "../dialob";

export type EditorAction =
  | { type: 'setActivePage', page: DialobItem }
  | { type: 'setActiveFormLanguage', language: string }
