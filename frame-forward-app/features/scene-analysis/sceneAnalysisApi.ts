export type SceneResult = {
  sceneType: string;
  subjectCandidates: string[];
  light: {quality?: string; direction?: string};
  backgroundComplexity: string;
  compositionalStructures: string[];
  usablePositions: Array<{description?: string}>;
  accessoryOpportunities: string[];
  safetyWarnings: string[];
};

type Task = {taskId: string; state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED'; result?: SceneResult; errorCode?: string};
const endpoint = 'http://10.0.2.2:8080';

export async function createSceneAnalysis(accessToken: string, input: Record<string, unknown>): Promise<Task> {
  const response = await fetch(`${endpoint}/scene-analyses`, {method: 'POST', headers: {'Authorization': `Bearer ${accessToken}`, 'Content-Type': 'application/json', 'Idempotency-Key': `scene-${Date.now()}`}, body: JSON.stringify(input)});
  if (!response.ok) throw new Error('无法创建现场分析，请确认照片和器材归属。');
  return response.json() as Promise<Task>;
}
export async function getSceneTask(accessToken: string, taskId: string): Promise<Task> {
  const response = await fetch(`${endpoint}/ai/tasks/${taskId}`, {headers: {'Authorization': `Bearer ${accessToken}`}});
  if (!response.ok) throw new Error('无法获取现场分析进度。');
  return response.json() as Promise<Task>;
}
