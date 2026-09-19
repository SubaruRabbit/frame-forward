import React, { useCallback, useEffect, useState } from 'react';
import { Pressable, Text, View } from 'react-native';

import { EmptyState, FailureState, LoadingState } from '@components/ScreenState';
import type { LearningUseCases } from './application/LearningUseCases';
import type { CourseDetail, CourseSummary } from './course';

type CatalogState = 'loading' | 'ready' | 'failed';
type DetailState = 'loading' | 'ready' | 'failed';

export function LearningScreen({ useCases }: { useCases: LearningUseCases }) {
  const [catalogState, setCatalogState] = useState<CatalogState>('loading');
  const [courses, setCourses] = useState<CourseSummary[]>([]);
  const [catalogError, setCatalogError] = useState<string | null>(null);
  const [detailState, setDetailState] = useState<DetailState | null>(null);
  const [detail, setDetail] = useState<CourseDetail | null>(null);
  const [selectedCourseId, setSelectedCourseId] = useState<string | null>(null);
  const [detailError, setDetailError] = useState<string | null>(null);

  const loadCatalog = useCallback(async () => {
    setCatalogState('loading');
    setCatalogError(null);
    try {
      setCourses(await useCases.loadCourses());
      setCatalogState('ready');
    } catch (cause) {
      setCatalogError(cause instanceof Error ? cause.message : '课程目录加载失败，请重试。');
      setCatalogState('failed');
    }
  }, [useCases]);

  const loadDetail = useCallback(
    async (courseId: string) => {
      setSelectedCourseId(courseId);
      setDetail(null);
      setDetailError(null);
      setDetailState('loading');
      try {
        setDetail(await useCases.loadCourse(courseId));
        setDetailState('ready');
      } catch (cause) {
        setDetailError(cause instanceof Error ? cause.message : '课程内容加载失败，请重试。');
        setDetailState('failed');
      }
    },
    [useCases],
  );

  useEffect(() => {
    loadCatalog().catch(() => undefined);
  }, [loadCatalog]);

  const backToCatalog = () => {
    setSelectedCourseId(null);
    setDetail(null);
    setDetailState(null);
    setDetailError(null);
  };

  if (detailState === 'loading') return <LoadingState title="正在加载课程内容" />;
  if (detailState === 'failed')
    return (
      <View>
        <FailureState
          title="课程内容加载失败"
          message={detailError ?? undefined}
          onRetry={() => selectedCourseId && loadDetail(selectedCourseId)}
        />
        <Pressable accessibilityRole="button" onPress={backToCatalog} testID="back-to-catalog">
          <Text>返回课程目录</Text>
        </Pressable>
      </View>
    );
  if (detailState === 'ready' && detail)
    return (
      <View testID="course-detail-screen">
        <Pressable accessibilityRole="button" onPress={backToCatalog}>
          <Text>返回课程目录</Text>
        </Pressable>
        <Text>{detail.title}</Text>
        {detail.chapters.map(chapter => (
          <View key={chapter.id}>
            <Text>{chapter.title}</Text>
            {chapter.lessons.map(lesson => (
              <View key={lesson.id}>
                <Text>{lesson.title}</Text>
                <Text>{lesson.objective}</Text>
                <Text>{lesson.content}</Text>
                <Text>正确示例：{lesson.correctExample}</Text>
                <Text>错误示例：{lesson.incorrectExample}</Text>
                <Text>判断题：{lesson.exercise.question}</Text>
                <Text>作业：{lesson.assignment}</Text>
              </View>
            ))}
          </View>
        ))}
      </View>
    );
  if (catalogState === 'loading') return <LoadingState title="正在加载课程目录" />;
  if (catalogState === 'failed')
    return (
      <FailureState
        title="课程目录加载失败"
        message={catalogError ?? undefined}
        onRetry={loadCatalog}
      />
    );
  if (!courses.length)
    return <EmptyState title="暂时没有课程" message="课程准备完成后会显示在这里。" />;
  return (
    <View testID="learning-screen">
      <Text>课程</Text>
      {courses.map(course => (
        <Pressable
          accessibilityRole="button"
          key={course.id}
          onPress={() => loadDetail(course.id)}
          testID={`course-${course.id}`}
        >
          <Text>
            {course.title} · {course.category}
          </Text>
          <Text>{course.lessonCount} 节课</Text>
        </Pressable>
      ))}
    </View>
  );
}
