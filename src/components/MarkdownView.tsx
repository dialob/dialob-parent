import React from 'react';
import Markdown from 'react-markdown';
import { Typography, Link, Table, TableHead, TableBody, TableRow, TableCell, Divider } from '@material-ui/core';

interface HeadingRendererProps {
  level: number;
}

interface LinkRendererProps {
  href: string;
}

const HeadingRenderer: React.FC<HeadingRendererProps> = ({ children, level }) => <Typography variant={level === 1 ? 'h1' : level === 2 ? 'h2' : undefined}>{children}</Typography>;
const ParagraphRenderer: React.FC = ({ children }) => <Typography variant='body1'>{children}</Typography>;
const LinkRenderer: React.FC<LinkRendererProps> = ({ children, href }) => <Link href={href}>{children}</Link>;
const TableRenderer: React.FC = ({ children }) => <Table>{children}</Table>;
const TableHeadRenderer: React.FC = ({ children }) => <TableHead>{children}</TableHead>;
const TableBodyRenderer: React.FC = ({ children }) => <TableBody>{children}</TableBody>;
const TableRowRenderer: React.FC = ({ children }) => <TableRow>{children}</TableRow>;
const TableCellRenderer: React.FC = ({ children }) => <TableCell>{children}</TableCell>;
const DividerRenderer: React.FC = () => <Divider variant='middle' />;

const renderers = {
  heading: HeadingRenderer,
  paragraph: ParagraphRenderer,
  link: LinkRenderer,
  table: TableRenderer,
  tableHead: TableHeadRenderer,
  tableBody: TableBodyRenderer,
  tableRow: TableRowRenderer,
  tableCell: TableCellRenderer,
  thematicBreak: DividerRenderer
};

export interface MarkdownViewProps {
  text?: string;
}

export const MarkdownView: React.FC<MarkdownViewProps> = ({ text }) => {
  return (
    <Markdown source={text} escapeHtml renderers={renderers} />
  );
}