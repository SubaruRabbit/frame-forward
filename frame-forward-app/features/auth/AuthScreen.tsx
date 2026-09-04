import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

export function AuthScreen() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const register = mode === 'register';
  const disabled = register ? !username || !email || !password : !identifier || !password;
  return (
    <View style={styles.screen} testID="login-screen">
      <Text style={styles.eyebrow}>FRAME FORWARD</Text>
      <Text style={styles.title}>{register ? '先建立你的摄影账户' : '登录后，拍得更好'}</Text>
      <Text style={styles.body}>
        {register ? '用一个你会记得的密码开始。' : '登录即可使用你的摄影教练、课程与作品记录。'}
      </Text>
      {register && (
        <>
          <Text style={styles.label}>用户名</Text>
          <TextInput
            testID="register-username"
            value={username}
            onChangeText={setUsername}
            autoCapitalize="none"
            style={styles.input}
          />
        </>
      )}
      <Text style={styles.label}>{register ? '邮箱' : '用户名或邮箱'}</Text>
      <TextInput
        testID="auth-identifier"
        value={register ? email : identifier}
        onChangeText={register ? setEmail : setIdentifier}
        autoCapitalize="none"
        keyboardType={register ? 'email-address' : 'default'}
        style={styles.input}
      />
      <Text style={styles.label}>密码</Text>
      <TextInput
        testID="auth-password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry
        autoComplete={register ? 'new-password' : 'current-password'}
        style={styles.input}
      />
      {register && (
        <Text style={styles.warning} testID="no-recovery-warning">
          首版不提供密码或账户恢复。请妥善保管密码。
        </Text>
      )}
      <Pressable
        accessibilityRole="button"
        accessibilityState={{ disabled }}
        disabled={disabled}
        style={[styles.primary, disabled && styles.disabled]}
      >
        <Text style={styles.primaryText}>{register ? '创建账户' : '登录'}</Text>
      </Pressable>
      <Pressable
        accessibilityRole="button"
        onPress={() => setMode(register ? 'login' : 'register')}
        style={styles.switch}
      >
        <Text style={styles.switchText}>{register ? '已有账户？登录' : '还没有账户？注册'}</Text>
      </Pressable>
    </View>
  );
}
const styles = StyleSheet.create({
  screen: { backgroundColor: '#F5F9F8', flex: 1, justifyContent: 'center', padding: 28 },
  eyebrow: { color: '#18A999', fontSize: 12, fontWeight: '800', letterSpacing: 2 },
  title: { color: '#102A43', fontSize: 32, fontWeight: '800', marginTop: 14 },
  body: { color: '#52616B', fontSize: 16, lineHeight: 24, marginTop: 10 },
  label: { color: '#102A43', fontWeight: '700', marginTop: 20 },
  input: {
    backgroundColor: '#FFF',
    borderColor: '#C8D8D4',
    borderRadius: 12,
    borderWidth: 1,
    color: '#102A43',
    fontSize: 16,
    marginTop: 8,
    minHeight: 48,
    paddingHorizontal: 14,
  },
  warning: { color: '#805B10', lineHeight: 20, marginTop: 16 },
  primary: {
    alignItems: 'center',
    backgroundColor: '#18A999',
    borderRadius: 12,
    marginTop: 26,
    minHeight: 50,
    justifyContent: 'center',
  },
  disabled: { backgroundColor: '#9ACFC8' },
  primaryText: { color: '#FFF', fontSize: 16, fontWeight: '800' },
  switch: { alignItems: 'center', marginTop: 18, minHeight: 44, justifyContent: 'center' },
  switchText: { color: '#147C70', fontWeight: '700' },
});
