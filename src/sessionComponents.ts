import { SessionQuestionnaire, SessionValueSet } from '@resys/dialob-fill-api';
import { ItemActionBoolean, ItemActionGroup, ItemActionList, ItemActionMultiChoice, ItemActionNote, ItemActionNumber, ItemActionSurvey, ItemActionSurveyGroup, ItemActionText } from '@resys/dialob-fill-api/lib/actions';

export interface SessionComponents {
  Questionnaire: React.ComponentType<{ questionnaire?: SessionQuestionnaire }>;
  Group: React.ComponentType<{ group: ItemActionGroup }>;
  Text: React.ComponentType<{ text: ItemActionText }>;
  Multichoice: React.ComponentType<{ multichoice: ItemActionMultiChoice, valueSet: SessionValueSet }>;
  List: React.ComponentType<{ list: ItemActionList, valueSet: SessionValueSet }>;
  Boolean: React.ComponentType<{ boolean: ItemActionBoolean }>;
  Number: React.ComponentType<{ number: ItemActionNumber }>;
  SurveyGroup: React.ComponentType<{ surveyGroup: ItemActionSurveyGroup, valueSet: SessionValueSet }>;
  Survey: React.ComponentType<{ survey: ItemActionSurvey, valueSet: SessionValueSet }>;
  Note: React.ComponentType<{ note: ItemActionNote }>;
}
