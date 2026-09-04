export type SceneResult = {
  sceneAnalysisId: string;
  sceneType: string;
  subjectCandidates: string[];
  light: { quality?: string; direction?: string };
  backgroundComplexity: string;
  compositionalStructures: string[];
  usablePositions: Array<{ description?: string }>;
  accessoryOpportunities: string[];
  safetyWarnings: string[];
};

export type SceneAnalysisRequest = {
  environmentMediaId: string;
  subjectType: 'CUSTOM';
  subject: string;
  targetStyle: string;
  timeConstraintMinutes: number;
  equipmentIds: string[];
};

export type SceneAnalysisTask = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: SceneResult;
  errorCode?: string;
};

export type SceneAnalysisProgress = 'queued' | 'running';
