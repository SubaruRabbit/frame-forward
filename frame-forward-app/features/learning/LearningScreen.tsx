import React, { useEffect, useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import { cachedP0Courses, type Course, type Lesson } from './courseCache';

export function LearningScreen() {
  const [courses, setCourses] = useState<Course[]>([]);
  const [lesson, setLesson] = useState<Lesson | null>(null);
  useEffect(() => {
    cachedP0Courses().then(setCourses);
  }, []);
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
