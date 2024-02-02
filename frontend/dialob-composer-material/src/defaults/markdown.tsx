import { Box, Divider, Link, Typography } from '@mui/material';

export const markdownComponents: object = {
  /* @ts-ignore */
  h1: ({ children }) => <Typography variant='h1'>{children}</Typography>,
  /* @ts-ignore */
  h2: ({ children }) => <Typography variant='h2'>{children}</Typography>,
  /* @ts-ignore */
  h3: ({ children }) => <Typography variant='h3'>{children}</Typography>,
  /* @ts-ignore */
  h4: ({ children }) => <Typography variant='h4'>{children}</Typography>,
  /* @ts-ignore */
  h5: ({ children }) => <Typography variant='h5'>{children}</Typography>,
  /* @ts-ignore */
  h6: ({ children }) => <Typography variant='h6'>{children}</Typography>,
  hr: () => <Divider />,
  /* @ts-ignore */
  p: ({ children }) => <Typography variant='body1' paragraph>{children}</Typography>,
  /* @ts-ignore */
  'a': (props) => <Link href={props.href}>{props.children}</Link>,
  /* @ts-ignore */
  'img': (props) => <Box component="img" alt={props.alt} src={props.src} maxWidth={600} width="100%" padding="8px 0px" />
}
