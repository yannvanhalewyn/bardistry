import React from 'react';
import {View, Text, ScrollView} from 'react-native';
import {useNavigation} from '@react-navigation/native';

const Lyrics = props => {
  return (
    <ScrollView className="px-4 ">
      {props.song.contents.map(section => {
        return (
          <View className="mt-6">
            {section.title ? (
              <Text className="font-bold dark:text-white">{section.title}</Text>
            ) : null}
            <Text className="text-lg dark:text-white">{section.lines}</Text>
          </View>
        );
      })}
    </ScrollView>
  );
};

export default Lyrics;
