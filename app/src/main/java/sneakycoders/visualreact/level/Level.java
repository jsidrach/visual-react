package sneakycoders.visualreact.level;

import android.support.v4.app.Fragment;

import java.util.Random;

abstract public class Level extends Fragment {
    abstract public boolean result();

    protected int getRandomInt(int min, int max) {
        return (new Random()).nextInt(max - min + 1) + min;
    }
}
