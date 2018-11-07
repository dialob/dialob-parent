import {confirmationMiddleware} from './confirmationMiddleware';
import {backendMiddleware} from './backendMiddleware';
import {previewMiddleware} from './previewMiddleware';
import {downloadMiddleware} from './downloadMiddleware';
import {extensionMiddleware} from './extensionMiddleware';
import {applyMiddleware} from 'redux';

const middleware = [
  confirmationMiddleware,
  extensionMiddleware,
  backendMiddleware,
  previewMiddleware,
  downloadMiddleware
];

export default function createDialobComposerMiddleware() {
  return middleware;
}
