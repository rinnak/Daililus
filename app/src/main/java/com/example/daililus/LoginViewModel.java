package com.example.daililus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<AuthState> _loginState = new MutableLiveData<>();


    public LiveData<AuthState> getLoginState(){
        return _loginState;
    }
    public void loginUser(String email, String password){
        _loginState.setValue(new AuthState(true, false, null));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        _loginState.setValue(new AuthState(false, true, null));
                    }
                    else{
                        String errorMsg = (task.getException() != null)  ?
                                "Неверный пароль" : "Ошибка входа";
                        _loginState.setValue(new AuthState(false, false, errorMsg));
                    }
                });
    }
}
