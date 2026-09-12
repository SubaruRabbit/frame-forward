export type Availability = {
  exif: boolean;
  evaluation: boolean;
  sourcePlan: boolean;
  retake: boolean;
};

export type Work = {
  mediaId: string;
  width: number;
  height: number;
  subject: string | null;
  camera: string | null;
  lens: string | null;
  favorite: boolean;
  availability: Availability;
  exif?: Record<string, string> | null;
  evaluation?: object | null;
  sourcePlan?: object | null;
  retake?: object | null;
  workflowContext?: WorkflowContext;
};

export type WorkflowContext = {
  mediaId: string;
  evaluation: { evaluationId: string; sessionId: string | null } | null;
  sourcePlan: { shootingPlanId: string; planContext: string } | null;
  session: { sessionId: string; shootingPlanId: string; planContext: string } | null;
  comparisonCandidates: Array<{ evaluationId: string; mediaId: string }>;
};

export type Filter = { subject?: string; camera?: string; lens?: string; favorite?: boolean };

export type DeletionJob = {
  jobId: string;
  mediaId: string;
  state: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  failureReason: string | null;
};
