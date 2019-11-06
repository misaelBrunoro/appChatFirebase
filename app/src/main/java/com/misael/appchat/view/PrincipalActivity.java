package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.misael.appchat.R;

public class PrincipalActivity extends AppCompatActivity {
    private BottomNavigationView navigationView;
    private MessagesFragment messagesFragment;
    private ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        verifyAuth();

        messagesFragment = new MessagesFragment();
        contactsFragment = new ContactsFragment();

        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.messages:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, messagesFragment)
                                .commit();
                        break;
                    case R.id.contacts:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, contactsFragment)
                                .commit();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        verifyAuth();
                        break;
                }
                return false;
            }
        });
    }

    private void verifyAuth() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(PrincipalActivity.this, LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }
}
