import {useEffect, useRef} from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  useWindowDimensions,
} from 'react-native';
import {KeyboardAwareScrollView} from 'react-native-keyboard-aware-scroll-view';
import {PlusIcon} from 'react-native-heroicons/solid';
import {useHeaderHeight} from '@react-navigation/elements';
import {useNavigation} from '@react-navigation/native';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import colors from 'tailwindcss/colors';
import Section from './Section';

const Sections = ({
  song,
  onEdit,
  onDelete,
  setHighlight,
  onMeasure,
}) => {
  return (
    <View className="" style={{}}>
      {song.sections?.map(section => (
        <Section
          key={section.id}
          section={section}
          onLayout={event => onMeasure(section.id, event.nativeEvent.layout)}
          onEdit={onEdit}
          onDelete={onDelete}
          setHighlight={setHighlight}
        />
      ))}
    </View>
  );
};

const Lyrics = ({
  song,
  isSheetOpen,
  onEditArtist,
  onEditTitle,
  onSectionAdd,
  onSectionDelete,
  onSectionEdit,
  onSectionMeasure,
  onSheetClose,
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
        bounces={false}
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
            <Sections
              song={song}
              onMeasure={onSectionMeasure}
              onEdit={onSectionEdit}
              onDelete={onSectionDelete}
              setHighlight={setHighlight}
            />
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
