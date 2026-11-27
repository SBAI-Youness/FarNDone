package ma.emsi.farndone;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ConnectActivity.class));
        finish();

        ActionBar actionBar = getSupportActionBar();
           if (actionBar != null ){
               actionBar.setTitle("LOPAPA");
        }}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==R.id.){
            Intent
        }
        return super.onOptionsItemSelected(item);
    }
}
