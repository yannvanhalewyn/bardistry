import React, {useState, useEffect} from 'react';

import {View, Text, TextInput, TouchableOpacity} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {PlusIcon} from 'react-native-heroicons/solid';
import {useNavigation} from '@react-navigation/native';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import colors from 'tailwindcss/colors';
import ContextMenu from '../components/ContextMenu';

const Section = ({section, onEdit, onDelete, setHighlight}) => {
  const [isEditing, setIsEditing] = useState(false);

  const contextMenu = [
    {
      title: 'Edit',
      systemIcon: 'pencil',
      onPress: e => setIsEditing(true),
    },
    section.highlight
      ? {
          title: 'Unhighlight',
          systemIcon: 'eye.slash',
          onPress: e => setHighlight(section.id, false),
        }
      : {
          title: 'Highlight',
          systemIcon: 'eye',
          onPress: e => setHighlight(section.id, true),
        },
    {
      title: 'Delete',
      destructive: true,
      systemIcon: 'trash',
      onPress: e => onDelete(section.id),
    },
  ];

  const hlClassNames = 'm-4 rounded-lg bg-gray-100 dark:bg-gray-900';

  const TextComponent = isEditing ? TextInput : Text;

  return (
    <ContextMenu actions={contextMenu}>
      <View
        key={section.id}
        className={
          'py-2 ' +
          (section.highlight ? hlClassNames : '') +
          // pt-1 because multiline={true} adds some top padding.
          (isEditing ? ' -pt-0' : '')
        }>
        <TextComponent
          selectionColor={colors.orange['500']}
          multiline={true}
          autoFocus={true}
          className="px-4 text-lg font-lato dark:text-white"
          onEndEditing={e => {
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
  onEditTitle,
  onEditArtist,
  onSheetClose,
  onSectionAdd,
  onSectionEdit,
  onSectionDelete,
  setHighlight,
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
        key={song.id}
        extraScrollHeight={64}
        keyboardDismissMode="interactive"
        className="bg-white dark:bg-black">
        <View className="pt-4 pb-16">
          <TextInput
            onEndEditing={e => onEditTitle(e.nativeEvent.text)}
            className="px-4 text-2xl font-bold dark:text-white"
            selectionColor={colors.orange['500']}
            selectTextOnFocus={true}>
            {song.title}
          </TextInput>

          <TextInput
            onEndEditing={e => onEditArtist(e.nativeEvent.text)}
            className="px-4 text-xl text-orange-500"
            selectionColor={colors.orange['500']}
            selectTextOnFocus={true}>
            {song.artist}
          </TextInput>

          <View className="mt-2">
            {song.sections.map(section => (
              <Section
                key={section.id}
                section={section}
                onEdit={onSectionEdit}
                onDelete={onSectionDelete}
                setHighlight={setHighlight}
              />
            ))}
          </View>

          <TouchableOpacity
            onPress={onSectionAdd}
            className="mt-2 flex-row items-center justify-center">
            <PlusIcon size={16} color={colors.gray['500']} />
            <Text className="text-gray-500 text-base">Add section</Text>
          </TouchableOpacity>
        </View>
      </KeyboardAwareScrollView>

      <BottomModal height={160} isOpen={isSheetOpen} onClose={onSheetClose}>
        <SongForm
          song={song}
          onEditTitle={onEditTitle}
          onEditArtist={onEditArtist}
          onSheetClose={onSheetClose}
        />
      </BottomModal>
    </>
  );
};

const Component = props => {
  if (!props.song) {
    return null;
  }
  return <Lyrics {...props} />;
};

export default Component;
