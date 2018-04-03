import React, {Component} from 'react';
import {Container, Menu, Grid, Segment, List, Sticky, Table, Input, Tab, Header, Button, Label, Icon, Dropdown, Popup} from 'semantic-ui-react';
import MainMenu from './components/MainMenu';
import TreeView from './components/TreeView';
import AddItemMenu from './components/AddItemMenu';
import Properties from './components/Properties';

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
                  <Segment basic>
                    <Menu tabular>
                      <Menu.Item active>
                        Page 1
                      </Menu.Item>
                      <Menu.Item>
                        Page 2
                      </Menu.Item>
                      <Menu.Menu position='right'>
                        <Menu.Item>
                          <Button icon='add'/>
                        </Menu.Item>
                      </Menu.Menu>
                    </Menu>

                    <Input fluid icon='square outline' iconPosition='left' placeholder='Group title' className='top attached' />
                    <Segment attached='bottom' raised>
                      <Input fluid icon='square outline' iconPosition='left' placeholder='Group title' className='top attached' />
                      <Segment attached='bottom' raised>
                        <p><Input fluid icon='font' iconPosition='left' placeholder='Text label' /></p>
                        <p><Input fluid icon='calendar' iconPosition='left' placeholder='Date label' /></p>
                        <p><Input fluid icon='caret down' iconPosition='left' placeholder='Choice label' /></p>
                        <AddItemMenu />
                      </Segment>
                      <AddItemMenu />
                    </Segment>
                    <AddItemMenu />
                  </Segment>

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
