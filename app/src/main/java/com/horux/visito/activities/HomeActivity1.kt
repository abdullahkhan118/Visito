package com.horux.visito.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

class HomeActivity : PermissionActivity() {
    var viewModel: HomeViewModel? = null
    private var binding: ActivityHomeBinding? = null
    private var navHeadBinding: NavHeaderMainBinding? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProvider(this).get<HomeViewModel>(HomeViewModel::class.java)
        getWindow()
            .setStatusBarColor(ContextCompat.getColor(this.getApplicationContext(), R.color.orange))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setSupportActionBar(binding.homeAppBarMain.toolbar)
        navHeadBinding = DataBindingUtil.inflate(
            getLayoutInflater(),
            R.layout.nav_header_main,
            binding.navView,
            false
        )
        binding.navView.addHeaderView(navHeadBinding.getRoot())
        binding.navView.setItemIconTintList(null)
        setOnClickListeners()
        UserGlobals.user = FirebaseAuth.getInstance().getCurrentUser()
        viewModel.getUser().observe(this, Observer<Any?> { userModel ->
            Log.e("UserModel", userModel.toString())
            FCMService.Companion.setToken()
                .addOnCompleteListener(object : OnCompleteListener<String?>() {
                    fun onComplete(task: Task<String?>) {
                        if (task.isSuccessful()) {
                            userModel.setToken(task.getResult())
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
            OnNavigationItemSelectedListener() {
            fun onNavigationItemSelected(item: MenuItem): Boolean {
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
                Log.e("Fragment", fragment.getClass().getName())
                replaceFragment(item.title.toString(), fragment)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
        })
    }

    fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.home_menu, menu)
        return true
    }

    /*
    For each menu a specific action is performed and based on that the item.isChecked value is changed
    */
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.signout) {
            var dataIsEmpty =
                viewModel.getUser().getValue().getName() == null || "" == viewModel.getUser()
                    .getValue().getName()
            dataIsEmpty = dataIsEmpty || viewModel.getUser().getValue()
                .getPhoneNumber() == null || "" == viewModel.getUser().getValue().getPhoneNumber()
            dataIsEmpty = dataIsEmpty || viewModel.getUser().getValue()
                .getPassword() == null || "" == viewModel.getUser().getValue().getPassword()
            if (dataIsEmpty) {
                replaceFragment("Account", AccountFragment())
            } else {
                viewModel.logout()
                startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun onStart() {
        super.onStart()
        viewModel.getUser().observe(this, Observer<Any?> { userModel ->
            navHeadBinding.navHeaderName.setText(userModel.getName())
            navHeadBinding.navHeaderEmail.setText(userModel.getEmail())
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
        getSupportActionBar().setTitle(title)
        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.nav_host_fragment, fragment)
            .commit()
    }

    private fun replaceFragment(title: String, fragment: Fragment) {
        Log.e("Fragment", "Replaced")
        binding.homeAppBarMain.toolbar.setTitle(title)
        getSupportActionBar().setTitle(title)
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }

    fun onBackPressed() {
        if (!getSupportActionBar().getTitle().equals("Places")) addFragment(
            "Places",
            PlacesFragment()
        ) else super.onBackPressed()
    }
}