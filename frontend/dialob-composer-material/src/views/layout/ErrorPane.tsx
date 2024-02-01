import React from 'react';
import { DialobItem, useComposer } from '../../dialob';
import { useEditor } from '../../editor';

const ErrorPane: React.FC = () => {
  const { form } = useComposer();
  const { editor } = useEditor();
  const item: DialobItem = Object.values(form.data).find((item) => item.id === 'workRelatedRiskAnalysis')!;
  return (
    <>
      {item.validations?.map((validation) => (
        <>
          <div>{validation?.message!['en']}</div>
          <div>{validation?.message!['fi']}</div>
          <div>{validation?.rule}</div>
          <br />
        </>
      ))}
    </>
  );
};

export default ErrorPane;
