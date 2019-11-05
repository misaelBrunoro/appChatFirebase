package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.misael.appchat.R;

public class MessagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_messages);

        verifyAuth();
    }

    private void verifyAuth() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(MessagesActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.i("OLHA ENTREI", "ENTREI AQUI");
        switch (item.getItemId()) {
            case R.id.contacts:
                Log.i("OLHA ENTREI", "ENTREI AQUI contacts");
                Intent intent = new Intent(MessagesActivity.this, ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                Log.i("OLHA ENTREI", "logout");
                FirebaseAuth.getInstance().signOut();
                verifyAuth();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
