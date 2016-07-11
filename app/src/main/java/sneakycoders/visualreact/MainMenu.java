package sneakycoders.visualreact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }

    public void openPreferences(View view) {
        Intent intent = new Intent(MainMenu.this, Preferences.class);
        startActivity(intent);
    }
}
