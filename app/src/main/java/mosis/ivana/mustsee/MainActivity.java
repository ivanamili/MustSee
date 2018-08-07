package mosis.ivana.mustsee;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private boolean loginInProgress;

    //view elements
    EditText txtEmail, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating FireBaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        loginInProgress=false;

        //proceed to home activity directly, user is already signed in
        if(mAuth.getCurrentUser()!=null){
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }

        txtEmail=findViewById(R.id.loginInsertUsername);
        txtPassword=findViewById(R.id.loginInsertPassword);


        Button btnRegister= (Button) findViewById(R.id.loginScreenRegisterButton);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);

            }
        });

        Button btnLogin = findViewById(R.id.signinButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginInProgress)
                    return;

                String email= txtEmail.getText().toString().trim();
                String password=txtPassword.getText().toString().trim();

                if(email.length()==0){
                    txtEmail.setError("Enter email!");
                    txtEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    txtEmail.setError("Enter valid email!");
                    txtEmail.requestFocus();
                    return;
                }
                if(password.length()==0){
                    txtPassword.setError("Enter password!");
                    txtPassword.requestFocus();
                    return;
                }
                if(password.length()<6){
                    txtPassword.setError("Password must contain at least 6 characters!");
                    txtPassword.requestFocus();
                    return;
                }
                loginInProgress=true;
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
