// Would like to use the Lato font.
import React, {useEffect} from 'react';
import {View, Text, Pressable, FlatList, SafeAreaView} from 'react-native';
import {useNavigation} from '@react-navigation/native';

const ListItem = props => {
  const navigation = useNavigation();
  const song = props.item;

  return (
    <Pressable className="px-4 py-2" onPress={() => navigation.navigate("Lyrics", {id: song.id, title: song.title})}>
      <Text className="text-xl dark:text-white font-bold">{song.title}</Text>
      <Text className="text-xl leading-5 text-orange-500 font-bold">
        {song.artist}
      </Text>
    </Pressable>
  );
};

const SongList = ({songs}) => {
  return (
    <SafeAreaView >
      <Text className="mx-4 text-4xl font-bold dark:text-white">My Songs</Text>
      <View className="mt-2 border border-orange-500" />
      <FlatList
        className=""
        data={songs}
        renderItem={props => <ListItem {...props} />}
      />
    </SafeAreaView>
  );
};

export default SongList;
