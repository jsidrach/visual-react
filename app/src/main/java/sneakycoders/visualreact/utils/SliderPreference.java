package sneakycoders.visualreact.utils;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.NumberFormat;

import sneakycoders.visualreact.R;

public class SliderPreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener {
    // Schemas and attributes
    protected static final String PREFERENCE_NS = "http://schemas.android.com/apk/lib/sneakycoders.visualreact.utils";
    protected static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    protected static final String ATTR_DEFAULT_VALUE = "defaultValue";
    protected static final String ATTR_MIN_VALUE = "minValue";
    protected static final String ATTR_MAX_VALUE = "maxValue";

    // Default values
    protected static final int DEFAULT_MIN_VALUE = 0;
    protected static final int DEFAULT_MAX_VALUE = 100;
    protected static final int DEFAULT_VALUE = 50;

    // Values
    protected int minValue;
    protected int maxValue;
    protected int defaultValue;
    protected int currentValue;

    // View
    protected SeekBar seekBar;
    protected TextView valueText;

    // Constructor
    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        minValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        maxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        defaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_VALUE);
    }

    // Dialog setup
    @Override
    protected View onCreateDialogView() {
        // Integer format instance
        NumberFormat intFormat = NumberFormat.getIntegerInstance();

        // Get current value from settings
        currentValue = getPersistedInt(defaultValue);

        // Inflate layout
        LayoutInflater inflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_slider, null);

        // Put minimum and maximum
        ((TextView) view.findViewById(R.id.min_value)).setText(intFormat.format(minValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(intFormat.format(maxValue));

        // Setup SeekBar
        seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        seekBar.setMax(maxValue - minValue);
        seekBar.setProgress(currentValue - minValue);
        seekBar.setOnSeekBarChangeListener(this);

        // Put current value
        valueText = (TextView) view.findViewById(R.id.current_value);
        valueText.setText(intFormat.format(currentValue));

        return view;
    }

    // Save when the dialog closes
    @Override
    protected void onDialogClosed(boolean ok) {
        super.onDialogClosed(ok);

        // Return if change was cancelled
        if (!ok) {
            return;
        }

        // Persist current value if needed
        if (shouldPersist()) {
            persistInt(currentValue);
        }

        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }

    // Change text when the SeekBar changes
    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        currentValue = value + minValue;
        valueText.setText(NumberFormat.getIntegerInstance().format(currentValue));
    }

    // Format summary string with current value
    @Override
    public CharSequence getSummary() {
        String summary = super.getSummary().toString();
        int value = getPersistedInt(defaultValue);
        return String.format(summary, value);
    }

    // Not used
    @Override
    public void onStartTrackingTouch(SeekBar seek) {
    }

    // Not used
    @Override
    public void onStopTrackingTouch(SeekBar seek) {
    }
}
