import React, {useEffect} from 'react';

import {View, Text, ScrollView, TextInput} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import colors from 'tailwindcss/colors';
import {HoldItem} from 'react-native-hold-menu';

const Lyrics = ({
  song,
  isSheetOpen,
  onSheetClose,
  onSongEdit,
  onSectionEdit,
}) => {
  const navigation = useNavigation();
  useEffect(
    x => {
      navigation.setOptions({title: '. . .'});
    },
    [song.title],
  );

  return (
    <>
      <ScrollView
        keyboardDismissMode="on-drag"
        className="bg-white dark:bg-black">
        <View className="px-4 pt-4 pb-16">
          <TextInput
            className="text-2xl font-bold dark:text-white"
            selectTextOnFocus={true}>
            {song.title}
          </TextInput>

          <TextInput
            className="text-xl text-orange-500"
            selectTextOnFocus={true}>
            {song.artist}
          </TextInput>

          <View className="mt-2">
            {song.sections.map(section => {
              // pt-1 because multiline={true} adds some top padding.
              const hlClassNames =
                ' p-4 pt-4 rounded-lg bg-gray-100 dark:bg-gray-900';

              return (
                <HoldItem
                  items={[
                    {text: 'Reply', onPress: () => {}},
                    {text: 'Edit', onPress: () => {}},
                    {text: 'Delete', destructive: true, onPress: () => {}},
                  ]}>
                  <View
                    key={section.id}
                    className={'my-2' + (section.isChorus ? hlClassNames : '')}>
                    <Text
                      selectionColor={colors.orange['500']}
                      multiline={true}
                      className="text-lg font-lato dark:text-white"
                      onEndEditing={e => {
                        console.log('js:onEndEditing', section.id);
                        onSectionEdit(section.id, e.nativeEvent.text);
                      }}>
                      {section.title ? (
                        <Text className="font-bold dark:text-white">
                          {section.title + '\n'}
                        </Text>
                      ) : null}
                      {section.body}
                    </Text>
                  </View>
                </HoldItem>
              );
            })}
          </View>
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
