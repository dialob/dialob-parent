import { ServiceErrorMsg, ServiceErrorProps } from './api';

const getErrorMsg = (error: any) => {
  if (error.msg) {
    return error.msg;
  }
  if (error.value) {
    return error.value
  }
  if (error.message) {
    return error.message;
  }
}
const getErrorId = (error: any) => {
  if (error.id) {
    return error.id;
  }
  if (error.code) {
    return error.code
  }
  return "";
}
const parseErrors = (props: any[]): ServiceErrorMsg[] => {
  if (!props) {
    return []
  }

  const result: ServiceErrorMsg[] = props.map(error => ({
    id: getErrorId(error),
    value: getErrorMsg(error)
  }));

  return result;
}

export interface StoreError extends Error {
  text: string;
  status: number;
  errors: ServiceErrorMsg[];
}


export class StoreErrorImpl extends Error {
  private _props: ServiceErrorProps;
  constructor(props: ServiceErrorProps) {
    super(props.text);
    this._props = {
      text: props.text,
      status: props.status,
      errors: parseErrors(props.errors)
    };
  }
  get name() {
    return this._props.text;
  }
  get status() {
    return this._props.status;
  }
  get errors() {
    return this._props.errors;
  }
}