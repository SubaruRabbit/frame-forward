import AsyncStorage from '@react-native-async-storage/async-storage';

const key = 'frame-forward.active-ai-task-ids';
export type AiTaskState = 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED' | 'CANCELLED';
export type AiTaskStatus = {
  taskId: string;
  state: AiTaskState;
  result?: Record<string, unknown>;
  errorCode?: string;
};
export interface AiTaskApi {
  get(taskId: string): Promise<AiTaskStatus>;
}

export const activeAiTasks = {
  async load(): Promise<string[]> {
    try {
      const raw = await AsyncStorage.getItem(key);
      const ids: unknown = raw ? JSON.parse(raw) : [];
      return Array.isArray(ids) && ids.every(id => typeof id === 'string') ? ids : [];
    } catch {
      return [];
    }
  },
  async add(taskId: string): Promise<void> {
    const ids = await this.load();
    if (!ids.includes(taskId)) await AsyncStorage.setItem(key, JSON.stringify([...ids, taskId]));
  },
  async remove(taskId: string): Promise<void> {
    await AsyncStorage.setItem(
      key,
      JSON.stringify((await this.load()).filter(id => id !== taskId)),
    );
  },
  async restore(api: AiTaskApi): Promise<AiTaskStatus[]> {
    const ids = await this.load();
    const states = await Promise.all(ids.map(id => api.get(id)));
    await AsyncStorage.setItem(
      key,
      JSON.stringify(
        states.filter(s => s.state === 'QUEUED' || s.state === 'RUNNING').map(s => s.taskId),
      ),
    );
    return states;
  },
};
