package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.misael.appchat.model.Contact;
import com.misael.appchat.model.Message;
import com.misael.appchat.model.Notification;
import com.misael.appchat.model.User;
import com.misael.appchat.R;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user;
    private EditText ediChat;
    private User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);

        user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());

        RecyclerView rv = findViewById(R.id.recyclerChat);
        ediChat = findViewById(R.id.editMensagem);
        Button btnChat = findViewById(R.id.btnEnviar);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
                ediChat.setText("");
            }
        });

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance().collection("/users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshot) {
                        me = queryDocumentSnapshot.toObject(User.class);
                        searchMessages();
                    }
                });
    }

    private void searchMessages() {
        if (me != null) {
            String fromId = me.getUuid();
            String toId = user.getUuid();

            FirebaseFirestore.getInstance().collection("/chatMessages")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                                if (!documentChanges.isEmpty()) {
                                    adapter.clear();
                                    for (DocumentChange doc : documentChanges) {
                                        if (doc.getType() == DocumentChange.Type.ADDED) {
                                            Message message = doc.getDocument().toObject(Message.class);
                                            adapter.add(new ItemMessage(message));
                                        }
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void sendMessage() {
        String text = ediChat.getText().toString();

        final String fromId = FirebaseAuth.getInstance().getUid();
        final String toId = user.getUuid();
        long timestamp = System.currentTimeMillis();
        final Message message = new Message(text, fromId, toId, timestamp);

        if (!message.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/chatMessages")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Contact contact = new Contact(toId, user.getUsername(), message.getText(),
                                                            message.getTimestamp(), user.getProfileURL());

                            FirebaseFirestore.getInstance().collection("/lastMessages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);

                            if (!user.isOnline()) {
                                Notification notification = new Notification();
                                notification.setFromId(message.getFromId());
                                notification.setText(message.getText());
                                notification.setTimestamp(message.getTimestamp());
                                notification.setToId(message.getToId());
                                notification.setFromName(me.getUsername());

                                FirebaseFirestore.getInstance().collection("/notifications")
                                        .document(user.getToken())
                                        .set(notification);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);
                        }
                    });
            FirebaseFirestore.getInstance().collection("/chatMessages")
                    .document(toId)
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Contact contact = new Contact(fromId, me.getUsername(), message.getText(),
                                                            message.getTimestamp(), me.getProfileURL());

                            FirebaseFirestore.getInstance().collection("/lastMessages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contact);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste", e.getMessage(), e);
                        }
                    });
        }
    }

    private class ItemMessage extends Item<GroupieViewHolder> {
        private Message message;

        public ItemMessage (Message message) {
            this.message = message;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_message);
            ImageView imgMsg = viewHolder.itemView.findViewById(R.id.img_message);

            txtMsg.setText(message.getText());
            if (message.getFromId().equals(FirebaseAuth.getInstance().getUid())) {
                Picasso.get()
                        .load(me.getProfileURL())
                        .into(imgMsg);
            } else {
                Picasso.get()
                        .load(user.getProfileURL())
                        .into(imgMsg);
            }

        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.item_from_message : R.layout.item_to_message;
        }
    }
}
