import { renderHook, act } from '@testing-library/react';
import { useFillItem } from './useFillItem';
import { useFillSession } from './useFillSession';
import { Session, SessionItem, SessionError } from '@dialob/fill-api';

jest.mock('./useFillSession');

describe('useFillItem', () => {
  let mockSession: jest.Mocked<Session>;

  beforeEach(() => {
    mockSession = {
      getItem: jest.fn(),
      getItemErrors: jest.fn(),
      on: jest.fn(),
      removeListener: jest.fn(),
    } as unknown as jest.Mocked<Session>;

    (useFillSession as jest.Mock).mockReturnValue(mockSession);
  });

  it('should return initial state when id is undefined', () => {
    const { result } = renderHook(() => useFillItem(undefined));
    expect(result.current.item).toBeUndefined();
    expect(result.current.errors).toEqual([]);
    expect(result.current.availableItems).toEqual([]);
  });

  it('should return initial state when id is defined', () => {
    const mockItem = { id: 'item1', items: ['child1', 'child2'] } as SessionItem<'group'>;
    const mockErrors: SessionError[] = [{ id: 'id1', code: 'error1', description: 'Id error 1' }];

    mockSession.getItem.mockImplementation((id) => {
      if (id === 'item1') return mockItem;
      return undefined;
    });
    mockSession.getItemErrors.mockReturnValue(mockErrors);

    const { result } = renderHook(() => useFillItem<'group'>('item1'));
    expect(result.current.item).toEqual(mockItem);
    expect(result.current.errors).toEqual(mockErrors);
    expect(result.current.availableItems).toEqual([]);
  });

  it('should update item, errors, and availableItems on session update', () => {
    const mockItem = { id: 'item1', items: ['child1', 'child2'] } as SessionItem<'group'>;
    const mockErrors: SessionError[] = [{ id: 'id1', code: 'error1', description: 'Error 1' }];
    const mockUpdatedItem = { id: 'item1', items: ['child1'] } as SessionItem<'group'>;
    const mockUpdatedErrors: SessionError[] = [{ id: 'id2', code: 'error2', description: 'Error 2' }];

    mockSession.getItem.mockImplementation((id) => {
      if (id === 'item1') return mockItem;
      return undefined;
    });
    mockSession.getItemErrors.mockReturnValue(mockErrors);

    const { result } = renderHook(() => useFillItem<'group'>('item1'));

    expect(result.current.item).toEqual(mockItem);
    expect(result.current.errors).toEqual(mockErrors);
    expect(result.current.availableItems).toEqual([]);

    mockSession.getItem.mockImplementation((id) => {
      if (id === 'item1') return mockUpdatedItem;
      return undefined;
    });
    mockSession.getItemErrors.mockReturnValue(mockUpdatedErrors);

    act(() => {
      const listener = mockSession.on.mock.calls[0][1];
      listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
    });

    expect(result.current.item).toEqual(mockUpdatedItem);
    expect(result.current.errors).toEqual(mockUpdatedErrors);
    expect(result.current.availableItems).toEqual([]);
  });

  it('should clean up the listener on unmount', () => {
    const { unmount } = renderHook(() => useFillItem('item1'));

    unmount();

    expect(mockSession.removeListener).toHaveBeenCalledWith('update', expect.any(Function));
  });

  it('should filter visible items correctly', () => {
    const mockItem = { id: 'item1', items: ['child1', 'child2', 'child3'] } as SessionItem<'group'>;

    mockSession.getItem.mockImplementation((id) => {
      if (id === 'item1') return mockItem;
      if (id === 'child1' || id === 'child3') return { id, type: 'variable' };
      return undefined;
    });

    const { result } = renderHook(() => useFillItem<'group'>('item1'));

    expect(result.current.availableItems).toEqual(['child1', 'child3']);
  });
});
