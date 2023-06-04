// Would like to use the Lato font.
import React, {useEffect} from 'react';
import {View, Text, FlatList, SafeAreaView} from 'react-native';

const renderItem = props => {
  const song = props.item;
  return (
    <View className="px-4 py-2">
      <Text className="text-xl dark:text-white font-bold">{song.title}</Text>
      <Text className="text-xl leading-5 text-orange-500 font-bold">{song.artist}</Text>
      <Text className="text-md mt-2 leading-5 dark:text-white">{song.contents}</Text>
    </View>
  );
};

const Component = props => {
  console.log('Rendering bardistry/Component.js', "songs:", props.songs?.length);

  return (
    <SafeAreaView className="dark:bg-black">
      <Text className="mx-4 text-4xl font-bold dark:text-white">My Songs</Text>
      <View className="mt-2 border border-orange-500"/>
      <FlatList className="" data={props.songs} renderItem={renderItem} />
    </SafeAreaView>
  );
};

export default Component;
