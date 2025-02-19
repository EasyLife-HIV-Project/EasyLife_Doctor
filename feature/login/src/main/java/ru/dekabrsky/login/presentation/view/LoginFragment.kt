package ru.dekabrsky.login.presentation.view

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
//import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.disposables.CompositeDisposable
import main.utils.gone
import main.utils.onTextChange
import main.utils.setBoolVisibility
import main.utils.visible
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.easylife.basic.di.module
import ru.dekabrsky.easylife.basic.fragments.BasicFragment
import ru.dekabrsky.easylife.basic.viewBinding.viewBinding
import ru.dekabrsky.easylife.scopes.Scopes
import ru.dekabrsky.login.R
import ru.dekabrsky.login.databinding.FmtLoginBinding
import ru.dekabrsky.login.presentation.presenter.LoginPresenter
import toothpick.Toothpick

class LoginFragment: BasicFragment(), LoginView {

    private val compositeDisposable by lazy { CompositeDisposable() }

    override val layoutRes: Int
    get() = R.layout.fmt_login

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    private val binding by viewBinding(FmtLoginBinding::bind)

    @ProvidePresenter
    fun providePresenter(): LoginPresenter {
        return Toothpick.openScopes(Scopes.SCOPE_FLOW_LOGIN, scopeName)
            .module {
                bind(Intent::class.java).toInstance(requireActivity().intent)
            }
            .getInstance(LoginPresenter::class.java)
            .also { Toothpick.closeScope(scopeName) }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextLogin.onTextChange { presenter.onLoginChanged(binding.editTextLogin.text.toString()) }
        binding.editTextPassword.onTextChange { presenter.onPasswordChanged(binding.editTextPassword.text.toString()) }
        binding.editTextCode.onTextChange { presenter.onCodeTextChanged(binding.editTextCode.text.toString()) }

        binding.loginCardBtn.setOnClickListener { presenter.onDoneButtonClick() }
        binding.changeMode.setOnClickListener { presenter.onChangeModeClick() }
        binding.termsText.setOnClickListener { presenter.onTermsTextClick() }
        binding.policyText.setOnClickListener { presenter.onPolicyTextClick() }
        binding.policyBox.setOnCheckedChangeListener { _, b -> presenter.onPolicyCheckedChanged(b) }
        binding.termsBox.setOnCheckedChangeListener { _, b -> presenter.onTermsCheckedChanged(b) }

        val notificationManager = ContextCompat.getSystemService(
            requireActivity().applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getNotificationRxPermission()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> getNotificationManagerPermission(notificationManager)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getNotificationManagerPermission(notificationManager: NotificationManager) {
        if (notificationManager.areNotificationsEnabled().not()) showAlertDialog()
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Перейдите в настройки и разрешите уведомления")
            .setMessage("Так вы сможете получать напоминания")
            .setPositiveButton("Перейти") { _, _ -> presenter.onGrantPermissionBySettingsClicked() }
            .setNegativeButton("Отменить", null)
            .setCancelable(false)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getNotificationRxPermission() {
//        val disposable = RxPermissions(this).requestEach(Manifest.permission.POST_NOTIFICATIONS)
//            .subscribe {
//                if (it.granted.not()) showAlertDialog()
//            }
//
//
//        compositeDisposable.add(disposable)
    }

    override fun setupForRegistration() {
        binding.title.text = getString(R.string.registration)
        setTitleBias(REG_TITLE_BIAS)
        binding.codeLayout.visible()
        binding.changeMode.text = getString(R.string.change_mode_to_login)
        binding.bgImage.setImageResource(R.drawable.ic_divan)
        binding.termsBlock.setBoolVisibility(true)
    }

    override fun setupForLogin() {
        binding.title.text = getString(R.string.login_title)
        setTitleBias(LOGIN_TITLE_BIAS)
        binding.codeLayout.gone()
        binding.changeMode.text = getString(R.string.registration)
        binding.bgImage.setImageResource(R.drawable.drawable_table)
        binding.termsBlock.setBoolVisibility(false)
    }

    private fun setTitleBias(bias: Float) {
        val params = binding.title.layoutParams as? ConstraintLayout.LayoutParams
        params?.verticalBias = bias

        binding.title.layoutParams = params
    }
    override fun setLogin(lastLogin: String) {
        binding.editTextLogin.setText(lastLogin)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        private const val REG_TITLE_BIAS = 0.25f
        private const val LOGIN_TITLE_BIAS = 0.45f

        fun newInstance() = LoginFragment()
    }
}