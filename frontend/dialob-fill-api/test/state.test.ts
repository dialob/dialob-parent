import { updateState, initState } from '../src/state';

describe('updateState', () => {
  it('pools errors together under same id', () => {
    let state = initState();
    state = updateState(state, [
      {
        type: 'ERROR',
        error: {
          id: 'item1',
          code: 'some_validation',
          description: 'Some validation'
        }
      },
      {
        type: 'ERROR',
        error: {
          id: 'item1',
          code: 'other_validation',
          description: 'Other validation'
        }
      }
    ]);

    expect(state.errors['item1'].length).toBe(2);
  });

  it('replaces error if one with the same id and code already exists', () => {
    let state = initState();
    state = updateState(state, [{
      type: 'ERROR',
      error: {
        id: 'item1',
        code: 'REQUIRED',
        description: 'My error'
      }
    }]);

    state = updateState(state, [{
      type: 'ERROR',
      error: {
        id: 'item1',
        code: 'REQUIRED',
        description: 'New error'
      }
    }]);

    expect(state.errors['item1'].length).toBe(1);
    expect(state.errors['item1'][0].description).toBe('New error');
  });
});