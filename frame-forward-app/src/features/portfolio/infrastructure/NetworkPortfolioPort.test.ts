import { createNetworkPortfolioPort } from './NetworkPortfolioPort';

test('将工作流上下文映射为作品领域模型', async () => {
  const network = {
    request: jest.fn().mockResolvedValue({
      mediaId: 'media-1',
      width: 1,
      height: 1,
      subject: null,
      camera: null,
      lens: null,
      favorite: false,
      availability: { exif: false, evaluation: false, sourcePlan: false, retake: false },
      workflowContext: {
        mediaId: 'media-1',
        evaluation: null,
        sourcePlan: null,
        session: null,
        comparisonCandidates: [],
      },
    }),
  };
  const work = await createNetworkPortfolioPort(network).detail('media-1');
  expect(work.workflowContext).toEqual(
    expect.objectContaining({ mediaId: 'media-1', comparisonCandidates: [] }),
  );
});
