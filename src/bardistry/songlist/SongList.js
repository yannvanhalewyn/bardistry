import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  SafeAreaView,
} from 'react-native';
import {SparklesIcon} from 'react-native-heroicons/solid';
import SearchBar from './SearchBar.js';
import colors from 'tailwindcss/colors';

const Song = props => {
  const song = props.item;

  return (
    <TouchableOpacity
      className="px-4 py-3"
      onPress={() =>
        props.onPress(song.id)
      }>
      <Text className="font-lato text-md dark:text-white font-bold">
        {song.title}
      </Text>
      <Text className="font-lato mt-1 text-orange-500">{song.artist}</Text>
    </TouchableOpacity>
  );
};

const SongList = ({
  songs,
  isLoading,
  loadSongs,
  showClearSearch,
  onQueryChange,
  onClearSearch,
  onSongPress,
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
        renderItem={props => <Song {...props} onPress={onSongPress} />}
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
