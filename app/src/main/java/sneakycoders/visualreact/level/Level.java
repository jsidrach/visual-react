package sneakycoders.visualreact.level;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sneakycoders.visualreact.R;

abstract public class Level extends Fragment {
    // Basic colors
    protected int successColor;
    protected int failColor;

    // Callback when then player taps its area
    abstract public boolean onPlayerTap();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Set colors
        successColor = ContextCompat.getColor(getActivity(), R.color.success_primary);
        failColor = ContextCompat.getColor(getActivity(), R.color.fail_primary);

        return null;
    }

    // Auxiliary functions

    protected Integer getRandomColor() {
        String[] palette = getResources().getStringArray(R.array.palette);
        return Color.parseColor(palette[new Random().nextInt(palette.length)]);
    }

    protected Integer[] getRandomColors(int n) {
        List<String> hexPalette = Arrays.asList(getResources().getStringArray(R.array.palette));
        List<Integer> palette = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        // Get colors
        for (String hex : hexPalette) {
            palette.add(Color.parseColor(hex));
        }

        // Fill colors
        int i = 0;
        while (i != n) {
            Collections.shuffle(palette);
            colors.addAll(palette.subList(0, Math.min(n - i, palette.size())));
        }

        return colors.toArray(new Integer[n]);
    }

    protected int getRandomInt(int min, int max) {
        return (new Random()).nextInt(max - min + 1) + min;
    }

    protected double getRandomDouble(double min, double max) {
        return min + (max - min) * (new Random()).nextDouble();
    }
}
