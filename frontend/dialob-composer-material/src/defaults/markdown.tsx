import React from 'react';
import { Box, Divider, Link, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Alert } from '@mui/material';

export const markdownComponents: object = {
  h1: ({ children }: { children: React.ReactNode }) => <Typography variant='h1'>{children}</Typography>,
  h2: ({ children }: { children: React.ReactNode }) => <Typography variant='h2'>{children}</Typography>,
  h3: ({ children }: { children: React.ReactNode }) => <Typography variant='h3'>{children}</Typography>,
  h4: ({ children }: { children: React.ReactNode }) => <Typography variant='h4'>{children}</Typography>,
  h5: ({ children }: { children: React.ReactNode }) => <Typography variant='h5'>{children}</Typography>,
  h6: ({ children }: { children: React.ReactNode }) => <Typography variant='h6'>{children}</Typography>,
  hr: () => <Divider />,
  p: ({ children }: { children: React.ReactNode }) => <Typography variant='body1' paragraph>{children}</Typography>,
  'a': (props: { href: string, children: React.ReactNode }) => <Link href={props.href}>{props.children}</Link>,
  'img': (props: { alt: string, src: string }) => <Box component="img" alt={props.alt} src={props.src} maxWidth={600} width="100%" padding="8px 0px" />,
  table: ({ children }: { children: React.ReactNode }) => (
    <TableContainer component={Paper} sx={{ my: 2 }}>
      <Table children={children} />
    </TableContainer>
  ),
  thead: ({ children }: { children: React.ReactNode }) => <TableHead children={children} />,
  tbody: ({ children }: { children: React.ReactNode }) => <TableBody children={children} />,
  tr: ({ children }: { children: React.ReactNode }) => <TableRow children={children} />,
  th: ({ children }: { children: React.ReactNode }) => (
    <TableCell children={children} sx={{ fontWeight: "bold", backgroundColor: "grey.100" }} />
  ),
  td: ({ children }: { children: React.ReactNode }) => <TableCell children={children} />,
  pre: ({ children }: { children: React.ReactNode }) => (
    <Box
      component="pre"
      sx={{
        backgroundColor: "grey.800",
        color: "grey.100",
        p: 1,
        borderRadius: 2,
        overflowX: "auto",
        m: 2,
        fontFamily: "monospace",
        'code': {
          backgroundColor: 'inherit'
        }
      }}
    >
      {children}
    </Box>
  ),
  code: ({ children }: { children: React.ReactNode }) => (
    <Box
      component="code"
      sx={{
        fontFamily: "monospace",
        px: 0.5,
        borderRadius: 1,
        color: "white",
        backgroundColor: "grey.600"
      }}
    >
      {children}
    </Box>
  ),
  blockquote: ({ children }: { children: React.ReactNode }) => {
    const rawText = React.Children.toArray(children)
      .map((child) => {
        if (typeof child === "string") return child;
        if (React.isValidElement(child)) return React.Children.toArray(child.props.children).join("");
        return "";
      })
      .join("")
      .trim();

    // Match patterns like [!NOTE], [!TIP], [!WARNING], [!ERROR]
    const match = rawText.match(/^\[!(\w+)\]\s*(.*)/);

    if (match) {
      const [, type, body] = match;

      // Map callout types to MUI severities
      const severityMap: Record<string, "info" | "success" | "warning" | "error"> = {
        NOTE: "info",
        TIP: "success",
        WARNING: "warning",
        ERROR: "error",
      };

      return (
        <Alert severity={severityMap[type] || "info"} sx={{ m: 2 }}>
          {body}
        </Alert>
      );
    }

    // Default blockquote
    return (
      <Box
        component="blockquote"
        sx={{
          borderLeft: 1,
          borderColor: "grey.400",
          pl: 2,
          my: 2,
          color: "grey.700",
          fontStyle: "italic",
        }}
      >
        {children}
      </Box>
    );
  },
}
