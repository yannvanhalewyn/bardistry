import React from 'react';
import {View, Text, ScrollView} from 'react-native';
import {useNavigation} from '@react-navigation/native';

const Lyrics = props => {
  return (
    <ScrollView className="bg-white dark:bg-black">
      <View className="px-4 pb-32">
        {props.song.processedLyrics.map(section => {
          const highlight = section['chorus?'];
          const hlClassNames = ' p-4 rounded-2xl bg-gray-100 dark:bg-gray-900';

          return (
            <View
              key={section.id}
              className={'mt-6' + (highlight ? hlClassNames : '')}>
              {section.title ? (
                <Text className="font-bold dark:text-white">{section.title}</Text>
              ) : null}
              <Text className="text-lg dark:text-white">{section.lines}</Text>
            </View>
          );
        })}
      </View>
    </ScrollView>
  );
};

export default Lyrics;
