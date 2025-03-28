import { act, renderHook } from '@testing-library/react';
import { useFillUpdate } from './useFillUpdate';
import { useFillSession } from './useFillSession';

jest.mock('./useFillSession');

describe('useFillUpdate', () => {
	let mockSession: jest.Mocked<ReturnType<typeof useFillSession>>;
	let mockUpdateFn: jest.Mock;

	beforeEach(() => {
		mockSession = {
			on: jest.fn(),
			removeListener: jest.fn(),
		} as unknown as jest.Mocked<ReturnType<typeof useFillSession>>;

		mockUpdateFn = jest.fn();

		(useFillSession as jest.Mock).mockReturnValue(mockSession);
	});

	it('should call the update function immediately and on session update', () => {
		renderHook(() => useFillUpdate(mockUpdateFn));

		// Verify the update function is called immediately
		expect(mockUpdateFn).toHaveBeenCalledWith(mockSession);

		// Simulate a session update event
		const listener = mockSession.on.mock.calls[0][1];
		act(() => {
      listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
		});

		// Verify the update function is called on session update
		expect(mockUpdateFn).toHaveBeenCalledTimes(2);
		expect(mockUpdateFn).toHaveBeenCalledWith(mockSession);
	});

	it('should clean up the listener on unmount', () => {
		const { unmount } = renderHook(() => useFillUpdate(mockUpdateFn));

		unmount();

		// Verify the listener is removed on unmount
		expect(mockSession.removeListener).toHaveBeenCalledWith('update', expect.any(Function));
	});

	it('should update the reference to the update function when it changes', () => {
		const { rerender } = renderHook(({ updateFn }) => useFillUpdate(updateFn), {
			initialProps: { updateFn: mockUpdateFn },
		});

		const newUpdateFn = jest.fn();
		rerender({ updateFn: newUpdateFn });

		// Simulate a session update event
		const listener = mockSession.on.mock.calls[0][1];
		act(() => {
      listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
		});

		// Verify the new update function is called
		expect(newUpdateFn).toHaveBeenCalledWith(mockSession);
		expect(mockUpdateFn).toHaveBeenCalledTimes(1); // Only called initially
	});
});
