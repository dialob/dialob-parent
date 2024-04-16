import React from "react";
import { FormattedMessage } from "react-intl";

export const BoldedMessage: React.FC<{ id: string, values?: any }> = ({ id, values }) => {
  return (
    <FormattedMessage id={id}
      values={{
        ...values,
        b: (text: string) => <strong>{text}</strong>
      }}
    />
  );
}
