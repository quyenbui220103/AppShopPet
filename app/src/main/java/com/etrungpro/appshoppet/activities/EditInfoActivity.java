package com.etrungpro.appshoppet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class EditInfoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100 ;
    TextView tvEmailInfo;
    EditText edtFirstNameInfo;
    EditText edtLastNameInfo;
    EditText edtPasswordInfo;
    EditText edtNewPasswordInfo;
    EditText edtConfirmPasswordInfo;
    Button btnConfirmInfo;
    ImageView imgInfo;
    String imageName;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        initUI();

        String userId = FirebaseAuth.getInstance().getUid();// lay Id nguoi dung tren firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();// tt csdl vs firestore
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();//tham chieu den thu goc de tai anh hoac tep lem
        db.collection("users")// lay thong tin nguoi dung tren fire base users
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {//neu tt ton tai thi tra du lieu
                            tvEmailInfo.setText((String) documentSnapshot.get("email"));
                            edtFirstNameInfo.setText((String) documentSnapshot.get("firstName"));
                            edtLastNameInfo.setText((String) documentSnapshot.get("lastName"));
                            edtPasswordInfo.setText((String) documentSnapshot.get("hashPassword"));
                            storageRef.child((String) documentSnapshot.get("img"))
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUri = uri;//lay dia chi url
                                            Glide.with(EditInfoActivity.this)
                                                    .load(uri)//tao anh
                                                    .into(imgInfo);//hien thi anh
                                            imageName = new File(uri.getPath()).getName();//tao 1 dt de lay ten anh tu uri voi ten file
                                        }
                                    });
                        }
                    }
                });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setImageInfo();
            }//tai anh len tu thu vien
        });

        btnConfirmInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tvEmailInfo.getText().toString();
                String firstName = edtFirstNameInfo.getText().toString();
                String lastName = edtLastNameInfo.getText().toString();
                String newPassword = edtNewPasswordInfo.getText().toString();
                String passwordConfirm = edtConfirmPasswordInfo.getText().toString();
                if(!newPassword.equals(passwordConfirm)) {
                    edtConfirmPasswordInfo.setError("Mật khẩu không trùng nhau !!");
                    edtConfirmPasswordInfo.setFocusable(true);//dua ra edittext nhap lai pass
                    return ;
                }

                Timestamp timestamp = Timestamp.now();//lay thoi gian thuc de chinh sua
                User user = new User(email, firstName, lastName, imageName, newPassword, timestamp);
                db.collection("users")//dua tt ms len firebase thong qua collection usert
                        .document(userId).
                        set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(EditInfoActivity.this, "Sửa thành công !!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditInfoActivity.this, MainActivity.class));
                                finish();
                            }
                        });

                storageRef.child(imageName)//xd vi tri tham chieu tren firebase storage
                        .putFile(imageUri)//tai anh len
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Glide.with(EditInfoActivity.this)
                                        .load(imageUri)//tai anh len
                                        .into(imgInfo);
                            }
                        });

            }
        });
    }

    void initUI() {
        tvEmailInfo = findViewById(R.id.tv_email_info);
        edtFirstNameInfo = findViewById(R.id.edt_firstName_info);
        edtLastNameInfo = findViewById(R.id.edt_lastName_info);
        edtPasswordInfo = findViewById(R.id.edt_password_info);
        edtNewPasswordInfo = findViewById(R.id.edt_new_password_info);
        edtConfirmPasswordInfo = findViewById(R.id.edt_password_confirm_info);
        btnConfirmInfo = findViewById(R.id.btn_confirm_info);
        imgInfo = findViewById(R.id.img_info);
    }

    void setImageInfo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE);// tai anh tu tv thong qu intent

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null) {
            imageUri = data.getData();//lay dia chia url
            Glide.with(this)
                    .load(imageUri)
                    .into(imgInfo);
            String date = String.valueOf(Timestamp.now().toDate().getTime());//lay time htai sau do chuyen ve chuoi
            String parseName = new File(imageUri.getPath()).getName();//lay ten file hinh anh
            imageName =parseName + "_" + date;//tao ten moi cho hinh anh
        } else {
            Toast.makeText(EditInfoActivity.this, "Bạn chưa chọn ảnh nào cả", Toast.LENGTH_SHORT).show();
        }
    }

}