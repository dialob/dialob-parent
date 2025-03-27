import { renderHook, act } from '@testing-library/react';
import { useFillVariable } from './useFillVariable';
import { useFillSession } from './useFillSession';

jest.mock('./useFillSession');

describe('useFillVariable', () => {
  let mockSession: jest.Mocked<ReturnType<typeof useFillSession>>;

  beforeEach(() => {
    mockSession = {
      getVariable: jest.fn(),
      on: jest.fn(),
      removeListener: jest.fn(),
    } as unknown as jest.Mocked<ReturnType<typeof useFillSession>>;

    (useFillSession as jest.Mock).mockReturnValue(mockSession);
  });

  it('should return the initial value of the variable from the session', () => {
    mockSession.getVariable.mockReturnValue('initialValue');

    const { result } = renderHook(() => useFillVariable('testId'));

    expect(result.current).toBe('initialValue');
    expect(mockSession.getVariable).toHaveBeenCalledWith('testId');
  });

  it('should update the variable when the session emits an update event', () => {
    mockSession.getVariable.mockReturnValue('initialValue');

    const { result } = renderHook(() => useFillVariable('testId'));

    expect(result.current).toBe('initialValue');

    mockSession.getVariable.mockReturnValue('updatedValue');

    act(() => {
      const listener = mockSession.on.mock.calls[0][1];
      listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
    });

    expect(result.current).toBe('updatedValue');
  });

  it('should clean up the listener on unmount', () => {
    const { unmount } = renderHook(() => useFillVariable('testId'));

    unmount();

    expect(mockSession.removeListener).toHaveBeenCalledWith('update', expect.any(Function));
  });

  it('should return undefined if no id is provided', () => {
    const { result } = renderHook(() => useFillVariable(undefined));

    expect(result.current).toBeUndefined();
    expect(mockSession.getVariable).not.toHaveBeenCalled();
  });

  it('should not set up a listener if no id is provided', () => {
    renderHook(() => useFillVariable(undefined));

    expect(mockSession.on).not.toHaveBeenCalled();
  });
});
