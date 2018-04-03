import React, {Component} from 'react';
import {Container, Menu, Grid, Segment, List, Sticky, Table, Input, Tab, Header, Button, Label, Icon, Dropdown, Popup} from 'semantic-ui-react';
import MainMenu from './components/MainMenu';
import TreeView from './components/TreeView';
import Properties from './components/Properties';
import Editor from './components/Editor';

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
                <Grid.Column computer={5}>
                  <Sticky offset={50}>
                    <Properties />
                  </Sticky>
                </Grid.Column>
              </Grid.Row>
            </Grid>
          </div>
        </div>
      </React.Fragment>);
  }
}

export default App;
