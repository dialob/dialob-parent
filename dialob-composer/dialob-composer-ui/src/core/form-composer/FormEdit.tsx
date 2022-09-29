import React from 'react';
import { Box } from '@mui/material';

import { Client, Composer } from '../context';





const FormEdit: React.FC<{ form: Client.Form }> = ({ form }) => {
  const { session, actions, service } = Composer.useComposer();
  const { site } = session;
  
  const formId = form.id;
  
  React.useEffect(() => {
    //service.ast(flowId, commands).then(data => setAst(data.ast));
  }, [formId, service])


  return (<Box height="100%">
  form composer here...
  </Box>);
}

export { FormEdit };
