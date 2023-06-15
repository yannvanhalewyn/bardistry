import React, {useEffect, useMemo, useRef} from 'react';
import {BottomSheetModal} from '@gorhom/bottom-sheet';
import {useColorScheme} from 'nativewind';
import colors from 'tailwindcss/colors';

const BottomModal = ({isOpen, onClose, height, children}) => {
  const bottomSheetModal = useRef(null);
  const {colorScheme} = useColorScheme();
  const snapPoints = useMemo(() => [height], [height]);

  useEffect(() => {
    if (isOpen) {
      bottomSheetModal.current?.present();
    } else {
      bottomSheetModal.current?.dismiss();
    }
  }, [isOpen]);

  return (
    <BottomSheetModal
      ref={bottomSheetModal}
      index={0}
      snapPoints={snapPoints}
      onDismiss={onClose}
      backgroundStyle={{
        backgroundColor:
          colorScheme === 'dark' ? colors.gray['900'] : colors.gray['100'],
      }}
      handleIndicatorStyle={{
        backgroundColor:
          colorScheme === 'dark' ? colors.gray['500'] : colors.gray['400'],
      }}>
      {children}
    </BottomSheetModal>
  );
};

export default BottomModal;
