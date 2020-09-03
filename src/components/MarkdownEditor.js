import React, {useState, useRef} from 'react';
import { TextArea, Segment, Button, Icon, Popup, Grid, Header } from 'semantic-ui-react';
import Markdown from 'react-markdown';
import debounce from 'lodash.debounce';

const MD_HELP = `
  # Heading 1
  ## Heading 2
  ### Heading 3

  normal **bold** *italic* 
  \`code\` ~~striketrhough~~ 

  [link](http://example.com)

  * list
  * list
    * list
  
  ---
    
  1. list
  1. list
    1. list

  | header | header |
  | ------ | ------ |
  | column | column |
  
`;

export const MarkdownEditor = ({value, onChange}) => {
  const [preview, setPreview] = useState(false);
  const [content, setContent] = useState(value);
  const handleChange = useRef(debounce(value => {
    if (onChange) {
      onChange(value);
    }
  }, 250)).current;

  const updateValue = (value) => {
    setContent(value);
    handleChange(value);
  }

  return (
    <Segment.Group>
      <Segment padded={false} className='composer-md-editor-toolbar' basic>
        <Button.Group icon size='mini'>
          <Popup trigger={
          <Button active={!preview} onClick={() => setPreview(false)}>
            <Icon name='code'/>
          </Button>
          } on='hover' content='Edit markdown' />
          <Popup trigger={
         <Button active={preview} onClick={() => setPreview(true)}>
            <Icon name='eye'/>
          </Button>
          } on='hover' content='Preview' />
        </Button.Group>
        <Button.Group floated='right' icon size='mini'>
          <Popup trigger={
          <Button>
            <Icon name='help' />
          </Button>
          } on='click' position='bottom right' flowing>
            <Grid columns={2} divided>
              <Grid.Row>
                <Grid.Column><Header as='h3'> MarkDown</Header></Grid.Column>
                <Grid.Column><Header as='h3'>Result</Header></Grid.Column>
              </Grid.Row>
              <Grid.Row>
                <Grid.Column>
                  <p style={{whiteSpace: 'pre', fontFamily: 'monospace'}}>
                    {MD_HELP}
                  </p>
                </Grid.Column>
                <Grid.Column>
                  <Markdown source={MD_HELP} escapeHtml />
                </Grid.Column>
              </Grid.Row>
             </Grid>
          </Popup>
        </Button.Group>
      </Segment>
      <Segment className='composer-md-editor-content'>
        {
           preview
            ?
              <Markdown source={content} escapeHtml />
            :
              <TextArea value={content} onChange={(e) => updateValue(e.target.value)} className='composer-md-editor'/>
        }
      </Segment>
    </Segment.Group>
  );
}