import { colors, elevation, radii, spacing } from '@theme/tokens';
import React from 'react';
import { Image, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';

const dawnImage = require('../../assets/images/light-journal-dawn.png');

type LightJournalHomeProps = {
  onStartCapture: () => void;
};

export function LightJournalHome({ onStartCapture }: LightJournalHomeProps) {
  return (
    <View style={styles.root} testID="light-journal-home">
      <View style={styles.brandRow}>
        <View>
          <Text allowFontScaling style={styles.brand}>
            LIGHT JOURNAL
          </Text>
          <Text allowFontScaling style={styles.title}>
            记录今天的光
          </Text>
        </View>
        <Pressable
          accessibilityLabel="查看通知"
          accessibilityRole="button"
          style={styles.notification}
        >
          <Text style={styles.notificationText}>●</Text>
        </Pressable>
      </View>

      <View style={styles.hero}>
        <Image accessibilityElementsHidden source={dawnImage} style={styles.heroImage} />
        <View style={styles.heroOverlay}>
          <Text allowFontScaling style={styles.heroEyebrow}>
            今日练习
          </Text>
          <Text allowFontScaling style={styles.heroTitle}>
            让每一次快门更有把握
          </Text>
          <Text allowFontScaling style={styles.heroCopy}>
            从构图、光线到色彩，拍出更好的照片
          </Text>
          <Pressable
            accessibilityLabel="开始拍摄，进入导入照片流程"
            accessibilityRole="button"
            onPress={onStartCapture}
            style={({ pressed }) => [styles.captureButton, pressed && styles.captureButtonPressed]}
            testID="start-capture"
          >
            <Text allowFontScaling style={styles.captureButtonText}>
              开始拍摄
            </Text>
          </Pressable>
        </View>
      </View>

      <SectionTitle action="02 / 12" title="今日摄影灵感" />
      <ScrollView
        contentContainerStyle={styles.inspirationList}
        horizontal
        showsHorizontalScrollIndicator={false}
      >
        <InspirationCard label="雾中留白" position="left" />
        <InspirationCard label="晨雾" position="right" />
      </ScrollView>

      <SectionTitle title="快速学习" />
      <Pressable
        accessibilityLabel="构图的第一堂课，12 分钟"
        accessibilityRole="button"
        style={styles.lesson}
      >
        <View style={styles.lessonIndex}>
          <Text style={styles.lessonIndexText}>01</Text>
        </View>
        <View style={styles.lessonCopy}>
          <Text allowFontScaling style={styles.lessonTitle}>
            构图的第一堂课
          </Text>
          <Text allowFontScaling style={styles.lessonMeta}>
            12 分钟 · 适合刚开始拍摄的你
          </Text>
        </View>
        <Text accessibilityElementsHidden style={styles.chevron}>
          ›
        </Text>
      </Pressable>

      <SectionTitle action="查看全部" title="最近评分" />
      <View accessibilityLabel="湖畔午后，86 分，构图很稳，前景层次出色" style={styles.scoreCard}>
        <View style={styles.scorePreview}>
          <Image accessibilityElementsHidden source={dawnImage} style={styles.scoreImage} />
        </View>
        <View style={styles.scoreCopy}>
          <Text allowFontScaling style={styles.scoreTitle}>
            湖畔午后
          </Text>
          <Text allowFontScaling style={styles.scoreHint}>
            构图很稳，前景层次出色
          </Text>
        </View>
        <View accessibilityElementsHidden style={styles.scoreBadge}>
          <Text style={styles.scoreNumber}>86</Text>
          <Text style={styles.scoreUnit}>分</Text>
        </View>
      </View>
    </View>
  );
}

function SectionTitle({ action, title }: { action?: string; title: string }) {
  return (
    <View style={styles.sectionTitle}>
      <Text allowFontScaling style={styles.sectionHeading}>
        {title}
      </Text>
      {action ? (
        <Text allowFontScaling style={styles.sectionAction}>
          {action}
        </Text>
      ) : null}
    </View>
  );
}

function InspirationCard({ label, position }: { label: string; position: 'left' | 'right' }) {
  return (
    <View accessibilityLabel={label} style={styles.inspirationCard}>
      <Image
        accessibilityElementsHidden
        source={dawnImage}
        style={[styles.inspirationImage, position === 'right' && styles.inspirationImageRight]}
      />
      <View style={styles.inspirationOverlay}>
        <Text allowFontScaling style={styles.inspirationLabel}>
          {label}
        </Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { gap: spacing.lg, paddingBottom: spacing.xxl },
  brandRow: { alignItems: 'center', flexDirection: 'row', justifyContent: 'space-between' },
  brand: { color: colors.accentPressed, fontSize: 11, fontWeight: '800', letterSpacing: 2.1 },
  title: { color: colors.text, fontSize: 30, fontWeight: '800', marginTop: spacing.xs },
  notification: {
    alignItems: 'center',
    backgroundColor: colors.surface,
    borderColor: colors.border,
    borderRadius: radii.pill,
    borderWidth: StyleSheet.hairlineWidth,
    height: 44,
    justifyContent: 'center',
    width: 44,
  },
  notificationText: { color: colors.accent, fontSize: 20 },
  hero: { borderRadius: radii.lg, minHeight: 274, overflow: 'hidden' },
  heroImage: { height: '100%', opacity: 0.98, position: 'absolute', width: '100%' },
  heroOverlay: {
    backgroundColor: 'rgba(12, 25, 36, 0.54)',
    flex: 1,
    justifyContent: 'flex-end',
    padding: spacing.xl,
  },
  heroEyebrow: { color: '#D4F1EC', fontSize: 12, fontWeight: '800', letterSpacing: 1.1 },
  heroTitle: {
    color: colors.onAccent,
    fontSize: 26,
    fontWeight: '800',
    lineHeight: 34,
    marginTop: spacing.sm,
  },
  heroCopy: { color: '#F0F7F5', fontSize: 14, lineHeight: 21, marginTop: spacing.sm },
  captureButton: {
    alignItems: 'center',
    alignSelf: 'flex-start',
    backgroundColor: colors.accent,
    borderRadius: radii.pill,
    justifyContent: 'center',
    marginTop: spacing.lg,
    minHeight: 44,
    paddingHorizontal: spacing.lg,
  },
  captureButtonPressed: { backgroundColor: colors.accentPressed },
  captureButtonText: { color: colors.onAccent, fontSize: 14, fontWeight: '800' },
  sectionTitle: {
    alignItems: 'baseline',
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: spacing.sm,
  },
  sectionHeading: { color: colors.text, fontSize: 19, fontWeight: '800' },
  sectionAction: { color: colors.mutedText, fontSize: 12, fontWeight: '700', letterSpacing: 0.3 },
  inspirationList: { gap: spacing.md, paddingRight: spacing.lg },
  inspirationCard: { borderRadius: radii.md, height: 142, overflow: 'hidden', width: 192 },
  inspirationImage: {
    height: '100%',
    position: 'absolute',
    transform: [{ translateX: -16 }],
    width: 250,
  },
  inspirationImageRight: { transform: [{ translateX: -74 }], width: 260 },
  inspirationOverlay: {
    backgroundColor: 'rgba(12, 25, 36, 0.22)',
    flex: 1,
    justifyContent: 'flex-end',
    padding: spacing.md,
  },
  inspirationLabel: { color: colors.onAccent, fontSize: 16, fontWeight: '800' },
  lesson: {
    alignItems: 'center',
    backgroundColor: colors.surface,
    borderColor: colors.border,
    borderRadius: radii.md,
    borderWidth: StyleSheet.hairlineWidth,
    elevation: elevation.card,
    flexDirection: 'row',
    minHeight: 80,
    padding: spacing.md,
  },
  lessonIndex: {
    alignItems: 'center',
    backgroundColor: colors.accentSoft,
    borderRadius: radii.sm,
    height: 44,
    justifyContent: 'center',
    width: 44,
  },
  lessonIndexText: { color: colors.accentPressed, fontSize: 12, fontWeight: '800' },
  lessonCopy: { flex: 1, marginHorizontal: spacing.md },
  lessonTitle: { color: colors.text, fontSize: 15, fontWeight: '800' },
  lessonMeta: { color: colors.mutedText, fontSize: 12, lineHeight: 18, marginTop: spacing.xs },
  chevron: { color: colors.muted, fontSize: 28, fontWeight: '300' },
  scoreCard: {
    alignItems: 'center',
    backgroundColor: colors.surface,
    borderColor: colors.border,
    borderRadius: radii.md,
    borderWidth: StyleSheet.hairlineWidth,
    elevation: elevation.card,
    flexDirection: 'row',
    minHeight: 88,
    padding: spacing.sm,
  },
  scorePreview: { borderRadius: radii.sm, height: 68, overflow: 'hidden', width: 68 },
  scoreImage: { height: 68, transform: [{ translateX: -37 }], width: 120 },
  scoreCopy: { flex: 1, marginHorizontal: spacing.md },
  scoreTitle: { color: colors.text, fontSize: 15, fontWeight: '800' },
  scoreHint: { color: colors.mutedText, fontSize: 12, lineHeight: 18, marginTop: spacing.xs },
  scoreBadge: { alignItems: 'baseline', flexDirection: 'row' },
  scoreNumber: { color: colors.accentPressed, fontSize: 25, fontWeight: '800' },
  scoreUnit: { color: colors.accentPressed, fontSize: 12, fontWeight: '800', marginLeft: 2 },
});
