import * as React from 'react';
import {View, Text} from 'react-native';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import Component from './Component.js';

const Stack = createNativeStackNavigator();

function App(props) {
  // console.log(props.screens)
  return (
    <NavigationContainer>
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
        {/* {props.screens.map(screen => { */}
        {/*   return ( */}
        {/*     <Stack.Screen name={screen.name} component={screen.component} /> */}
        {/*   ); */}
        {/* })} */}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

export default App;
