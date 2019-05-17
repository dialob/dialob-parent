import { Config } from './config';
import { Session } from './session';
import { RESTTransport, Transport } from './transport';

export function newSession(sessionId: string, config: Config): Session {
  let { transport } = config;
  if(!transport) {
    transport = {
      mode: 'rest',
      credentials: 'same-origin',
    };
  }
  let transportObj: Transport;
  switch(transport.mode) {
    case 'rest':
      transportObj = new RESTTransport(config.endpoint, transport);
      break;
    default:
      throw new Error('Unexpected transport mode!');
  }

  const session = new Session(sessionId, transportObj);
  return session;
}

export { Config, Session };
export default { newSession };
