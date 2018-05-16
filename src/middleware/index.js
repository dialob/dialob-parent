import {confirmationMiddleware} from './confirmationMiddleware';
import {backendMiddleware} from './backendMiddleware';
import {previewMiddleware} from './previewMiddleware';

export const middleware = [
  confirmationMiddleware,
  backendMiddleware,
  previewMiddleware
];
