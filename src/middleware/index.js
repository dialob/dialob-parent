import {confirmationMiddleware} from './confirmationMiddleware';
import {backendMiddleware} from './backendMiddleware';

export const middleware = [
  confirmationMiddleware,
  backendMiddleware
];
