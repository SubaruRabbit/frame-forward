import React, { useState } from 'react';
import { Pressable, Text, View } from 'react-native';
import { type PhotoEvaluation, PhotoReview } from './PhotoReview';
import type { PhotoReviewUseCases } from './application/PhotoReviewUseCases';

export function PhotoReviewFlow({
  mediaId,
  sessionId,
  result: initialResult,
  useCases,
}: {
  mediaId: string;
  sessionId?: string;
  result: PhotoEvaluation;
  useCases: PhotoReviewUseCases;
}) {
  const [result, setResult] = useState(initialResult);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const reanalyze = async () => {
    if (loading) return;
    setLoading(true);
    setError(null);
    try {
      setResult(await useCases.reanalyze(mediaId, sessionId));
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : '照片重新分析失败，请重试。');
    } finally {
      setLoading(false);
    }
  };
  return (
    <View>
      <PhotoReview result={result} onReanalyze={reanalyze} reanalyzing={loading} />
      {error ? (
        <View>
          <Text testID="reanalyze-error">{error}</Text>
          <Pressable testID="reanalyze-retry" accessibilityRole="button" onPress={reanalyze}>
            <Text>重试</Text>
          </Pressable>
        </View>
      ) : null}
    </View>
  );
}
