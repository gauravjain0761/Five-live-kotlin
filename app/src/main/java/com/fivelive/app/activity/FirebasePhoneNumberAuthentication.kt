package com.fivelive.app.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fivelive.app.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class FirebasePhoneNumberAuthentication : AppCompatActivity() {
    private var mCallbacks: OnVerificationStateChangedCallbacks? = null
    private val mVerificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    var phoneNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_phone_number_authentication)
        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.button2).setOnClickListener { //  sendVerificationCode("+918800736083");
            sendVerificationCode("+917011599088")
        }

        // [START phone_auth_callbacks]
        mCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Toast.makeText(
                    this@FirebasePhoneNumberAuthentication,
                    "onVerificationCompleted:",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(FirebasePhoneNumberAuthentication.Companion.TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        this@FirebasePhoneNumberAuthentication,
                        "Invalid phone number.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(FirebasePhoneNumberAuthentication.Companion.TAG, "onCodeSent:$verificationId")
            }

            override fun onCodeAutoRetrievalTimeOut(s: String) {
                super.onCodeAutoRetrievalTimeOut(s)
                Toast.makeText(
                    this@FirebasePhoneNumberAuthentication,
                    "TimeOut:$s",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun sendVerificationCode(phoneNumber: String?) {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber!!) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks!!) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                override fun onComplete(task: Task<AuthResult?>) {
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(
                            FirebasePhoneNumberAuthentication.Companion.TAG,
                            "signInWithCredential:success"
                        )
                        val user = task.result?.user
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(
                            FirebasePhoneNumberAuthentication.Companion.TAG,
                            "signInWithCredential:failure",
                            task.exception
                        )
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
            })
    }

    companion object {
        private const val TAG = "FirebasePhoneNumberAuth"
    }
}