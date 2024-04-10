package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtUser;
    TextInputEditText edtPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    CallbackManager callbackManager;
    ImageView loginBanner;
    TextView edtSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this);
        initUI();

        Glide.with(LoginActivity.this).load(R.drawable.login_logo).into(loginBanner);





        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }

        });

        edtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void handleFacebookAccessToken(AccessToken token) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();//Lấy dữ liệu trên firebase
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());//trả về 1 đối tượng đẻ xác thực bằng token của fb
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {//định nghĩa 1 listener để theo dõi kết quả của quá trinh đăng nhập
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, cập nhật giao diện người dùng với thông tin người dùng đã đăng nhập
                            FirebaseUser user = mAuth.getCurrentUser();
                           Toast.makeText(LoginActivity.this, user.getUid(), Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu đăng nhập không thành công, hiển thị thông báo cho người dùng.
                            Log.e("fb", "đăng nhập bằng thông tin xác thực : thất bại", task.getException());
                            Toast.makeText(LoginActivity.this, "Quá trình xác thực đã thất bại.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUI() {// ánh xạ các thaành phần giao diện từ tệp xml
        progressDialog = new ProgressDialog(this);
        edtUser = findViewById(R.id.edtUserRegister);
        edtPassword = findViewById(R.id.edtPasswordRegister);
        btnLogin = findViewById(R.id.btn_register);
        loginBanner = findViewById(R.id.login__banner);
        edtSignup = findViewById(R.id.edt_signup);
    }

    private void signIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = edtUser.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if(email.length() == 0) {
            edtUser.setFocusable(true);
            edtUser.setError("Bạn phải nhập email");
            return;
        }
        if(password.length() == 0) {
            edtPassword.setFocusable(true);
            edtPassword.setError("Bạn phải nhập passsword");
            return ;
        }

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, cập nhật giao diện người dùng với thông tin người dùng đã đăng nhập
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Quá trình xác thực đã thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Chuyển kết quả hoạt động trở lại Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}