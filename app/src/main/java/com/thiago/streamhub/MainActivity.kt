package com.thiago.streamhub

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thiago.streamhub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ServiceAdapter(
            items = StreamingCatalog.services,
            packageManager = packageManager
        ) { service, isInstalled ->
            if (isInstalled) {
                openInstalledApp(service.packageName)
            } else {
                openPlayStore(service.playStoreId, service.webFallbackUrl)
            }
        }
    }

    /** Abre o app oficial já instalado (login e streaming continuam pelo app dele). */
    private fun openInstalledApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            // Pacote existe mas sem launch intent exposto — tenta abrir pela Play Store.
            openPlayStore(packageName, null)
        }
    }

    /** Se não estiver instalado, manda para a Play Store; se não tiver Play Store, abre o site. */
    private fun openPlayStore(playStoreId: String, webFallbackUrl: String?) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$playStoreId")
                )
            )
        } catch (e: ActivityNotFoundException) {
            val url = webFallbackUrl
                ?: "https://play.google.com/store/apps/details?id=$playStoreId"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
}
