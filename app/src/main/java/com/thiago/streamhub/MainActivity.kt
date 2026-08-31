package com.thiago.streamhub

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.Settings
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thiago.streamhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ServiceAdapter
    private val preferences by lazy { getSharedPreferences("streamhub", MODE_PRIVATE) }
    private var showInstalledOnly = false
    private var showFavoritesOnly = false
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clockText.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        setupVehicleActions()

        adapter = ServiceAdapter(
            items = StreamingCatalog.services,
            packageManager = packageManager,
            favoritePackages = favorites()
        ) { service, isInstalled ->
            if (isInstalled) openInstalledApp(service.packageName)
            else openPlayStore(service.playStoreId, service.webFallbackUrl)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query = s?.toString()?.trim().orEmpty()
                refreshCatalog()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        binding.installedFilter.setOnClickListener {
            showInstalledOnly = !showInstalledOnly
            binding.installedFilter.isSelected = showInstalledOnly
            refreshCatalog()
        }
        binding.favoritesFilter.setOnClickListener {
            showFavoritesOnly = !showFavoritesOnly
            binding.favoritesFilter.isSelected = showFavoritesOnly
            refreshCatalog()
        }
        binding.clearFilters.setOnClickListener {
            binding.searchInput.text?.clear()
            showInstalledOnly = false
            showFavoritesOnly = false
            binding.installedFilter.isSelected = false
            binding.favoritesFilter.isSelected = false
            refreshCatalog()
        }
        refreshCatalog()
    }

    private fun setupVehicleActions() {
        binding.navGps.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=postos+de+combustível")))
            } catch (_: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com")))
            }
        }
        binding.navMusic.setOnClickListener {
            val musicIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC)
            try { startActivity(musicIntent) } catch (_: ActivityNotFoundException) {
                Toast.makeText(this, R.string.no_music_app, Toast.LENGTH_SHORT).show()
            }
        }
        binding.navBluetooth.setOnClickListener { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
        binding.navCamera.setOnClickListener {
            try { startActivity(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)) }
            catch (_: ActivityNotFoundException) { Toast.makeText(this, R.string.no_camera_app, Toast.LENGTH_SHORT).show() }
        }
        binding.navObd.setOnClickListener {
            Toast.makeText(this, R.string.obd_ready, Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
        }
        binding.navAssistant.setOnClickListener {
            try { startActivity(Intent(Intent.ACTION_VOICE_COMMAND)) }
            catch (_: ActivityNotFoundException) { Toast.makeText(this, R.string.no_assistant, Toast.LENGTH_SHORT).show() }
        }
        binding.navSettings.setOnClickListener { startActivity(Intent(Settings.ACTION_SETTINGS)) }
    }

    private fun refreshCatalog() {
        val favoritePackages = favorites()
        val filtered = StreamingCatalog.services.filter { service ->
            val matchesQuery = query.isBlank() || service.name.contains(query, ignoreCase = true)
            val installed = isAppInstalled(service.packageName)
            val matchesInstalled = !showInstalledOnly || installed
            val matchesFavorite = !showFavoritesOnly || favoritePackages.contains(service.packageName)
            matchesQuery && matchesInstalled && matchesFavorite
        }
        adapter.replaceItems(filtered, favoritePackages)
        val installedCount = StreamingCatalog.services.count { isAppInstalled(it.packageName) }
        binding.catalogCount.text = getString(R.string.catalog_count, filtered.size, installedCount)
        binding.emptyState.visibility = if (filtered.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun favorites(): Set<String> = preferences.getStringSet("favorites", emptySet()).orEmpty()

    fun toggleFavorite(service: StreamingService) {
        val updated = favorites().toMutableSet()
        if (!updated.add(service.packageName)) updated.remove(service.packageName)
        preferences.edit().putStringSet("favorites", updated).apply()
        adapter.updateFavorites(updated)
        refreshCatalog()
        Toast.makeText(this, if (updated.contains(service.packageName)) R.string.added_favorite else R.string.removed_favorite, Toast.LENGTH_SHORT).show()
    }

    private fun isAppInstalled(packageName: String): Boolean = try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (_: Exception) {
        false
    }

    private fun openInstalledApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) startActivity(launchIntent) else openPlayStore(packageName, null)
    }

    private fun openPlayStore(playStoreId: String, webFallbackUrl: String?) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$playStoreId")))
        } catch (_: ActivityNotFoundException) {
            val url = webFallbackUrl ?: "https://play.google.com/store/apps/details?id=$playStoreId"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
}
