import {useState} from 'react';

import {View, Text, TextInput, Button, InputAccessoryView} from 'react-native';
import ContextMenu from '../components/ContextMenu';
import colors from 'tailwindcss/colors';

const Section = ({section, onEdit, onDelete, setHighlight}) => {
  const [isEditing, setIsEditing] = useState(false);

  const contextMenu = [
    {
      title: 'Edit',
      systemIcon: 'pencil',
      onPress: e => setIsEditing(true),
    },
    section.highlight
      ? {
          title: 'Unhighlight',
          systemIcon: 'eye.slash',
          onPress: e => setHighlight(section.id, false),
        }
      : {
          title: 'Highlight',
          systemIcon: 'eye',
          onPress: e => setHighlight(section.id, true),
        },
    {
      title: 'Delete',
      destructive: true,
      systemIcon: 'trash',
      onPress: e => onDelete(section.id),
    },
  ];

  const hlClassNames = 'm-4 rounded-lg bg-gray-100 dark:bg-gray-900';

  const TextComponent = isEditing ? TextInput : Text;

  return (
    <ContextMenu actions={contextMenu}>
      <View
        key={section.id}
        className={
          'py-2 ' +
          (section.highlight ? hlClassNames : '') +
          // pt-1 because multiline={true} adds some top padding.
          (isEditing ? ' -pt-0' : '')
        }>
        <TextComponent
          selectionColor={colors.orange['500']}
          multiline={true}
          autoFocus={true}
          inputAccessoryViewID="lyrics.keyboard-edit-button"
          className="px-4 text-lg font-lato dark:text-white"
          onEndEditing={e => {
            onEdit(section.id, e.nativeEvent.text);
          }}
          onSelectionChange={e => {
            console.log('selection change', e.nativeEvent.selection);
          }}
          onBlur={e => setIsEditing(false)}>
          {section.title ? (
            <TextComponent className="font-bold dark:text-white">
              {section.title + '\n'}
            </TextComponent>
          ) : null}
          {section.body}
        </TextComponent>
      </View>
    </ContextMenu>
  );
};

const KeyboardEditButtons = () => {
  return (
    <InputAccessoryView nativeID="lyrics.keyboard-edit-buttons">
      <Button onPress={() => alert("hi")} title="Clear Text" />
    </InputAccessoryView>
  );
};

export { Section, KeyboardEditButtons };
