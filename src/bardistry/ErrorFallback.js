import {View, Text, TouchableOpacity} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {ExclamationTriangleIcon} from 'react-native-heroicons/solid';
import colors from 'tailwindcss/colors';

const ErrorFallback = props => {
  const handleBack = async () => {
    await AsyncStorage.removeItem('bardistry.navigation-state');
    props.reset();
  };

  return (
    <View className="flex-1 justify-center items-center">
      <ExclamationTriangleIcon color={colors.orange['500']} size={72} />
      <Text className="mt-4 font-bold text-xl dark:text-white">
        Whoops, something went wrong...
      </Text>
      <TouchableOpacity className="mt-4" onPress={handleBack}>
        <Text className="text-center text-orange-500 font-bold text-lg">
          Reload the app
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default ErrorFallback;
