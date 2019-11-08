package com.misael.appchat.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.misael.appchat.model.User;
import com.misael.appchat.R;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);

        User user = getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());

        RecyclerView rv = findViewById(R.id.recyclerChat);
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.add(new ItemMessage(true));
        adapter.add(new ItemMessage(false));
        adapter.add(new ItemMessage(true));
        adapter.add(new ItemMessage(false));
        adapter.add(new ItemMessage(true));
    }

    private class ItemMessage extends Item<GroupieViewHolder> {
        private final boolean isLeft;

        public ItemMessage (boolean isLeft) {
            this.isLeft = isLeft;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
        }

        @Override
        public int getLayout() {
            return isLeft ? R.layout.item_from_message : R.layout.item_to_message;
        }
    }
}
