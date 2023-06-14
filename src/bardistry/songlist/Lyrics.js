import React, {useState, useEffect} from 'react';

import {View, Text, TextInput} from 'react-native';
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view'
import {useNavigation} from '@react-navigation/native';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import colors from 'tailwindcss/colors';
import ContextMenu from '../components/ContextMenu';

const Section = ({section, onEdit}) => {
  const [isEditing, setIsEditing] = useState(false);

  const contextMenu = [
    {
      title: 'Edit',
      systemIcon: 'pencil',
      onPress: e => setIsEditing(true),
    },
    {
      title: 'Highlight',
      systemIcon: 'eye',
      onPress: e => console.log('Highlight pressed'),
    },
    {
      title: 'Append section',
      systemIcon: 'text.append',
      onPress: e => console.log('Edit pressed'),
    },
    {
      title: 'Delete',
      destructive: true,
      systemIcon: 'trash',
      onPress: e => console.log('Edit pressed'),
    },
  ];

  const hlClassNames = ' m-4 rounded-lg bg-gray-100 dark:bg-gray-900';

  const TextComponent = isEditing ? TextInput : Text;

  return (
    <ContextMenu actions={contextMenu}>
      <View
        key={section.id}
        className={
          'px-4 py-2' +
          (section.isChorus ? hlClassNames : '') +
          // pt-1 because multiline={true} adds some top padding.
          (isEditing ? ' -pt-0' : '')
        }>
        <TextComponent
          selectionColor={colors.orange['500']}
          multiline={true}
          autoFocus={true}
          className="text-lg font-lato dark:text-white"
          onEndEditing={e => {
            console.log('js:onEndEditing', section.id);
            onEdit(section.id, e.nativeEvent.text);
          }}
          onBlur={e => setIsEditing(false)}>
          {section.title ? (
            <TextComponent className="font-bold dark:text-white">
              {section.title + '\n'}
            </TextComponent>
          ) : null}
          {section.body}
        </TextComponent>
      </View>
    </ContextMenu>
  );
};

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
      <KeyboardAwareScrollView
        extraScrollHeight={64}
        keyboardDismissMode="on-drag"
        className="bg-white dark:bg-black">
        <View className="pt-4 pb-16">
          <TextInput
            className="mx-4 text-2xl font-bold dark:text-white"
            selectTextOnFocus={true}>
            {song.title}
          </TextInput>

          <TextInput
            className="mx-4 text-xl text-orange-500"
            selectTextOnFocus={true}>
            {song.artist}
          </TextInput>

          <View className="mt-2">
            {song.sections.map(section => (
              <Section section={section} onEdit={onSectionEdit} />
            ))}
          </View>
        </View>
      </KeyboardAwareScrollView>

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
