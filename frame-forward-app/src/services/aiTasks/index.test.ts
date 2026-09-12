import AsyncStorage from '@react-native-async-storage/async-storage';
import { activeAiTasks } from './index';
test('a simulated restart restores terminal state and clears its active id', async () => {
  (AsyncStorage.getItem as jest.Mock).mockResolvedValue(JSON.stringify(['task-1']));
  await expect(
    activeAiTasks.restore({
      get: async id => ({ taskId: id, state: 'FAILED', errorCode: 'INVALID_MODEL_OUTPUT' }),
    }),
  ).resolves.toEqual([{ taskId: 'task-1', state: 'FAILED', errorCode: 'INVALID_MODEL_OUTPUT' }]);
  expect(AsyncStorage.setItem).toHaveBeenCalledWith('frame-forward.active-ai-task-ids', '[]');
});
