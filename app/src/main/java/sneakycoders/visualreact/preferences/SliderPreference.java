package sneakycoders.visualreact.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

import sneakycoders.visualreact.R;

public class SliderPreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener {
    // Schemas and attributes
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/lib/sneakycoders.visualreact.utils";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";
    // Default values
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_VALUE = 50;
    // Values
    final private int minValue;
    final private int maxValue;
    final private int defaultValue;
    private int currentValue;
    // View
    private TextView valueText;

    // Constructor
    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        minValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        maxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        defaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_VALUE);
    }

    @Override
    protected View onCreateDialogView() {
        // Integer format instance
        NumberFormat intFormat = NumberFormat.getIntegerInstance();

        // Get current value from settings
        currentValue = getPersistedInt(defaultValue);

        // Inflate layout
        View view = View.inflate(getContext(), R.layout.dialog_slider, null);

        // Put minimum and maximum
        ((TextView) view.findViewById(R.id.min_value)).setText(intFormat.format(minValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(intFormat.format(maxValue));

        // Setup SeekBar
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(currentValue - minValue);
        seekBar.setOnSeekBarChangeListener(this);

        // Put current value
        valueText = (TextView) view.findViewById(R.id.current_value);
        valueText.setText(intFormat.format(currentValue));

        return view;
    }

    @Override
    protected void onDialogClosed(boolean ok) {
        super.onDialogClosed(ok);

        // Return if change was cancelled
        if (!ok) {
            return;
        }

        // Save current value if needed
        if (shouldPersist()) {
            persistInt(currentValue);
        }

        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            currentValue = this.getPersistedInt(this.defaultValue);
        } else {
            // Set default state from the XML attribute
            currentValue = (Integer) defaultValue;
            persistInt(currentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, defaultValue);
    }

    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        // Change text when the SeekBar changes
        currentValue = value + minValue;
        valueText.setText(NumberFormat.getIntegerInstance().format(currentValue));
    }

    @Override
    public CharSequence getSummary() {
        // Format summary string with current value
        String summary = super.getSummary().toString();
        int value = getPersistedInt(defaultValue);
        return String.format(summary, value);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {
        // Not used
    }

    @Override
    public void onStopTrackingTouch(SeekBar seek) {
        // Not used
    }
}
