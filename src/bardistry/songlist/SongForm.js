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

const SongForm = ({song, onSheetClose, onEditTitle, onEditArtist}) => {
  const Input = ({placeholder, k, onChange, defaultValue, autoFocus}) => {
    return (
      <TextInput
        autoCorrect={false}
        autoFocus={autoFocus}
        className="mt-1 px-2 py-2 rounded bg-gray-200 dark:bg-gray-700 dark:text-white"
        placeholder={placeholder}
        key={k}
        selectTextOnFocus={true}
        selectionColor={colors.orange['500']}
        onEndEditing={e => {
          const value = e.nativeEvent.text;
          // TODO hack to prevent bottom sheet to pop back up because of
          // re-render, in case this gets dispatched because of swiping the
          // sheet down.
          setTimeout(() => onChange(value), 200);
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
          onChange={onEditTitle}
          defaultValue={song.title}
        />
      </View>
      <View className="mt-2">
        <Label title="Artist" />
        <Input
          placeholder="Title"
          k="artist"
          onChange={onEditArtist}
          defaultValue={song.artist}
        />
      </View>
    </View>
  );
};

export default SongForm;
