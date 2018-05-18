import {confirmationMiddleware} from './confirmationMiddleware';
import {backendMiddleware} from './backendMiddleware';
import {previewMiddleware} from './previewMiddleware';
import {downloadMiddleware} from './downloadMiddleware';

export const middleware = [
  confirmationMiddleware,
  backendMiddleware,
  previewMiddleware,
  downloadMiddleware
];
