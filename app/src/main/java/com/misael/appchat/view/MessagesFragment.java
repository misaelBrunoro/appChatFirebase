package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.misael.appchat.R;
import com.misael.appchat.app.ChatApp;
import com.misael.appchat.model.User;
import com.misael.appchat.model.Contact;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import java.util.List;

public class MessagesFragment extends Fragment {

    private GroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frg_messages, container, false);

        RecyclerView rv = v.findViewById(R.id.reciyclerLastMessages);
        rv.setLayoutManager(new LinearLayoutManager(v.getContext()));
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

        searchLastMessage();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                ItemContact contact = (ItemContact) item;
                String uid = contact.contact.getUuid();
                FirebaseFirestore.getInstance().collection("/users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            }
                        });
            }
        });
        return v;
    }

    private void searchLastMessage() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("/lastMessages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                            adapter.clear();
                            for (DocumentChange doc: documentChanges) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    Contact contact = doc.getDocument().toObject(Contact.class);
                                    adapter.add(new ItemContact(contact));
                                }
                            }
                        }
                    }
                });
    }

    private class ItemContact extends Item<GroupieViewHolder> {
        private final Contact contact;

        private ItemContact(Contact contact) {
            this.contact = contact;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView username = viewHolder.itemView.findViewById(R.id.textView);
            TextView message = viewHolder.itemView.findViewById(R.id.textView2);
            ImageView photo = viewHolder.itemView.findViewById(R.id.imageView);

            username.setText(contact.getUsername());
            message.setText(contact.getLastMessage());
            Picasso.get()
                    .load(contact.getPhotoURL())
                    .into(photo);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user_contact;
        }
    }
}
