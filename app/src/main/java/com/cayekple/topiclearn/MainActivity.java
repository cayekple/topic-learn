package com.cayekple.topiclearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private FloatingActionButton fabAdd;

    private BottomNavigationView nvMainBottomNav;

    private String userId;

    private HomeFragment mHomeFragment;
    private NotificationFragment mNotificationFragment;
    private ProfileFragment mProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd = findViewById(R.id.fabAdd);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Topic Learn");

        nvMainBottomNav = findViewById(R.id.bnbMainBar);

        mHomeFragment = new HomeFragment();
        mNotificationFragment = new NotificationFragment();
        mProfileFragment = new ProfileFragment();

        replaceFragment(mHomeFragment);

        nvMainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_action_home:
                        replaceFragment(mHomeFragment);
                        return true;
                    case R.id.bottom_action_notification:
                        replaceFragment(mNotificationFragment);
                        return true;
                    case R.id.bottom_action_profile:
                        replaceFragment(mProfileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTopicIntent = new Intent(MainActivity.this, AddTopicActivity.class);
                startActivity(addTopicIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            sendToLogin();
        }else {
            userId = mAuth.getCurrentUser().getUid();
            mFirebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){
                            Intent settingIntent = new Intent(MainActivity.this, AccountSetting.class);
                            startActivity(settingIntent);
                            finish();
                        }

                        String name = task.getResult().getString("name");
                        String eduLevel = task.getResult().getString("educational_level");
                        String image = task.getResult().getString("image");

                        Toast.makeText(MainActivity.this, "Name: "+name, Toast.LENGTH_LONG).show();

//                        txtFullname.setText(name);
//                        txtEducationalLevel.setText(eduLevel);
                        RequestOptions placeholderRequest = new RequestOptions();
//                        placeholderRequest.placeholder(R.drawable.ic_profile);
//                        Glide.with(MainActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(userProfileImage);
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error: "+error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionLogoutBtn:
                logOut();
                return true;
            case R.id.actionSettingBtn:
                Intent settingIntent =  new Intent(MainActivity.this, AccountSetting.class);
                startActivity(settingIntent);
                return true;
            default:
                return false;
        }
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }
}