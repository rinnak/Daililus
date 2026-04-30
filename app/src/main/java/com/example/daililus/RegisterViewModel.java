package com.example.daililus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterViewModel extends ViewModel {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final MutableLiveData<AuthState> _authState = new MutableLiveData<>();

    public LiveData<AuthState> getAuthState(){
        return _authState;
    }

    public void registerUser(String email, String password){
        _authState.setValue(new AuthState(true, false, null)); //загрузка
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        _authState.setValue(new AuthState(false, true, null));
                    }
                    else{
                        String errorMsg;
                        Exception exception = task.getException();

                        if (exception instanceof FirebaseAuthUserCollisionException) {
                            errorMsg = "Эта почта уже занята другим аккаунтом";
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMsg = "Неверный формат адреса почты";
                        } else {
                            errorMsg = (exception != null) ? exception.getMessage() : "Ошибка регистрации";
                        }

                        _authState.setValue(new AuthState(false, false, errorMsg));
                    }
                });
    }
}
