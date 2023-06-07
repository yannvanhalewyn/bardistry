// Would like to use the Lato font.
import {View, Text, Pressable, FlatList, SafeAreaView} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {SparklesIcon} from 'react-native-heroicons/solid';
import SearchBar from './SearchBar.js';
import colors from 'tailwindcss/colors';

const Song = props => {
  const navigation = useNavigation();
  const song = props.item;

  return (
    <View className="bg-gray-100 dark:bg-gray-900">
      <Pressable
        className="px-4 py-3"
        onPress={() =>
          navigation.navigate('Lyrics', {id: song.id, title: song.title})
        }>
        <Text className="font-lato text-md dark:text-white font-bold">{song.title}</Text>
        <Text className="font-lato mt-1 text-orange-500">{song.artist}</Text>
      </Pressable>
    </View>
  );
};

const SongList = ({songs, showClearSearch, onClearSearch, onQueryChange}) => {
  return (
    <SafeAreaView className="px-4 bg-white dark:bg-black">
      <View className="mx-4 mt-8 flex-row items-center">
        <SparklesIcon color={colors.orange['500']} />
        <Text className="ml-2 text-4xl font-black dark:text-white">
          Bardistry
        </Text>
      </View>
      <SearchBar
        className="mt-4"
        showClearSearch={showClearSearch}
        onClearSearch={onClearSearch}
        onChangeText={onQueryChange}
      />

      <FlatList
        className="mx-4 my-4 rounded-2xl border dark:border-gray-700"
        /* data={[]} */
        data={songs}
        renderItem={props => <Song {...props} />}
        ItemSeparatorComponent={
          <View className="border border-gray-700"></View>
        }
      />
    </SafeAreaView>
  );
};

export default SongList;
