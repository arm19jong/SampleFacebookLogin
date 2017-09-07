package com.shoppening.samplefacebooklogin

import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import android.content.Intent
import com.facebook.*
import java.util.*
import org.json.JSONException
import android.widget.Toast
import com.facebook.GraphRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*

class MainActivity : LifecycleActivity() {
    lateinit var callbackManager: CallbackManager
    var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
//        Toast.makeText(this, mAuth!!.currentUser!!.displayName, Toast.LENGTH_SHORT).show()
        if(mAuth!!.currentUser == null){
            Toast.makeText(this, "user null", Toast.LENGTH_SHORT).show()
        }
        login_button.setReadPermissions(Arrays.asList("public_profile", "email", "user_photos"))
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code

                        handleFacebookAccessToken(loginResult.accessToken)
                        var paramiter = Bundle()
                        paramiter.putString("fields","id, name, email, picture.type(large)")
                        val request = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject, graphResponse ->
                            try {
                                val str_email = jsonObject.getString("email")
                                Toast.makeText(this@MainActivity, str_email, Toast.LENGTH_LONG).show()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                            Log.i("user", jsonObject.toString())
                        }

                        request.parameters = paramiter
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()

    }

    fun handleFacebookAccessToken(token:AccessToken){
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "sucess", Toast.LENGTH_SHORT).show()
                        var m = mAuth!!.currentUser!!.providerData
                        m = mAuth!!.currentUser!!.providerData


                    }
                    else{
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()

                    }
                })

    }

    override fun onDestroy() {
        super.onDestroy()
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()
    }
//    fun printHashKey() {
//        try {
//            val info = packageManager.getPackageInfo(
//                    "com.shoppening.samplefacebooklogin",
//                    PackageManager.GET_SIGNATURES)
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("YourTag", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//
//        } catch (e: NoSuchAlgorithmException) {
//
//        }
//
//    }

}
