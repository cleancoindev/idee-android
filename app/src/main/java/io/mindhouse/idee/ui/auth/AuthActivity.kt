package io.mindhouse.idee.ui.auth

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.transition.TransitionManager
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import io.mindhouse.idee.R
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_auth.*
import timber.log.Timber

class AuthActivity : BaseActivity<AuthViewState, AuthViewModel>() {

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, AuthActivity::class.java)
    }

    private val fbCallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initFbLogin()
    }

    override fun render(state: AuthViewState) {
        TransitionManager.beginDelayedTransition(content)
        if (state.isLoading) {
            facebookLoginButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            facebookLoginButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

        if (state.isLoggedId) {
            Timber.e("Logged in but not implemented!!")
        }
    }

    //==========================================================================

    private fun initFbLogin() {
        facebookLoginButton.setReadPermissions(AuthorizeRepository.FACEBOOK_PERMISSIONS)
        facebookLoginButton.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                viewModel.onFacebookToken(result)
            }

            override fun onCancel() {
                Timber.w("Facebook login cancelled.")
            }

            override fun onError(error: FacebookException) {
                val message = error.localizedMessage
                renderError(message)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

}
