import React from 'react';
import Markdown, { type Components } from 'react-markdown';
import { Typography, Link, Divider, Box } from '@mui/material';

const components: Components = {
  h1: ({ children }) => <Typography variant='h1'>{children}</Typography>,
  h2: ({ children }) => <Typography variant='h2'>{children}</Typography>,
  h3: ({ children }) => <Typography variant='h3'>{children}</Typography>,
  h4: ({ children }) => <Typography variant='h4'>{children}</Typography>,
  h5: ({ children }) => <Typography variant='h5'>{children}</Typography>,
  h6: ({ children }) => <Typography variant='h6'>{children}</Typography>,
  hr: () => <Divider />,
  p: ({ children }) => <Typography variant='body1' paragraph>{children}</Typography>,
  'a': (props) => <Link href={props.href}>{props.children}</Link>,
  'img': (props) => <Box component="img" alt={props.alt} src={props.src} maxWidth={600} width="100%" padding="8px 0px" />
}
export interface MarkdownViewProps {
  text?: string;
}

export const MarkdownView: React.FC<MarkdownViewProps> = ({ text }) => {
  return (
    <Markdown skipHtml components={components} >{text || ''}</Markdown>
  );
}
