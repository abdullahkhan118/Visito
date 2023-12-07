package com.horux.visito.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.horux.visito.R
import com.horux.visito.databinding.ActivityHomeBinding
import com.horux.visito.databinding.NavHeaderMainBinding
import com.horux.visito.fragments.AboutUsFragment
import com.horux.visito.fragments.AccountFragment
import com.horux.visito.fragments.EventsFragment
import com.horux.visito.fragments.FavoritesFragment
import com.horux.visito.fragments.HelpFragment
import com.horux.visito.fragments.HotelsFragment
import com.horux.visito.fragments.MapsFragment
import com.horux.visito.fragments.PlacesFragment
import com.horux.visito.fragments.RestaurantsFragment
import com.horux.visito.globals.UserGlobals
import com.horux.visito.network_check.InternetCheck
import com.horux.visito.operations.ui_operations.DialogPrompt
import com.horux.visito.services.FCMService
import com.horux.visito.viewmodels.HomeViewModel

class HomeActivity : PermissionActivity() {
    val viewModel: HomeViewModel by viewModels<HomeViewModel>()
    private val binding: ActivityHomeBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_home) }
    private val navHeadBinding: NavHeaderMainBinding by lazy { DataBindingUtil.inflate(
        layoutInflater,
        R.layout.nav_header_main,
        binding.navView,
        false
    ) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getWindow()
            .setStatusBarColor(ContextCompat.getColor(this.getApplicationContext(), R.color.orange))
        setSupportActionBar(binding.homeAppBarMain.toolbar)
        binding.navView.addHeaderView(navHeadBinding.getRoot())
        binding.navView.setItemIconTintList(null)
        setOnClickListeners()
        UserGlobals.user = FirebaseAuth.getInstance().getCurrentUser()
        viewModel.user.observe(this, Observer { userModel ->
            Log.e("UserModel", userModel.toString())
            FCMService.Companion.setToken()
                .addOnCompleteListener(object : OnCompleteListener<String> {
                    override fun onComplete(task: Task<String>) {
                        if (task.isSuccessful()) {
                            userModel.token = (task.getResult())
                            viewModel.updateUser(userModel)
                        }
                    }
                })
        })
        addFragment("Places", PlacesFragment())
    }

    private fun setOnClickListeners() {
        binding.homeAppBarMain.toolbar.setNavigationOnClickListener(View.OnClickListener {
            binding.drawerLayout.openDrawer(
                GravityCompat.START
            )
        })
        binding.navView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val fragment: Fragment
                when (item.itemId) {
                    R.id.account -> fragment = AccountFragment()
                    R.id.favorite -> fragment = FavoritesFragment()
                    R.id.maps -> fragment = MapsFragment()
                    R.id.places -> fragment = PlacesFragment()
                    R.id.hotels -> fragment = HotelsFragment()
                    R.id.restaurants -> fragment = RestaurantsFragment()
                    R.id.events -> fragment = EventsFragment()
                    R.id.help -> fragment = HelpFragment()
                    else -> fragment = AboutUsFragment()
                }
                replaceFragment(item.title.toString(), fragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.home_menu, menu)
        return true
    }

    /*
    For each menu a specific action is performed and based on that the item.isChecked value is changed
    */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signout) {
            val dataIsEmpty = with(viewModel.user.value) {
                this?.name.isNullOrEmpty() || this?.phoneNumber.isNullOrEmpty() || this?.password.isNullOrEmpty()
            }
            if (dataIsEmpty) {
                replaceFragment("Account", AccountFragment())
            } else {
                viewModel.logout()
                startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.user.observe(this, Observer { userModel ->
            navHeadBinding.navHeaderName.setText(userModel.name)
            navHeadBinding.navHeaderEmail.setText(userModel.name)
        })
    }

    val isInternetAvailable: Boolean
        get() {
            val isConnected: Boolean = InternetCheck.internetAvailable(this)
            if (!isConnected) showMessage("Internet not available")
            setLoaderVisibility(isConnected)
            return isConnected
        }

    fun setLoaderVisibility(show: Boolean) {
        if (show) binding.homeProgress.setVisibility(View.VISIBLE) else binding.homeProgress.setVisibility(
            View.GONE
        )
    }

    fun showMessage(message: String?) {
        DialogPrompt().showMessage(this, getLayoutInflater(), message)
    }

    private fun addFragment(title: String, fragment: Fragment) {
        Log.e("Fragment", "Added")
        binding.homeAppBarMain.toolbar.setTitle(title)
        supportActionBar?.setTitle(title)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun replaceFragment(title: String, fragment: Fragment) {
        Log.e("Fragment", "Replaced")
        binding.homeAppBarMain.toolbar.setTitle(title)
        supportActionBar?.setTitle(title)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (supportActionBar?.title?.equals("Places") == true) addFragment(
            "Places",
            PlacesFragment()
        ) else super.onBackPressed()
    }
}