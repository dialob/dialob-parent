import { SessionConfig } from './config';
import { DialobError, DialobRequestError } from './error';
import { Session, type SessionOptions } from './session';
import type { SessionError, SessionItem, SessionValueSet } from './state';
import { RESTTransport, Transport } from './transport';

export function newSession(sessionId: string, config: SessionConfig, options?: SessionOptions): Session {
  let { transport } = config;
  if (!transport) {
    transport = {
      mode: 'rest',
      credentials: 'same-origin',
    };
  }
  let transportObj: Transport;
  switch (transport.mode) {
    case 'rest':
      transportObj = new RESTTransport(config.endpoint, transport);
      break;
    default:
      throw new Error('Unexpected transport mode!');
  }

  const session = new Session(sessionId, transportObj, options);
  return session;
}

export * from './actions';
export {
  type SessionConfig as Config,
  Session,
  type SessionError,
  type SessionItem,
  type SessionValueSet,
  type SessionOptions,
  DialobError,
  DialobRequestError
};
export default { newSession };
