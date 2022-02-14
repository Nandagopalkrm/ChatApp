package com.example.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;


import com.example.chatapp.adapters.UserAdapter;

import com.example.chatapp.databinding.ActivityUserBinding;
import com.example.chatapp.models.User;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager= new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }


    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .get()
                .addOnCompleteListener(task -> {
                   loading(false);
                   String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if (task.isSuccessful() && task.getResult() !=null){
                       List<User> users = new ArrayList<>();
                       for (QueryDocumentSnapshot queryDocumentSnapshots : task.getResult()){
                           if (currentUserId.equals(queryDocumentSnapshots.getId())){
                               continue;
                           }
                            User user =new User();
                            user.name = queryDocumentSnapshots.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshots.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshots.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshots.getString(Constants.KEY_FCM_TOKEN);
                            users.add(user);
                       }
                       if (users.size()>0){
                           UserAdapter userAdapter= new UserAdapter(users);
                           binding.usersRecyclerView.setAdapter(userAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);
                       }else {
                           showErrorMessage();
                       }
                   }else{
                       showErrorMessage();
                   }
                });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No User Available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

    }

}