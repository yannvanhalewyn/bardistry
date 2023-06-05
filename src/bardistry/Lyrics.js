import React from 'react';
import {View, Text, ScrollView} from 'react-native';
import {useNavigation} from '@react-navigation/native';

const Lyrics = props => {
  return (
    <ScrollView className="px-4 dark:bg-black">
      <Text className="text-md mt-2 leading-5 dark:text-white">
        {props.song.contents}
      </Text>
    </ScrollView>
  );
};

export default Lyrics;
