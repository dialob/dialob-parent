import React, {Component} from 'react';
import {Menu, List} from 'semantic-ui-react';

class TreeView extends Component {
  render() {
    return (
      <Menu vertical fixed='left' style={{marginTop: this.props.marginTop, width: this.props.menuWidth}}>
        <Menu.Item>
          <List size='small'>
            <List.Item>
              <List.Icon name='folder' style={{float: 'initial'}}/>
              <List.Content>
                <List.Header>page1</List.Header>
                <List.List>
                  <List.Item>
                    <List.Icon name='folder' style={{float: 'initial'}}/>
                    <List.Content>
                      <List.Header>xyz</List.Header>
                    </List.Content>
                  </List.Item>
                </List.List>
              </List.Content>
            </List.Item>
          </List>
        </Menu.Item>
      </Menu>);
  }
}

export default TreeView;
