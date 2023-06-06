// Would like to use the Lato font.
import {View, Text, Pressable, FlatList, SafeAreaView} from 'react-native';
import {useNavigation} from '@react-navigation/native';

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

const SongList = ({songs}) => {
  return (
    <SafeAreaView className="px-4 bg-white dark:bg-black">
      <View className="mx-4 mt-4 rounded-2xl border dark:border-gray-700">
        <FlatList
          className=""
          data={songs}
          renderItem={props => <Song {...props} />}
          /* ItemSeparatorComponent={<Separator alignSelf="stretch" backgroundColor="red" horizontal/>} */
          ItemSeparatorComponent={<View className="border border-gray-700"></View>}
        />
      </View>
    </SafeAreaView>
  );
};

export default SongList;
