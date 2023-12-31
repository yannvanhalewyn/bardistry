import {useRef} from 'react';
import {View, Text, Pressable, TextInput} from 'react-native';
import {useColorScheme} from 'nativewind';
import {XCircleIcon, MagnifyingGlassIcon} from 'react-native-heroicons/solid';
import colors from 'tailwindcss/colors';

const SearchBar = ({style, showClearSearch, onChangeText, onClearSearch}) => {
  const inputRef = useRef(null);
  const {colorScheme} = useColorScheme();
  const iconColor =
    colorScheme === 'dark' ? colors.gray['500'] : colors.gray['500'];

  return (
    <View
      style={style}
      className="mx-4 px-2 flex-row items-center border rounded-lg bg-gray-100 border-gray-200 dark:bg-gray-900 dark:border-gray-800">
      <MagnifyingGlassIcon size={20} color={iconColor} />
      <TextInput
        ref={inputRef}
        placeholderTextColor={colors.gray["500"]}
        autoCorrect={false}
        className="flex-grow ml-2 py-3 font-lato text-gray-500 dark:text-white"
        placeholder="Search for songs, artists, ..."
        onChangeText={onChangeText}
      />
      {showClearSearch ? (
        <Pressable
          className="p-2"
          onPress={() => {
            inputRef.current?.clear();
            inputRef.current?.focus();
            onClearSearch();
          }}>
          <XCircleIcon size={20} color={colors.gray['400']} />
        </Pressable>
      ) : null}
    </View>
  );
};

export default SearchBar;
