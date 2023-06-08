import {Suspense, useState, useEffect} from 'react';
import {Linking, View, Text} from 'react-native';
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

function App(props) {
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
      <NavigationContainer
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
            name={props.screens[0].name}
            component={props.screens[0].component}
          />
          <Stack.Screen
            name={props.screens[1].name}
            component={props.screens[1].component}
            options={({route}) => ({
              title: route.params.title,
              headerTintColor: colors.orange['500'],
            })}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </Suspense>
  );
}

export default App;
