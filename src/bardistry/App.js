import * as React from 'react';
import {View, Text, useColorScheme} from 'react-native';
import {NavigationContainer, DefaultTheme, DarkTheme} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

function App(props) {
  const color = useColorScheme();

  return (
    <NavigationContainer theme={color === "dark" ? DarkTheme : DefaultTheme}>
      <Stack.Navigator>
        <Stack.Screen
          name={props.screens[0].name}
          component={props.screens[0].component}
        />
        <Stack.Screen
          name={props.screens[1].name}
          component={props.screens[1].component}
          options={({ route }) => ({ title: route.params.title })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default App;
