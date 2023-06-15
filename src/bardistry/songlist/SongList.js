import {
  View,
  Text,
  TouchableOpacity,
  FlatList,
  SafeAreaView,
} from 'react-native';
import {SparklesIcon} from 'react-native-heroicons/solid';
import BottomModal from './BottomModal.js';
import SongForm from './SongForm.js';
import SearchBar from './SearchBar.js';
import colors from 'tailwindcss/colors';
import AddSongButton from './AddSongButton';
import ContextMenu from '../components/ContextMenu';

const Song = ({item, onPress, onDelete}) => {
  const song = item;

  return (
    <ContextMenu
      actions={[
        {
          title: 'Add to collection',
          systemIcon: 'list.bullet',
          onPress: () => console.log('add to collection'),
        },
        {
          title: 'Edit labels',
          systemIcon: 'tag',
          onPress: () => console.log('edit labels'),
        },
        {
          title: 'Delete',
          destructive: true,
          systemIcon: 'trash',
          onPress: () => onDelete(song.id),
        },
      ]}>
      <TouchableOpacity
        className="px-4 py-3"
        // Prevent long press to navigate on Android
        onLongPress={() => null}
        onPress={() => onPress(song.id)}>
        <Text className="font-lato text-md dark:text-white font-bold">
          {song.title}
        </Text>
        <Text className="font-lato mt-1 text-orange-500">{song.artist}</Text>
      </TouchableOpacity>
    </ContextMenu>
  );
};

const SongList = ({
  songs,
  isLoading,
  loadSongs,
  onQueryChange,
  onClearSearch,
  showClearSearch,
  isFormOpen,
  onOpenForm,
  onCloseForm,
  onCreateSong,
  onDeleteSong,
  onSongPress,
}) => {

  return (
    <>
      <SafeAreaView className="px-4 flex-1 bg-white dark:bg-black">
        <View className="mx-4 mt-8 flex-row items-center">
          <SparklesIcon color={colors.orange['500']} />
          <Text className="ml-2 flex-grow text-4xl font-black dark:text-white">
            Bardistry
          </Text>
          <AddSongButton onPress={onOpenForm} />
        </View>
        <SearchBar
          className="mt-4"
          showClearSearch={showClearSearch}
          onClearSearch={onClearSearch}
          onChangeText={onQueryChange}
        />
        <FlatList
          className="mx-4 my-4 rounded-lg bg-gray-50 border bg-gray-100 border-gray-200 dark:bg-gray-900 dark:border-gray-800"
          data={songs}
          renderItem={props => (
            <Song {...props} onPress={onSongPress} onDelete={onDeleteSong} />
          )}
          ItemSeparatorComponent={
            <View className="border-0.5 border-gray-200 dark:border-gray-800"></View>
          }
          refreshing={isLoading}
          onRefresh={loadSongs}
        />
      </SafeAreaView>

      <BottomModal height={200} isOpen={isFormOpen} onClose={onCloseForm} >
        <SongForm onSubmit={onCreateSong} />
      </BottomModal>
    </>
  );
};

export default SongList;
