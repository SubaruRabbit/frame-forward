import React, { useEffect, useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import type { LearningUseCases } from './application/LearningUseCases';
import type { Course, Lesson } from './courseCache';

export function LearningScreen({ useCases }: { useCases: LearningUseCases }) {
  const [courses, setCourses] = useState<Course[]>([]);
  const [lesson, setLesson] = useState<Lesson | null>(null);
  useEffect(() => {
    useCases.loadCourses().then(setCourses);
  }, [useCases]);
  if (lesson)
    return (
      <View testID="lesson-screen">
        <Text>{lesson.title}</Text>
        <Text>{lesson.objective}</Text>
        <Text>练习：上传一张 JPEG，并围绕本课目标获得点评。</Text>
      </View>
    );
  return (
    <View testID="learning-screen">
      <Text>课程</Text>
      {courses.length ? (
        courses.map(course => (
          <Pressable key={course.id} onPress={() => setLesson(course.lessons[0] ?? null)}>
            <Text>
              {course.title} · {course.category}
            </Text>
            <Text>进度 0/{course.lessons.length}</Text>
          </Pressable>
        ))
      ) : (
        <Text>正在准备已缓存的 P0 课程</Text>
      )}
    </View>
  );
}
