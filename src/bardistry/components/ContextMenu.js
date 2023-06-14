import ContextMenu from 'react-native-context-menu-view';

const Component = props => {
  const onPress = e => {
    const handler = props.actions[e.nativeEvent.index]?.onPress;

    if (handler) {
      handler(e)
    }

  };
  return <ContextMenu {...props} onPress={onPress} />;
};

export default Component;
