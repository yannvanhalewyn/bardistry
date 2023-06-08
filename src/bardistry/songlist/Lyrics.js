import React, {useEffect} from 'react';

import {View, Text, ScrollView, TextInput} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import colors from 'tailwindcss/colors'

const Lyrics = ({song, isSheetOpen, onSheetClose, onSongEdit}) => {
  const navigation = useNavigation();
  useEffect(
    x => {
      navigation.setOptions({title: song.title});
    },
    [song.title],
  );

  return (
    <>
      <ScrollView
        keyboardDismissMode="on-drag"
        className="bg-white dark:bg-black">
        <View className="px-4 pb-32">
          {song.processedLyrics.map(section => {
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
                  selectionColor={colors.orange['500']}
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

      <BottomModal height={160} isOpen={isSheetOpen} onClose={onSheetClose}>
        <SongForm
          onSongEdit={onSongEdit}
          song={song}
          onSheetClose={onSheetClose}
        />
      </BottomModal>
    </>
  );
};

export default Lyrics;
