package io.jawziyya.azkar.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.fragment.app.FragmentContainerView
import io.jawziyya.azkar.App
import io.jawziyya.azkar.ui.core.hideKeyboard
import io.jawziyya.azkar.ui.core.navigation.AppStateChanger
import io.jawziyya.azkar.ui.main.MainScreenKey
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.navigator.Navigator
import com.zhuinden.simplestackextensions.fragments.DefaultFragmentKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import timber.log.Timber

class AppActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, AppActivity::class.java)
    }

    private val viewRoot by lazy {
        FragmentContainerView(this).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fixes issue when starting the app again from icon on launcher
        if (isTaskRoot.not()
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(viewRoot)

        // Fix for window insets for fragment navigation
        viewRoot.setOnApplyWindowInsetsListener { view, insets ->
            var consumed = false

            (view as ViewGroup).children.forEach { child ->
                val childResult = child.dispatchApplyWindowInsets(insets)
                if (childResult.isConsumed) {
                    consumed = true
                }
            }

            if (consumed) insets.consumeSystemWindowInsets() else insets
        }

        val fragmentStateChanger = AppStateChanger(supportFragmentManager, viewRoot.id)

        Navigator
            .configure()
            .setGlobalServices(App.instance.globalServices)
            .setScopedServices(DefaultServiceProvider())
            .setStateChanger(SimpleStateChanger { stateChange ->
                Timber.d(
                    "app_navigation - %s",
                    stateChange.getNewKeys<DefaultFragmentKey>().toString()
                )

                hideKeyboard()
                fragmentStateChanger.handleStateChange(stateChange)
            })
            .install(this, viewRoot, History.of(MainScreenKey()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }
}