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
    protected int successLightColor;
    protected int failLightColor;
    // Random number generator
    private Random random;

    // Callback when then player taps its area
    abstract public boolean onPlayerTap();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Initialize the random number generator
        random = new Random();

        // Set colors
        successColor = ContextCompat.getColor(getActivity(), R.color.success_primary);
        failColor = ContextCompat.getColor(getActivity(), R.color.fail_primary);
        successLightColor = ContextCompat.getColor(getActivity(), R.color.success_light);
        failLightColor = ContextCompat.getColor(getActivity(), R.color.fail_light);

        return null;
    }

    // Auxiliary functions

    protected Integer getRandomColor() {
        String[] palette = getResources().getStringArray(R.array.palette);
        return Color.parseColor(palette[random.nextInt(palette.length)]);
    }

    protected Integer getRandomDistinctiveColor() {
        String[] palette = getResources().getStringArray(R.array.distinctivePalette);
        return Color.parseColor(palette[random.nextInt(palette.length)]);
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
            int addN = Math.min(n - i, palette.size());
            colors.addAll(palette.subList(0, addN));
            i += addN;
        }

        return colors.toArray(new Integer[n]);
    }

    protected int randomInt(int idMin, int idMax) {
        int min = getResources().getInteger(idMin);
        int max = getResources().getInteger(idMax);
        return randomInInterval(min, max);
    }

    protected float randomFloat(int idMin, int idMax) {
        float min = getResources().getFraction(idMin, 1, 1);
        float max = getResources().getFraction(idMax, 1, 1);
        return randomInInterval(min, max);
    }

    protected boolean randomBoolean() {
        return random.nextBoolean();
    }

    protected int randomInInterval(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    protected float randomInInterval(float min, float max) {
        return (float) (min + (max - min) * random.nextDouble());
    }
}
