import React from 'react';
import {ItemconfigType} from '../defaults/itemConfig';

export class DialobContextType {
  private questionnaire: any;
  private form: any;
  private language: string;
  private config: ItemconfigType;
  private componentName: string;

  constructor(questionnaire: any, form: any, language: string, config: ItemconfigType, componentName: string) {
    this.questionnaire = questionnaire;
    this.form = form;
    this.language = language;
    this.config = config;
    this.componentName = componentName;
  }

  private trim(string: string) {
    if (!string) {
      return '-';
    }
    return string;
  }

  private replaceVariable(varName: string) {
    let questionnaireValue = this.questionnaire.context.find(context=>context.id === varName);
    if (questionnaireValue) {
      return questionnaireValue.value;
    }
    questionnaireValue = this.questionnaire.variableValues.find(context=>context.id === varName);
    if (questionnaireValue) {
      return questionnaireValue.value;
    }
    questionnaireValue = this.questionnaire.answers.find(context=>context.id === varName);
    if (questionnaireValue) {
      return questionnaireValue.value;
    }
  }

  public substituteVariables(string: string){
    return string.replace(/\{(.+?)\}/g,(match, p1)=>{return this.trim(this.replaceVariable(p1))});
  }

  public getAnswer(itemId: string, answerId : string | null = null): any {
    let aID = answerId ? answerId : itemId;
    let answer = this.questionnaire.answers.find(e => e.id === aID);
    if (answer) {
      return answer.value;
    }
    return null;
  }

  public getTranslated(value: any): any {
    return (value && value[this.language]) || '';
  }

  public createItem(itemId: string, answerId: string | null = null, isMainGroupItem: boolean = false): JSX.Element | null {
    const item =  this.form.data[itemId];
    let configItem = this.config.items.find(c => c.matcher(item, isMainGroupItem));
    if (!configItem) {
      return null;
    }
    const ComponentType = configItem[this.componentName];
    if (!ComponentType) {
      console.warn(`Component type not defined for ${itemId}[${this.componentName}]`);
      return null;
    }
    if (configItem.answerRequired) {
      const answer = this.getAnswer(item.id, answerId);
      if (answer === null) {
        // skip unanswered
        return null;
      }
      return (<ComponentType key={item.id} item={item} answerId={answerId} answer={answer} />);
    }
    else {
      if (configItem.childrenRequired) {
        const items = item.items ? item.items.map(id => this.createItem(id)).filter(item => item) : null;
        if (!items || items.length === 0) {
          // skip empty
          return null;
        }
        // TODO: Give child items as props
      }
      return (<ComponentType key={item.id} item={item}/>);
    }
  }

  public findValueSet(valueSetId) {
    return this.form.valueSets.find(vs => vs.id === valueSetId);
  }

  public getItem(itemId: string) {
    return this.form.data[itemId];
  }

  public getLanguage() {
    return this.language;
  }

}

const defaultContext: DialobContextType = (undefined as unknown) as DialobContextType;

const DialobContext = React.createContext<DialobContextType>(defaultContext);

export { DialobContext }