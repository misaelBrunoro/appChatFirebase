package com.misael.appchat.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.misael.appchat.R;
import com.misael.appchat.model.User;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PerfilFragment extends Fragment {
    private ImageView mImgPhoto;
    private Button mBtnPhoto;
    private EditText mEditUsername;
    private Button mBtnEditar;
    private Uri mSelectedURI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_perfil, container, false);

        mBtnPhoto        = v.findViewById(R.id.btnPhoto);
        mImgPhoto        = v.findViewById(R.id.imgPhoto);
        mEditUsername    = v.findViewById(R.id.editUsername);
        mBtnEditar       = v.findViewById(R.id.btnAlterar);

        mBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        mBtnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPerfil();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                mSelectedURI = data.getData();

                Bitmap bitmap = null;
                try {
                    if (android.os.Build.VERSION.SDK_INT >= 29) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), mSelectedURI));
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), mSelectedURI);
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

    private void editPerfil() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);

                            if (mSelectedURI != null) {
                                changePhoto(user);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Perfil", e.getMessage());
                        }
                    });
        }
    }

    private void changePhoto(final User user) {
        FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfileURL())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String fileName = UUID.randomUUID().toString();
                        final StorageReference ref = FirebaseStorage.getInstance().getReference("/Images/" + fileName);
                        ref.putFile(mSelectedURI)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                user.setProfileURL(uri.toString());
                                                FirebaseFirestore.getInstance().collection("users")
                                                        .document(user.getUuid())
                                                        .set(user)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.i("Photo", "Sucesso ao alterar foto");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.i("Photo", e.getMessage());
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Delete", e.getMessage());
                    }
                });
    }
}
