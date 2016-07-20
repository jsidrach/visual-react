package sneakycoders.visualreact.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import sneakycoders.visualreact.R;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate layout
        setContentView(R.layout.main_menu);
    }

    public void openPreferences(@SuppressWarnings("UnusedParameters") View view) {
        // Switch to Preferences screen
        Intent intent = new Intent(MainMenu.this, Preferences.class);
        startActivity(intent);
    }
}
