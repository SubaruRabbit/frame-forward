import React from 'react';
import { act, create } from 'react-test-renderer';
import { PortfolioScreen, type PortfolioApi } from './PortfolioScreen';

const work = {
  mediaId: 'work-1',
  width: 1200,
  height: 800,
  subject: null,
  camera: 'Nikon Zf',
  lens: 'NIKKOR Z 40mm',
  favorite: false,
  availability: { exif: false, evaluation: false, sourcePlan: false, retake: false },
};
test('显示空作品集和不完整作品详情，且可切换收藏', async () => {
  const api: PortfolioApi = {
    list: jest.fn().mockResolvedValue({ items: [work], nextCursor: null }),
    detail: jest
      .fn()
      .mockResolvedValue({ ...work, exif: null, evaluation: null, sourcePlan: null, retake: null }),
    favorite: jest.fn().mockResolvedValue({ mediaId: 'work-1', favorite: true }),
    deleteWork: jest.fn(),
  };
  let view: ReturnType<typeof create>;
  await act(async () => {
    view = create(<PortfolioScreen api={api} />);
  });
  expect(view!.root.findByProps({ testID: 'portfolio-work-work-1' })).toBeTruthy();
  await act(async () => {
    view!.root.findByProps({ testID: 'portfolio-work-work-1' }).props.onPress();
  });
  expect(JSON.stringify(view!.toJSON())).toContain('EXIF 暂不可用');
  await act(async () => {
    view!.root.findByProps({ testID: 'portfolio-favorite-work-1' }).props.onPress();
  });
  expect(JSON.stringify(view!.toJSON())).toContain('已收藏');
});
test('删除失败时保留失败状态而不显示成功', async () => {
  const api: PortfolioApi = {
    list: jest.fn().mockResolvedValue({ items: [work], nextCursor: null }),
    detail: jest.fn().mockResolvedValue(work),
    favorite: jest.fn(),
    deleteWork: jest.fn().mockResolvedValue({
      jobId: 'job-1',
      mediaId: 'work-1',
      state: 'FAILED',
      failureReason: '关联数据清理失败，请重试。',
    }),
  };
  let view: ReturnType<typeof create>;
  await act(async () => {
    view = create(<PortfolioScreen api={api} />);
  });
  await act(async () => {
    view!.root.findByProps({ testID: 'portfolio-work-work-1' }).props.onPress();
  });
  await act(async () => {
    view!.root.findByProps({ testID: 'portfolio-delete-work-1' }).props.onPress();
  });
  expect(view!.root.findByProps({ testID: 'portfolio-delete-confirm' })).toBeTruthy();
  await act(async () => {
    view!.root.findByProps({ testID: 'portfolio-delete-confirm' }).props.onPress();
  });
  expect(JSON.stringify(view!.toJSON())).toContain('删除未完成，请重试。');
  expect(JSON.stringify(view!.toJSON())).not.toContain('删除完成');
});
test('为空列表显示明确状态', async () => {
  const api: PortfolioApi = {
    list: jest.fn().mockResolvedValue({ items: [], nextCursor: null }),
    detail: jest.fn(),
    favorite: jest.fn(),
    deleteWork: jest.fn(),
  };
  let view: ReturnType<typeof create>;
  await act(async () => {
    view = create(<PortfolioScreen api={api} />);
  });
  expect(JSON.stringify(view!.toJSON())).toContain('还没有作品');
});
