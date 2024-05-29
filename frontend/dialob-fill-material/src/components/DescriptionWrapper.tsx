import React, { PropsWithChildren } from 'react';
import { DescriptionProps, Description } from './Description';

export type DescriptionWrapperProps =  PropsWithChildren<DescriptionProps>;

export const DescriptionWrapper: React.FC<DescriptionWrapperProps> = ({title, text, children}) => {
  if (!text) {
    return <>{children}</>;
  } else {
    return (
      <div style={{display: 'flex', alignItems: 'flex-start'}}>
        <div style={{flexGrow: 1}}>
          {children}
        </div>
        <Description title={title} text={text} />
      </div>
    );
  }
};
