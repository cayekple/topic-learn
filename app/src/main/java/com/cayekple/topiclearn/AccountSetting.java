package com.cayekple.topiclearn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSetting extends AppCompatActivity {

    private EditText txtFullName, txtEducationLevel;
    private Button btnSaveSetting;
    private ProgressBar settingProgressBar;
    private CircleImageView setupImage;
    private Uri mainImageUrl = null;

    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

    private String userId;

    private boolean isChanged =  false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        txtFullName = findViewById(R.id.txtFullname);
        txtEducationLevel = findViewById(R.id.txtEducationalLevel);
        btnSaveSetting = findViewById(R.id.btnSaveSettings);
        settingProgressBar = findViewById(R.id.settingProgressBar);
        setupImage = findViewById(R.id.imgProfile);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        userId = FirebaseAuth.getInstance().getUid();
        settingProgressBar.setVisibility(View.VISIBLE);
        btnSaveSetting.setEnabled(false);

        mFirebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String eduLevel = task.getResult().getString("educational_level");
                        String image = task.getResult().getString("image");

                        mainImageUrl = Uri.parse(image);

                        txtFullName.setText(name);
                        txtEducationLevel.setText(eduLevel);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.ic_profile);
                        Glide.with(AccountSetting.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(AccountSetting.this, "Firestore Retrieve Error: "+error, Toast.LENGTH_LONG).show();
                }
                settingProgressBar.setVisibility(View.INVISIBLE);
                btnSaveSetting.setEnabled(true);
            }
        });

        btnSaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fullName = txtFullName.getText().toString();
                final String eduLevel = txtEducationLevel.getText().toString();

                if (!TextUtils.isEmpty(fullName) && !TextUtils.isEmpty(eduLevel) && mainImageUrl != null) {
                    settingProgressBar.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        userId = mFirebaseAuth.getCurrentUser().getUid();

                        StorageReference imagePath = mStorageReference.child("profile_image").child(userId + ".jpg");

                        imagePath.putFile(mainImageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task, fullName, eduLevel);
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(AccountSetting.this, "Image Error: " + error, Toast.LENGTH_LONG).show();
                                    settingProgressBar.setVisibility(View.INVISIBLE);
                                }

                            }
                        });

                    } else {
                        storeFirestore(null, fullName, eduLevel);
                    }
                }
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(AccountSetting.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(AccountSetting.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AccountSetting.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        imagePicker();
                    }
                } else {
                    imagePicker();
                }
            }
        });
    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String fullName, String eduLevel) {
        if (task != null) {
            Task<Uri> downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl();
            Toast.makeText(AccountSetting.this, "Uri: "+downloadUri, Toast.LENGTH_LONG).show();
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", fullName);
            userMap.put("image", downloadUri.toString());
            userMap.put("educational_level", eduLevel);
            mFirebaseFirestore.collection("Users").document(userId).set(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AccountSetting.this, "Account Setting Updated ", Toast.LENGTH_LONG).show();
                                Intent mainActivity = new Intent(AccountSetting.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(AccountSetting.this, "Firestore Error: "+error, Toast.LENGTH_LONG).show();
                            }
                            settingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", fullName);
            userMap.put("educational_level", eduLevel);
            mFirebaseFirestore.collection("Users").document(userId).set(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AccountSetting.this, "Account Setting Updated ", Toast.LENGTH_LONG).show();
                                Intent mainActivity = new Intent(AccountSetting.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(AccountSetting.this, "Firestore Error: "+error, Toast.LENGTH_LONG).show();
                            }
                            settingProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }

    }

    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AccountSetting.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUrl = result.getUri();
                setupImage.setImageURI(mainImageUrl);
                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}