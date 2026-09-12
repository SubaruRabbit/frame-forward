export type SceneResult = {
  sceneType: string;
  subjectCandidates: string[];
  light: { quality?: string; direction?: string };
  backgroundComplexity: string;
  compositionalStructures: string[];
  usablePositions: Array<{ description?: string }>;
  accessoryOpportunities: string[];
  safetyWarnings: string[];
};
import type { NetworkClient } from '@services/api';

type Task = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: SceneResult;
  errorCode?: string;
};
export async function createSceneAnalysis(
  network: NetworkClient,
  input: Record<string, unknown>,
): Promise<Task> {
  return network.request<Task>({
    path: '/scene-analyses',
    method: 'POST',
    body: JSON.stringify(input),
    headers: { 'Idempotency-Key': `scene-${Date.now()}` },
  });
}
export async function getSceneTask(network: NetworkClient, taskId: string): Promise<Task> {
  return network.request<Task>({ path: `/ai/tasks/${taskId}` });
}
