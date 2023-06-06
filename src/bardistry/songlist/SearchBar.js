import {View, Text} from 'react-native';
import {Button, XStack, YStack} from 'tamagui';
import {Input} from 'tamagui';

const SearchBar = ({ style, onChangeText }) => {
  return (
    <View style={style} className="px-4">
      <Input
        className="dark:bg-gray-900 dark:border-gray-900 dark:text-white"
        placeholder="Search for titles, artists, ..."
        onChangeText={onChangeText}
      />
    </View>
  );
};

export default SearchBar;
