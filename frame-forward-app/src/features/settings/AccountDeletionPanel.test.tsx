import React from 'react';
import renderer, { act } from 'react-test-renderer';
import { AccountDeletionPanel } from './AccountDeletionPanel';

describe('AccountDeletionPanel', () => {
  it('keeps the account when either confirmation is cancelled and signs out only after final confirmation', async () => {
    const confirm = jest.fn();
    let tree: renderer.ReactTestRenderer;
    await act(async () => {
      tree = renderer.create(<AccountDeletionPanel onConfirm={confirm} />);
    });
    const password = tree!.root.findByProps({ testID: 'account-deletion-password' });
    await act(async () => {
      password.props.onChangeText('ValidPass1!');
    });
    await act(async () => {
      tree!.root.findByProps({ testID: 'start-account-deletion' }).props.onPress();
    });
    await act(async () => {
      tree!.root.findByProps({ testID: 'cancel-account-deletion' }).props.onPress();
    });
    expect(confirm).not.toHaveBeenCalled();
    await act(async () => {
      tree!.root.findByProps({ testID: 'start-account-deletion' }).props.onPress();
    });
    await act(async () => {
      tree!.root.findByProps({ testID: 'confirm-account-deletion' }).props.onPress();
    });
    expect(confirm).toHaveBeenCalledWith('ValidPass1!');
  });
});
