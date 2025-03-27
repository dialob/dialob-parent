import { renderHook, act } from '@testing-library/react';
import { useFillValueSet } from './useFillValueSet';
import { useFillSession } from './useFillSession';
import { SessionValueSet } from '@dialob/fill-api';

jest.mock('./useFillSession');

describe('useFillValueSet', () => {
	let mockSession: jest.Mocked<ReturnType<typeof useFillSession>>;

	beforeEach(() => {
		mockSession = {
			getValueSet: jest.fn(),
			on: jest.fn(),
			removeListener: jest.fn(),
		} as unknown as jest.Mocked<ReturnType<typeof useFillSession>>;

		(useFillSession as jest.Mock).mockReturnValue(mockSession);
	});

	it('should return the initial value set from the session', () => {
		const mockValueSet = { id: 'test', entries: [] };
		mockSession.getValueSet.mockReturnValue(mockValueSet);

		const { result } = renderHook(() => useFillValueSet('test'));

		expect(result.current).toBe(mockValueSet);
		expect(mockSession.getValueSet).toHaveBeenCalledWith('test');
	});

	it('should update the value set when the session emits an update event', () => {
		const initialValueSet = { id: 'test', entries: [] };
		const updatedValueSet = { id: 'test', entries: [{ key: '1', value: 'One' }] };

		mockSession.getValueSet
			.mockReturnValue(initialValueSet);

		const { result } = renderHook(() => useFillValueSet('test'));

		expect(result.current).toBe(initialValueSet);

    mockSession.getValueSet
      .mockReturnValue(updatedValueSet)

		act(() => {
			const listener = mockSession.on.mock.calls[0][1];
      listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
		});

		expect(result.current).toBe(updatedValueSet);
	});

	it('should clean up the listener on unmount', () => {
		const { unmount } = renderHook(() => useFillValueSet('test'));

		unmount();

		expect(mockSession.removeListener).toHaveBeenCalledWith('update', expect.any(Function));
	});

	it('should return undefined if no id is provided', () => {
		const { result } = renderHook(() => useFillValueSet(undefined));

		expect(result.current).toBeUndefined();
		expect(mockSession.getValueSet).not.toHaveBeenCalled();
	});
});
