import {useRef} from 'react';
import {View, Text, Pressable} from 'react-native';
import {useColorScheme} from 'nativewind';
import {Input} from 'tamagui';
import {XCircleIcon, MagnifyingGlassIcon} from 'react-native-heroicons/solid';
import colors from 'tailwindcss/colors';

const SearchBar = ({style, showClearSearch, onChangeText, onClearSearch}) => {
  const inputRef = useRef(null);
  const {colorScheme} = useColorScheme();
  const iconColor =
    colorScheme === 'dark' ? colors.gray['500'] : colors.gray['300'];

  return (
    <View
      style={style}
      className="mx-4 px-2 flex-row items-center rounded-lg dark:bg-gray-900 dark:border-gray-900">
      <MagnifyingGlassIcon size={20} color={iconColor} />
      <Input
        ref={inputRef}
        unstyled={true}
        className="flex-grow ml-2 py-4 dark:text-white"
        placeholder="Search for titles, artists, ..."
        onChangeText={onChangeText}
      />
      {showClearSearch ? (
        <Pressable
          className="p-3"
          onPress={() => {
            inputRef.current?.clear();
            onClearSearch();
          }}>
          <XCircleIcon size={20} color={iconColor} />
        </Pressable>
      ) : null}
    </View>
  );
};

export default SearchBar;
