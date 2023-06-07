import {Suspense} from 'react';
import {View, Text} from 'react-native';
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

  return (
    <Suspense>
      <NavigationContainer
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
            options={({route}) => ({title: route.params.title,
                                    headerTintColor: colors.orange['500']})}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </Suspense>
  );
}

export default App;
