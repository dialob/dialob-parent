import React from 'react';
import Markdown from 'react-markdown';
import { Typography, Link, Divider } from '@mui/material';

const components:object = {
  /* @ts-ignore */
  h1: ({children}) => <Typography variant='h1'>{children}</Typography>,
  /* @ts-ignore */
  h2: ({children}) => <Typography variant='h2'>{children}</Typography>,
  /* @ts-ignore */
  h3: ({children}) => <Typography variant='h3'>{children}</Typography>,
  /* @ts-ignore */
  h4: ({children}) => <Typography variant='h4'>{children}</Typography>,
  /* @ts-ignore */
  h5: ({children}) => <Typography variant='h5'>{children}</Typography>,
  /* @ts-ignore */
  h6: ({children}) => <Typography variant='h6'>{children}</Typography>,
  hr: () => <Divider />,
  /* @ts-ignore */
  p: ({children}) => <Typography variant='body1' paragraph>{children}</Typography>,
  /* @ts-ignore */
  'a': (props) => <Link href={props.href}>{props.children}</Link>
}
export interface MarkdownViewProps {
  text?: string;
}

export const MarkdownView: React.FC<MarkdownViewProps> = ({ text }) => {
  return (
    <Markdown skipHtml components={components} >{text || ''}</Markdown>
  );
}