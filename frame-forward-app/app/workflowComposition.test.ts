import { createWorkflowUseCases } from './workflowComposition';

test('组合根装配照片复评和拍摄会话网络端口', () => {
  const network = { request: jest.fn() };
  const workflows = createWorkflowUseCases(network);
  expect(typeof workflows.photoReview.reanalyze).toBe('function');
  expect(typeof workflows.shootingSession.createSession).toBe('function');
});
