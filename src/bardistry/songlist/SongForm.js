import {useState, useRef} from 'react';
import {View, Text, TouchableOpacity} from 'react-native';
import {BottomSheetTextInput} from '@gorhom/bottom-sheet';
import colors from 'tailwindcss/colors';
import {styled} from 'nativewind';

const TextInput = styled(BottomSheetTextInput);

const SongForm = ({onSubmit}) => {
  const state = useRef({title: '', artist: ''});
  const inputs = [useRef(null), useRef(null)];
  const setState = f => (state.current = f(state.current));

  const Input = ({
    reff,
    placeholder,
    onChange,
    defaultValue,
    autoFocus,
    separator,
  }) => {
    return (
      <TextInput
        ref={reff}
        autoCorrect={false}
        autoFocus={autoFocus}
        className={
          'px-3 py-3 dark:text-white' +
          (separator ? ' border-b border-gray-200 dark:border-gray-800' : '')
        }
        placeholder={placeholder}
        selectTextOnFocus={true}
        selectionColor={colors.orange['500']}
        onChange={e => onChange(e.nativeEvent.text)}
        defaultValue={defaultValue}
      />
    );
  };

  return (
    <View className="px-4 mt-2">
      <View className="rounded-lg border-0.5 border-gray-200 dark:border-gray-800 bg-gray-200 dark:bg-gray-700">
        <Input
          reff={inputs[0]}
          separator={true}
          autoFocus={false}
          placeholder="Title"
          k="title"
          onChange={title =>
            setState(state => Object.assign(state, {title: title}))
          }
        />
        <Input
          reff={inputs[1]}
          placeholder="Artist"
          onChange={artist =>
            setState(state => Object.assign(state, {artist: artist}))
          }
        />
      </View>

      <TouchableOpacity
        onPress={() => {
          inputs[0].current?.blur();
          inputs[1].current?.blur();
          onSubmit(state.current);
        }}
        className="mt-4 px-4 py-2 bg-orange-500 rounded-lg">
        <Text className="text-center text-base font-bold text-white">
          Add Song
        </Text>
      </TouchableOpacity>
    </View>
  );
};

export default SongForm;
