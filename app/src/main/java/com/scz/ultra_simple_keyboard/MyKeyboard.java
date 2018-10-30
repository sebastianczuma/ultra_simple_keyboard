package com.scz.ultra_simple_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class MyKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView kv;
    private Keyboard keyboard;
    private Keyboard symbolsKeyboard;
    private Keyboard shiftSymbolsKeyboard;
    private boolean caps = false;

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(com.scz.ultra_simple_keyboard.R.layout.keyboard, null);
        kv.setPreviewEnabled(false);
        keyboard = new Keyboard(this, com.scz.ultra_simple_keyboard.R.xml.qwerty);
        symbolsKeyboard = new Keyboard(this, com.scz.ultra_simple_keyboard.R.xml.symbols);
        shiftSymbolsKeyboard = new Keyboard(this, com.scz.ultra_simple_keyboard.R.xml.symbols_shift);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                assert am != null;
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (kv != null) {
                    Keyboard current = kv.getKeyboard();
                    if (current == symbolsKeyboard) {
                        kv.setKeyboard(shiftSymbolsKeyboard);
                    } else if (current == shiftSymbolsKeyboard) {
                        kv.setKeyboard(symbolsKeyboard);
                    } else {
                        caps = !caps;
                        keyboard.setShifted(caps);
                        kv.invalidateAllKeys();
                    }
                }
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                if (kv != null) {
                    Keyboard current = kv.getKeyboard();
                    if (current == symbolsKeyboard || current == shiftSymbolsKeyboard) {
                        current = keyboard;
                    } else {
                        current = symbolsKeyboard;
                    }
                    kv.setKeyboard(current);
                    if (current == symbolsKeyboard) {
                        current.setShifted(false);
                    }
                }
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
}