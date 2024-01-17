import Editor from '../components/Editor';

interface ValueSetProp {
  title: string,
  name: string,
  editor: React.FC
}

export const DEFAULT_VALUESET_PROPS: ValueSetProp[] = [
  {
    title: 'Custom attribute',
    name: 'attr',
    editor: Editor,
  }
];
