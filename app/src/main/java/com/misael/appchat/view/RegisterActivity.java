package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.misael.appchat.R;
import com.misael.appchat.model.User;

import java.io.IOException;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEditUsername;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnRegister;
    private Button mBtnPhoto;
    private ImageView mImgPhoto;
    private AlertDialog dialog;
    private Uri mSelectedURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        mEditUsername    = findViewById(R.id.editUsername);
        mEditEmail       = findViewById(R.id.editEmail);
        mEditPassword    = findViewById(R.id.editPassword);
        mBtnRegister     = findViewById(R.id.btnRegister);
        mBtnPhoto        = findViewById(R.id.btnPhoto);
        mImgPhoto        = findViewById(R.id.imgPhoto);

        mBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                mSelectedURI = data.getData();

                Bitmap bitmap = null;
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        // To handle deprication use
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), mSelectedURI));
                    } else {
                        // Use older version
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedURI);
                    }
                    mImgPhoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                    mBtnPhoto.setAlpha(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }

    private void registerUser() {
        String username = mEditUsername.getText().toString();
        String email    = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Você deve preencher todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).build();
        dialog.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("Register", task.getResult().getUser().getUid());
                            saveUserInFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Register", e.getMessage());
                        dialog.hide();
                    }
                });
    }

    private void saveUserInFirebase() {
        String fileName = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/Images/" + fileName);
        ref.putFile(mSelectedURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Register", uri.toString());

                                String uid        = FirebaseAuth.getInstance().getUid();
                                String username   = mEditUsername.getText().toString();
                                String profireURL = uri.toString();

                                User user = new User(uid, username, profireURL);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(RegisterActivity.this, PrincipalActivity.class);

                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                dialog.hide();
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("Register", e.getMessage());
                                                dialog.hide();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Register", e.getMessage(), e);
                        dialog.hide();
                    }
                });
    }
}
