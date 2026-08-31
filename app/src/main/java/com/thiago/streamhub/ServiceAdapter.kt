package com.thiago.streamhub

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thiago.streamhub.databinding.ItemServiceBinding

class ServiceAdapter(
    private val items: List<StreamingService>,
    private val packageManager: PackageManager,
    private val onClick: (StreamingService, isInstalled: Boolean) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = items[position]
        val installed = isAppInstalled(service.packageName)

        holder.binding.serviceName.text = service.name
        holder.binding.statusText.text =
            if (installed) "Abrir" else holder.itemView.context.getString(R.string.not_installed_action)

        holder.binding.root.setOnClickListener {
            onClick(service, installed)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
