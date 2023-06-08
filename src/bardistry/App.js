import {Suspense, useState, useEffect} from 'react';
import {Linking, View, Text, TouchableOpacity} from 'react-native';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import {BottomSheetModalProvider} from '@gorhom/bottom-sheet';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {useColorScheme} from 'nativewind';
import colors from 'tailwindcss/colors';

import {
  NavigationContainer,
  DefaultTheme,
  DarkTheme,
} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

const EditButton = ({onPress}) => {
  return (
    <TouchableOpacity onPress={onPress}>
      <Text className="text-blue-500 font-bold font-lato">Edit</Text>
    </TouchableOpacity>
  );
};

function App({screens, navigationRef, onEditPress}) {
  const {colorScheme} = useColorScheme();
  const [isReady, setIsReady] = useState(false);
  const [initialState, setInitialState] = useState();

  useEffect(() => {
    const restoreState = async () => {
      try {
        const initialUrl = await Linking.getInitialURL();

        if (Platform.OS !== 'web' && initialUrl == null) {
          const savedStateString = await AsyncStorage.getItem(
            'bardistry.navigation-state',
          );
          if (savedStateString) {
            setInitialState(JSON.parse(savedStateString));
          }
        }
      } finally {
        setIsReady(true);
      }
    };

    restoreState();
  }, []);

  if (!isReady) {
    return null;
  }

  return (
    <Suspense>
      <GestureHandlerRootView style={{flex: 1}}>
        <BottomSheetModalProvider>
          <NavigationContainer
            ref={navigationRef}
            initialState={initialState}
            onStateChange={state => {
              AsyncStorage.setItem(
                'bardistry.navigation-state',
                JSON.stringify(state),
              );
            }}
            theme={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
            <Stack.Navigator>
              <Stack.Screen
                options={{headerShown: false}}
                name={screens[0].name}
                component={screens[0].component}
              />
              <Stack.Screen
                name={screens[1].name}
                component={screens[1].component}
                options={{
                  headerTintColor: colors.orange['500'],
                  headerRight: () => <EditButton onPress={onEditPress} />,
                }}
              />
            </Stack.Navigator>
          </NavigationContainer>
        </BottomSheetModalProvider>
      </GestureHandlerRootView>
    </Suspense>
  );
}

export default App;
