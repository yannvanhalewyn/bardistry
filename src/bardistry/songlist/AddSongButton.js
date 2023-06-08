import {useState} from 'react';
import {View, Text, TouchableOpacity, Button} from 'react-native';
import {useNavigation} from '@react-navigation/native';
import * as Icons from 'react-native-heroicons/solid';
import {FAB} from '@rneui/themed';

const AddSongButton = ({onPress}) => {
  const [visible, setVisible] = useState(true);

  const navigation = useNavigation();

  return (
    <TouchableOpacity
      visible={visible}
      className="flex-row items-center bg-orange-500 rounded-lg px-3 py-1"
      onPress={onPress}>
      <Text className="font-bold text-sm dark:text-white">+ Add Song</Text>
    </TouchableOpacity>
  );
};

export default AddSongButton;
