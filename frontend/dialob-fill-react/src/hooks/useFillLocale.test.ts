import { renderHook, act } from '@testing-library/react';
import { useFillLocale } from './useFillLocale';
import { useFillSession } from './useFillSession';

jest.mock('./useFillSession');

describe('useFillLocale', () => {
	let mockSession: jest.Mocked<ReturnType<typeof useFillSession>>;

	beforeEach(() => {
		mockSession = {
			getLocale: jest.fn(),
			on: jest.fn(),
			removeListener: jest.fn(),
		} as unknown as jest.Mocked<ReturnType<typeof useFillSession>>;

		(useFillSession as jest.Mock).mockReturnValue(mockSession);
	});

	it('should return the initial locale from the session', () => {
		mockSession.getLocale.mockReturnValue('en');

		const { result } = renderHook(() => useFillLocale());

		expect(result.current).toBe('en');
	});

	it('should update the locale when the session emits an update event', () => {
		mockSession.getLocale.mockReturnValue('en')

		const { result } = renderHook(() => useFillLocale());

		expect(result.current).toBe('en');

    mockSession.getLocale.mockReturnValue('fr');

		act(() => {
			const listener = mockSession.on.mock.calls[0][1];
			listener('SYNC', { message: 'Mock error', name: 'MOCK_CODE' });
		});

		expect(result.current).toBe('fr');
	});

	it('should clean up the listener on unmount', () => {
		const { unmount } = renderHook(() => useFillLocale());

		unmount();

		expect(mockSession.removeListener).toHaveBeenCalledWith('update', expect.any(Function));
	});
});
