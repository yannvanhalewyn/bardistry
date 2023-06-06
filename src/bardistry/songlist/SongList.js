// Would like to use the Lato font.
import {View, Text, Pressable, FlatList, SafeAreaView} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import SearchBar from './SearchBar.js';

const Song = props => {
  const navigation = useNavigation();
  const song = props.item;

  return (
    <View className="bg-gray-100 dark:bg-gray-900">
      <Pressable
        className="px-4 py-4"
        onPress={() =>
          navigation.navigate('Lyrics', {id: song.id, title: song.title})
        }>
        <Text className="text-md dark:text-white font-bold">{song.title}</Text>
        <Text className="mt-1 text-orange-500">
          {song.artist}
        </Text>
      </Pressable>
    </View>
  );
};

const SongList = ({songs, onQueryChange}) => {
  return (
    <SafeAreaView className="px-4 bg-white dark:bg-black">
      <Text className="text-4xl mt-2 mx-4 font-black dark:text-white">Bardistry</Text>
      <SearchBar className="mt-4" onChangeText={onQueryChange} />

        <FlatList
          className="mx-4 mt-4 flex rounded-2xl border dark:border-gray-700"
          data={songs}
          renderItem={props => <Song {...props} />}
          ItemSeparatorComponent={<View className="border border-gray-700"></View>}
        />
    </SafeAreaView>
  );
};

export default SongList;
