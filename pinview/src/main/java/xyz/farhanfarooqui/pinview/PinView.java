package xyz.farhanfarooqui.pinview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

public class PinView extends LinearLayout {

    private int mPinCount;
    private int mPinSize, mPinWidth, mPinHeight, mPinTextSize, mPinGap;
    private int mPinTextColor, mPinTextColorSelected;
    private int mPinBackground, mPinBackgroundFilled;
    private Typeface mPinTypeface;
    private InputMethodManager mKeyboardManager;

    private int currentPin;
    private OnPinCompletedListener mPinCompletedListener;


    public PinView(Context context) {
        super(context);
        init(null);
    }

    public PinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mKeyboardManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PinView);
            String fontName = a.getString(R.styleable.PinView_pFont);
            mPinCount = a.getInt(R.styleable.PinView_pCount, 6);
            mPinSize = a.getDimensionPixelSize(R.styleable.PinView_pSize, 100);
            mPinWidth = a.getDimensionPixelSize(R.styleable.PinView_pWidth, -1);
            mPinHeight = a.getDimensionPixelSize(R.styleable.PinView_pHeight, -1);
            mPinGap = a.getDimensionPixelSize(R.styleable.PinView_pGap, 30);
            mPinTextSize = a.getDimensionPixelSize(R.styleable.PinView_pTextSize, 16);
            mPinTextColor = a.getColor(R.styleable.PinView_pTextColor, Color.WHITE);
            mPinTextColorSelected = a.getColor(R.styleable.PinView_pTextColorSelected, Color.BLACK);
            mPinBackground = a.getResourceId(R.styleable.PinView_pBackground, R.drawable.background_pin);
            mPinBackgroundFilled = a.getResourceId(R.styleable.PinView_pBackgroundFilled, R.drawable.background_pin_filled);

            try {
                if (fontName != null) {
                    mPinTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mPinTypeface = null;
            }
            a.recycle();
        }
        setOrientation(HORIZONTAL);
        createPins();
    }

    private void createPins() {
        for (int i = 0; i < mPinCount; i++) {
            PinEditText pinEditText = new PinEditText(getContext());
            pinEditText.setId(i);
            if (mPinTypeface != null)
                pinEditText.setTypeface(mPinTypeface);
            pinEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPinTextSize);
            pinEditText.setGravity(Gravity.CENTER);
            pinEditText.setTextColor(mPinTextColor);
            pinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            pinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            pinEditText.setBackground(getDrawable(mPinBackground));
            pinEditText.setCursorVisible(false);
            pinEditText.setPadding(0, 0, 0, 0);

            if(mPinWidth == -1 || mPinHeight == -1) {
                mPinWidth = mPinSize;
                mPinHeight = mPinSize;
            }

            LayoutParams params = new LayoutParams(mPinSize, mPinSize);

            if (i != 0)
                params.setMargins(mPinGap, 0, 0, 0);

            pinEditText.setLayoutParams(params);
            addView(pinEditText);
        }
    }

    private Drawable getDrawable(int resourceId) {
        return getResources().getDrawable(resourceId);
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(HORIZONTAL);
    }

    private PinEditText nextPin() {
        return findViewById(currentPin + 1);
    }

    private PinEditText previousPin() {
        return findViewById(currentPin - 1);
    }

    public void setPinCompletedListener(OnPinCompletedListener listener) {
        mPinCompletedListener = listener;
    }

    /**
     * @return A {@link String} containing pin or null if all fields are not filled
     */
    public String getPin() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mPinCount; i++) {
            PinEditText pinEditText = findViewById(i);
            if (pinEditText.getText().toString().isEmpty()) {
                return null;
            } else {
                stringBuilder.append(pinEditText.getText().toString());
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Sets a pin to the {@link PinView}. Does nothing if the Pin length and pin count doesn't match
     *
     * @param pin Pin to show in the PinView
     */
    @SuppressLint("SetTextI18n")
    public void setPin(String pin) {
        char pins[] = pin.toCharArray();
        if (pins.length == mPinCount)
            for (int i = 0; i < pins.length; i++) {
                PinEditText pinEditText = findViewById(i);
                String pinValue = String.valueOf(pins[i]);
                pinEditText.setText(pinValue);
            }
    }

    @SuppressLint("AppCompatCustomView")
    private class PinEditText extends EditText {

        public PinEditText(Context context) {
            super(context);
        }

        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return new CustomInputConnection(super.onCreateInputConnection(outAttrs),
                    true);
        }

        /**
         * Sets the cursor at the end of the EditText
         */
        private void setCursorAtEnd() {
            if (!getText().toString().isEmpty()) {
                setSelection(1);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            setCursorAtEnd();
            return true;
        }

        @Override
        protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);

            if (focused) {
                currentPin = getId(); // if the EditText is currently focused set it to currentPin
            }

            if (getDrawable(mPinBackground) != null && getDrawable(mPinBackgroundFilled) != null) {
                if (focused) {
                    setCursorAtEnd();
                    setBackground(null);
                    setBackground(getDrawable(mPinBackground));
                    setTextColor(mPinTextColorSelected);
                } else {
                    if (getText().toString().isEmpty()) {
                        setBackground(null);
                        setBackground(getDrawable(mPinBackground));
                    } else {
                        setBackground(null);
                        setBackground(getDrawable(mPinBackgroundFilled));
                        setTextColor(mPinTextColor);
                    }
                }
            }
        }

        @Override
        protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
            if (getDrawable(mPinBackground) != null && getDrawable(mPinBackgroundFilled) != null) {
                if (getText().toString().isEmpty()) {
                    setBackground(null);
                    setBackground(getDrawable(mPinBackground));
                } else {
                    setTextColor(mPinTextColor);
                    setBackground(null);
                    setBackground(getDrawable(mPinBackgroundFilled));
                    if (nextPin() != null) { // There's a next pin
                        if (!nextPin().getText().toString().isEmpty()) { // the next pin isn't empty
                            clearAllFocus();
                            mKeyboardManager.hideSoftInputFromWindow(getWindowToken(), 0);
                        } else { // the next pin is empty, and it'll get focus
                            nextPin().requestFocus();
                        }
                    } else { // No more pin so clear all focus
                        clearAllFocus();
                        mKeyboardManager.hideSoftInputFromWindow(getWindowToken(), 0);
                    }

                    String pin = getPin();
                    if (pin != null && pin.length() == mPinCount) {
                        mPinCompletedListener.onPinCompleted(pin);
                    }
                }
            }
        }

        /**
         * Clears all focus from the PinView
         */
        private void clearAllFocus() {
            ViewGroup rootView = (ViewGroup) getRootView();
            int dfValue = rootView.getDescendantFocusability();
            rootView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            clearFocus();
            rootView.setDescendantFocusability(dfValue);
        }

        private class CustomInputConnection extends InputConnectionWrapper {

            CustomInputConnection(InputConnection target, boolean mutable) {
                super(target, mutable);
            }

            @Override
            public boolean sendKeyEvent(KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                    if (getText().toString().isEmpty()) {
                        if (previousPin() != null) {
                            previousPin().requestFocus();
                            return false; // avoid from triggering the delete twice
                        } else {
                            clearAllFocus();
                            mKeyboardManager.hideSoftInputFromWindow(getWindowToken(), 0);
                        }
                    }
                } else if (event.getAction() == KeyEvent.ACTION_DOWN && !getText().toString().isEmpty()) {
                    setText(String.valueOf(event.getNumber()));
                    return false; // avoid from setting text twice
                }
                return super.sendKeyEvent(event);
            }

            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                if (beforeLength == 1 && afterLength == 0) {
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }

    public interface OnPinCompletedListener {
        public void onPinCompleted(String pin);
    }
}
