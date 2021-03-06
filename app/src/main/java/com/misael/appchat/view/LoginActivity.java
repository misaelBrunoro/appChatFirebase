package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.misael.appchat.R;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnEnter;
    private TextView mTxtAccount;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        mEditEmail    = findViewById(R.id.editEmail);
        mEditPassword = findViewById(R.id.editPassword);
        mBtnEnter     = findViewById(R.id.btnEnter);
        mTxtAccount   = findViewById(R.id.txtAccount);

        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEditEmail.getText().toString();
                String password = mEditPassword.getText().toString();
                Log.i("Teste", email);
                Log.i("Teste", password);

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Você deve preencher todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog = new SpotsDialog.Builder().setContext(LoginActivity.this).build();
                dialog.show();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, PrincipalActivity.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    dialog.hide();
                                    startActivity(intent);
                                }
                                Toast.makeText(LoginActivity.this, "Dados incorretos", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Teste", e.getMessage());
                                dialog.hide();
                            }
                        });
            }
        });

        mTxtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
