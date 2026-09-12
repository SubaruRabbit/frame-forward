export type ReferenceImageTask = {
  taskId: string;
  state: 'QUEUED' | 'RUNNING' | 'SUCCEEDED' | 'FAILED';
  result?: { imageUrl: string };
  errorCode?: string;
};

export type ReferenceImageRequest = {
  environmentMediaId: string;
  shootingPlanId: string;
  selectedPlan: {
    label: string;
    position: string;
    cameraHeight: string;
    composition: string;
    orientation: string;
    focalLengthMm: number;
    exposure: { startingPoint: boolean };
  };
};
