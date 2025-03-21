import React, { PropsWithChildren } from 'react';
import { Typography, Link, Table, TableHead, TableBody, TableRow, TableCell, Divider } from '@mui/material';

import Markdown from 'react-markdown';

interface HeadingRendererProps {
  level: number;
}

interface LinkRendererProps {
  href: string;
}

const HLEVEL_MAPPING: ('h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6')[] = [
  'h1',
  'h2',
  'h3',
  'h4',
  'h5',
  'h6'
];

const HeadingRenderer: React.FC<PropsWithChildren<HeadingRendererProps>> = ({ children, level }) => <Typography variant={HLEVEL_MAPPING[level - 1]} gutterBottom>{children}</Typography>;
const ParagraphRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <Typography variant='body1' paragraph>{children}</Typography>;
const LinkRenderer: React.FC<PropsWithChildren<LinkRendererProps>> = ({ children, href }) => <Link href={href}>{children}</Link>;
const TableRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <Table>{children}</Table>;
const TableHeadRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <TableHead>{children}</TableHead>;
const TableBodyRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <TableBody>{children}</TableBody>;
const TableRowRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <TableRow>{children}</TableRow>;
const TableCellRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <TableCell>{children}</TableCell>;
const DividerRenderer: React.FC<{}> = () => <Divider variant='middle' />;
const ListRenderer: React.FC<PropsWithChildren<{}>> = ({ children }) => <Typography variant='body1'>{children}</Typography>;

const renderers = {
  heading: HeadingRenderer,
  paragraph: ParagraphRenderer,
  link: LinkRenderer,
  table: TableRenderer,
  tableHead: TableHeadRenderer,
  tableBody: TableBodyRenderer,
  tableRow: TableRowRenderer,
  tableCell: TableCellRenderer,
  thematicBreak: DividerRenderer,
  list: ListRenderer,
};

export interface MarkdownViewProps {
  text: string;
}

export const MarkdownView: React.FC<MarkdownViewProps> = ({ text }) => {
  return (
    <Markdown source={text} escapeHtml renderers={renderers} />
  );
}
