import { Box, Divider, Link, Typography } from '@mui/material';

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
  'img': (props: { alt: string, src: string }) => <Box component="img" alt={props.alt} src={props.src} maxWidth={600} width="100%" padding="8px 0px" />
}
