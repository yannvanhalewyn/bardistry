import React from 'react';
import {View, Text, ScrollView, TextInput} from 'react-native';
import {useNavigation} from '@react-navigation/native';
const DismissKeyboard = ({children}) => (
    <TouchableWithoutFeedback onPress={() => Keyboard.dismiss()}>{children}</TouchableWithoutFeedback>
);

const Lyrics = props => {
  return (
    <ScrollView
      keyboardDismissMode="on-drag"
      className="bg-white dark:bg-black">
      <View className="px-4 pb-32">
        {props.song.processedLyrics.map(section => {
          const highlight = section['chorus?'];
          const hlClassNames = ' p-4 rounded-lg bg-gray-100 dark:bg-gray-900';

          return (
            <View
              key={section.id}
              className={'mt-6' + (highlight ? hlClassNames : '')}>
              {section.title ? (
                <Text className="font-bold dark:text-white">
                  {section.title}
                </Text>
              ) : null}
              <TextInput
                multiline={true}
                className="text-lg font-lato dark:text-white"
                onChangeText={text => console.log(text)}>
                {section.body}
              </TextInput>
            </View>
          );
        })}
      </View>
    </ScrollView>
  );
};

export default Lyrics;
