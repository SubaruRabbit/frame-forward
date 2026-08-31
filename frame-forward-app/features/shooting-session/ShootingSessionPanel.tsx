import React, {useMemo, useState} from 'react';
import {Image, Pressable, StyleSheet, Text, View} from 'react-native';

export type SessionPlan = {id: string; label: string; summary: string};
export type ShootingSession = {id: string; planId: string; label: string};
type Exif = {aperture?: string; shutterSpeed?: string; iso?: string};
export type RetakeWork = {mediaId: string; score: number; imageUrl?: string; exif?: Exif};
export type RetakeComparison = {
  original: RetakeWork;
  retake: RetakeWork;
  scoreDelta: number;
  dimensionChanges: Record<string, number>;
  compositionChanges: string[];
  parameterChanges: Record<string, {before: string; after: string}>;
  improvedProblems: string[];
  remainingProblems: string[];
  nextPracticeAdvice: string;
};

type Props = {
  plans: SessionPlan[];
  sessions?: ShootingSession[];
  comparison?: RetakeComparison;
  onCreateSession?: (planId: string) => void;
  onSelectSession?: (sessionId: string) => void;
};

export function ShootingSessionPanel({plans, sessions = [], comparison, onCreateSession, onSelectSession}: Props) {
  const [planId, setPlanId] = useState(() => plans[0]?.id ?? '');
  const [sessionId, setSessionId] = useState(() => sessions[0]?.id ?? '');
  const selectedPlan = useMemo(() => plans.find(plan => plan.id === planId), [planId, plans]);
  const selectSession = (nextSessionId: string) => {
    setSessionId(nextSessionId);
    onSelectSession?.(nextSessionId);
  };

  return <View style={styles.panel} testID="shooting-session-panel">
    <Text style={styles.title}>重拍对比</Text>
    <Text style={styles.copy}>选择已采用的方案，建立同一拍摄任务内的前后对比。</Text>
    <Text style={styles.sectionTitle}>拍摄方案</Text>
    <View style={styles.choices}>{plans.map(plan => <Pressable accessibilityRole="button" accessibilityState={{selected: plan.id === planId}} key={plan.id} onPress={() => setPlanId(plan.id)} style={[styles.choice, plan.id === planId && styles.choiceSelected]} testID={`session-plan-${plan.id}`}><Text style={styles.choiceLabel}>{plan.label}</Text><Text style={styles.choiceSummary}>{plan.summary}</Text></Pressable>)}</View>
    {selectedPlan ? <Text testID="selected-session-plan" style={styles.selectedPlan}>当前方案：{selectedPlan.label}</Text> : <Text style={styles.missing}>请先选择一个拍摄方案。</Text>}
    <Pressable accessibilityRole="button" accessibilityState={{disabled: !selectedPlan}} disabled={!selectedPlan} onPress={() => selectedPlan && onCreateSession?.(selectedPlan.id)} style={[styles.primaryButton, !selectedPlan && styles.disabledButton]} testID="create-shooting-session"><Text style={styles.primaryButtonText}>创建拍摄任务</Text></Pressable>
    {sessions.length > 0 ? <View style={styles.sessions}><Text style={styles.sectionTitle}>继续拍摄任务</Text>{sessions.map(session => <Pressable accessibilityRole="button" accessibilityState={{selected: session.id === sessionId}} key={session.id} onPress={() => selectSession(session.id)} style={[styles.session, session.id === sessionId && styles.choiceSelected]} testID={`shooting-session-${session.id}`}><Text>{session.label}</Text></Pressable>)}</View> : null}
    {comparison ? <ComparisonResult comparison={comparison}/> : null}
  </View>;
}

function ComparisonResult({comparison}: {comparison: RetakeComparison}) {
  return <View style={styles.comparison} testID="retake-comparison">
    <Text style={styles.sectionTitle}>前后作品</Text>
    <View style={styles.mediaRow}><WorkCard title="原作" work={comparison.original} testID="original-media"/><WorkCard title="重拍" work={comparison.retake} testID="retake-media"/></View>
    <Text style={styles.delta} testID="score-delta">总分变化：{signed(comparison.scoreDelta)} 分</Text>
    <Section testID="dimension-changes" title="分项变化" text={entries(comparison.dimensionChanges, value => signed(value))}/>
    <Section testID="composition-changes" title="构图变化" text={list(comparison.compositionChanges)}/>
    <Section testID="parameter-changes" title="参数变化" text={entries(comparison.parameterChanges, value => `${value.before} → ${value.after}`)}/>
    <Section testID="improved-problems" title="已改善问题" text={list(comparison.improvedProblems, '本次尚未确认明显改善')}/>
    <Section testID="remaining-problems" title="仍需练习" text={list(comparison.remainingProblems, '未发现仍存的主要问题')}/>
    <Section testID="next-practice-advice" title="下一步练习" text={comparison.nextPracticeAdvice}/>
  </View>;
}

function WorkCard({title, work, testID}: {title: string; work: RetakeWork; testID: string}) {
  const hasExif = Boolean(work.exif && Object.values(work.exif).some(Boolean));
  return <View style={styles.workCard} testID={testID}>
    <Text style={styles.workTitle}>{title} · {work.score} 分</Text>
    {work.imageUrl ? <Image accessibilityLabel={`${title}照片`} source={{uri: work.imageUrl}} style={styles.image}/> : <View accessibilityLabel={`${title}照片未提供`} style={styles.imageFallback}><Text>照片未提供</Text></View>}
    {hasExif ? <Text style={styles.exif}>{[work.exif?.aperture, work.exif?.shutterSpeed, work.exif?.iso && `ISO ${work.exif.iso}`].filter(Boolean).join(' · ')}</Text> : <Text style={styles.exifUnavailable} testID={`${testID === 'original-media' ? 'original' : 'retake'}-exif-unavailable`}>EXIF：未提供</Text>}
  </View>;
}

function Section({title, text, testID}: {title: string; text: string; testID: string}) { return <View style={styles.section} testID={testID}><Text style={styles.sectionTitle}>{title}</Text><Text style={styles.sectionText}>{text}</Text></View>; }
function signed(value: number) { return `${value >= 0 ? '+' : ''}${value}`; }
function list(values: string[], empty = '未提供') { return values.length ? values.join('、') : empty; }
function entries<T>(values: Record<string, T>, render: (value: T) => string) { const items = Object.entries(values); return items.length ? items.map(([key, value]) => `${key} ${render(value)}`).join(' · ') : '未提供'; }

const styles = StyleSheet.create({
  panel: {borderColor: '#C8D8D4', borderRadius: 16, borderWidth: 1, marginTop: 16, padding: 16}, title: {color: '#102A43', fontSize: 20, fontWeight: '800'}, copy: {color: '#52616B', lineHeight: 20, marginTop: 6}, sectionTitle: {color: '#102A43', fontWeight: '800', marginTop: 14}, choices: {gap: 8, marginTop: 8}, choice: {borderColor: '#C8D8D4', borderRadius: 10, borderWidth: 1, padding: 10}, choiceSelected: {backgroundColor: '#E5F6F3', borderColor: '#18A999', borderWidth: 2}, choiceLabel: {color: '#102A43', fontWeight: '800'}, choiceSummary: {color: '#52616B', marginTop: 2}, selectedPlan: {color: '#102A43', marginTop: 10}, missing: {color: '#B54708', marginTop: 10}, primaryButton: {backgroundColor: '#18A999', borderRadius: 10, marginTop: 12, padding: 12}, disabledButton: {backgroundColor: '#9CBFBA'}, primaryButtonText: {color: '#FFFFFF', fontWeight: '800', textAlign: 'center'}, sessions: {marginTop: 8}, session: {borderColor: '#C8D8D4', borderRadius: 10, borderWidth: 1, marginTop: 8, padding: 10}, comparison: {borderTopColor: '#C8D8D4', borderTopWidth: StyleSheet.hairlineWidth, marginTop: 18, paddingTop: 2}, mediaRow: {flexDirection: 'row', gap: 10, marginTop: 8}, workCard: {flex: 1}, workTitle: {color: '#102A43', fontWeight: '800'}, image: {backgroundColor: '#E8F0EE', height: 128, marginTop: 6, width: '100%'}, imageFallback: {alignItems: 'center', backgroundColor: '#E8F0EE', height: 128, justifyContent: 'center', marginTop: 6}, exif: {color: '#52616B', fontSize: 12, marginTop: 6}, exifUnavailable: {color: '#B54708', fontSize: 12, marginTop: 6}, delta: {color: '#102A43', fontSize: 16, fontWeight: '800', marginTop: 14}, section: {marginTop: 12}, sectionText: {color: '#263B4A', lineHeight: 20, marginTop: 3},
});
