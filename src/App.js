import React, {Component} from 'react';
import {Grid} from 'semantic-ui-react';
import MainMenu from './components/MainMenu';
import TreeView from './components/TreeView';
import Editor from './components/Editor';

require('./style.css');

class App extends Component {
  render() {
    const marginTop = '42px';
    const menuWidth = '250px';
    return (
      <React.Fragment>
        <MainMenu />
        <div >
          <TreeView marginTop={marginTop} menuWidth={menuWidth} />
          <div style={{marginLeft: menuWidth, marginTop}}>
            <Grid columns='equal'>
              <Grid.Row>
                <Grid.Column>
                  <Editor />
                </Grid.Column>
                <Grid.Column computer={4}>
                </Grid.Column>
              </Grid.Row>
            </Grid>
          </div>
        </div>
      </React.Fragment>);
  }
}

export default App;
