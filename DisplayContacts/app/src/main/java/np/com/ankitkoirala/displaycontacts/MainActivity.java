package np.com.ankitkoirala.displaycontacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import np.com.ankitkoirala.displaycontacts.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ListView listView;

    private static final int CONTACT_PERMISSION_REQ_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        listView = findViewById(R.id.listView);
        Log.d(TAG, "onCreate: listView: " + listView);

        int permissionCheckRes = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if(permissionCheckRes == PackageManager.PERMISSION_GRANTED) {
//            contactsPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_REQ_CODE);
        }

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                List<String> contacts = null;
                if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String[] projections = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    Cursor cursor = getContentResolver().query(
                            ContactsContract.Contacts.CONTENT_URI,
                            projections,
                            null,
                            null,
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

                    if(cursor != null) {
                        Log.d(TAG, "onClick: cursor is not null");
                        contacts = new ArrayList<>();
                        while(cursor.moveToNext()) {
                            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                            contacts.add(contactName);
                        }
                        cursor.close();

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.contact_view, R.id.textView, contacts);
                        listView.setAdapter(adapter);
                    }
                } else {
                    Snackbar.make(view, "Permission not granted to view contacts", Snackbar.LENGTH_LONG)
                            .setAction("Grant permission", view1 -> {
                                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_REQ_CODE);
                                } else {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                    MainActivity.this.startActivity(intent);
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: starts");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACT_PERMISSION_REQ_CODE:
                if(grantResults != null && grantResults.length > 0) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission was granted");
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        contactsPermissionGranted = true;
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: Permission not granted");
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}