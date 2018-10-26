import {confirmationMiddleware} from './confirmationMiddleware';
import {backendMiddleware} from './backendMiddleware';
import {previewMiddleware} from './previewMiddleware';
import {downloadMiddleware} from './downloadMiddleware';
import {applyMiddleware} from 'redux';

const middleware = [
  confirmationMiddleware,
  backendMiddleware,
  previewMiddleware,
  downloadMiddleware
];

export default function createDialobComposerMiddleware() {
  return middleware;
}
