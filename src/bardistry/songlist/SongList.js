import {View, Text, Pressable, FlatList, SafeAreaView} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import {SparklesIcon} from 'react-native-heroicons/solid';
import SearchBar from './SearchBar.js';
import colors from 'tailwindcss/colors';

const Song = props => {
  const navigation = useNavigation();
  const song = props.item;

  return (
    <Pressable
      className="px-4 py-3"
      onPress={() =>
        navigation.navigate('Lyrics', {id: song.id, title: song.title})
      }>
      <Text className="font-lato text-md dark:text-white font-bold">
        {song.title}
      </Text>
      <Text className="font-lato mt-1 text-orange-500">{song.artist}</Text>
    </Pressable>
  );
};

const SongList = ({
  songs,
  isLoading,
  loadSongs,
  showClearSearch,
  onClearSearch,
  onQueryChange,
}) => {
  return (
    <SafeAreaView className="px-4 flex-1 bg-white dark:bg-black">
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
        className="mx-4 my-4 rounded-lg bg-gray-50 border bg-gray-100 border-gray-200 dark:bg-gray-900 dark:border-gray-800"
        data={songs}
        renderItem={props => <Song {...props} />}
        ItemSeparatorComponent={
          <View className="border-0.5 border-gray-200 dark:border-gray-800"></View>
        }
        refreshing={isLoading}
        onRefresh={loadSongs}
      />
    </SafeAreaView>
  );
};

export default SongList;
