export type ShootingPlan = {
  label: 'SAFE' | 'ATMOSPHERIC' | 'CREATIVE';
  recommended: boolean;
  position: string;
  distance: string;
  cameraHeight: string;
  orientation: string;
  focalLengthMm: number;
  exposure: { aperture: string; shutterSpeed: string; iso: number; startingPoint: boolean };
  metering: string;
  focus: string;
  driveMode: string;
  whiteBalance: string;
  composition: string;
  pose: string;
  accessoryUse: string;
  steps: string[];
};

export type ShootingPlanRequest = { sceneAnalysisId: string };

export type ShootingPlanTask = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: { plans: ShootingPlan[] };
};

export type ShootingPlanProgress = 'queued' | 'running';
