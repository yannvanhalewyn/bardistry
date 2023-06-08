import {View, Text, Button} from 'react-native';
import {BottomSheetTextInput} from '@gorhom/bottom-sheet';
import colors from 'tailwindcss/colors';
import {styled} from 'nativewind';

const TextInput = styled(BottomSheetTextInput);

const Label = ({title}) => {
  return (
    <Text className="text-sm font-bold font-lato dark:text-gray-300">
      {title}
    </Text>
  );
};

const SongForm = ({song, onSheetClose, onSongEdit}) => {
  const Input = ({placeholder, k, defaultValue, autoFocus}) => {
    return (
      <TextInput
        autoFocus={autoFocus}
        className="mt-1 px-2 py-2 rounded bg-gray-200 dark:bg-gray-700 dark:text-white"
        placeholder={placeholder}
        key={k}
        onEndEditing={e => {
          const value = e.nativeEvent.text;
          // TODO hack to prevent bottom sheet to pop back up because of
          // re-render, in case this gets dispatched because of swiping the
          // sheet down.
          setTimeout(() => onSongEdit(song.id, k, value), 200);
        }}
        defaultValue={defaultValue}
      />
    );
  };

  return (
    <View className="px-4">
      <View>
        <Label title="Title" />
        <Input
          autoFocus={true}
          placeholder="Title"
          k="title"
          defaultValue={song.title}
        />
      </View>
      <View className="mt-2">
        <Label title="Artist" />
        <Input placeholder="Title" k="artist" defaultValue={song.artist} />
      </View>
    </View>
  );
};

export default SongForm;
