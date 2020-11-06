package com.cayekple.topiclearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class AddTopicActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView topicImg;
    private EditText etMainTopic;
    private Button saveTopic;
    private ProgressBar topicProgressBar;

    private Uri topicImageUri;

    private StorageReference mStorageReference;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;

    private String userId;

    private String mUUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);

        mToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add New Topic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        topicImg = findViewById(R.id.imgTopic);
        etMainTopic = findViewById(R.id.etMainTopic);
        topicProgressBar = findViewById(R.id.topicProgressBar);
        saveTopic = findViewById(R.id.btnSaveTopic);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        topicImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(AddTopicActivity.this);
            }
        });

        saveTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mainTopic = etMainTopic.getText().toString();

                if (!TextUtils.isEmpty(mainTopic) && topicImageUri != null) {
                    topicProgressBar.setVisibility(View.VISIBLE);

                    if (mUUID == null)
                        mUUID = UUID.randomUUID().toString();


                    final StorageReference imagePath = mStorageReference.child("topic_image")
                            .child(mUUID + ".png");
                    UploadTask uploadTask = imagePath.putFile(topicImageUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            Log.d("TopicImage", "Image Uri :"+imagePath.getDownloadUrl());
                            return imagePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                storeFirestore(downloadUri, mainTopic);
                            }else {
                                String error = task.getException().getMessage();
                                Toast.makeText(AddTopicActivity.this, "Image Error: " + error, Toast.LENGTH_LONG).show();
                                topicProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
            }
        });
    }

    private void storeFirestore(Uri downloadUri, String mainTopic) {
        if (downloadUri != null) {

            Toast.makeText(AddTopicActivity.this, "Uri: "+downloadUri, Toast.LENGTH_LONG).show();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("topic", mainTopic);
            userMap.put("image", downloadUri.toString());
            userMap.put("user_id", userId);
            userMap.put("timestamp", FieldValue.serverTimestamp());
            mFirebaseFirestore.collection("Topics").add(userMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AddTopicActivity.this, "Operation successful", Toast.LENGTH_LONG).show();
                                Intent mainActivity = new Intent(AddTopicActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(AddTopicActivity.this, "Firestore Error: "+error, Toast.LENGTH_LONG).show();
                            }
                            topicProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("topic", mainTopic);
            userMap.put("user_id", userId);
            userMap.put("timestamp", FieldValue.serverTimestamp());
            mFirebaseFirestore.collection("Topics").add(userMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AddTopicActivity.this, "Operation Successful ", Toast.LENGTH_LONG).show();
                                Intent mainActivity = new Intent(AddTopicActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(AddTopicActivity.this, "Firestore Error: "+error, Toast.LENGTH_LONG).show();
                            }
                            topicProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                topicImageUri = result.getUri();
                topicImg.setImageURI(topicImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}