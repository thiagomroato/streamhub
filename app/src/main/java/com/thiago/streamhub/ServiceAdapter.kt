package com.thiago.streamhub

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thiago.streamhub.databinding.ItemServiceBinding

class ServiceAdapter(
    private var items: List<StreamingService>,
    private val packageManager: PackageManager,
    private var favoritePackages: Set<String>,
    private val onClick: (StreamingService, isInstalled: Boolean) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder = ServiceViewHolder(
        ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = items[position]
        val installed = isAppInstalled(service.packageName)
        holder.binding.serviceName.text = service.name
        holder.binding.statusText.text = holder.itemView.context.getString(
            if (installed) R.string.open_action else R.string.not_installed_action
        )
        holder.binding.availabilityText.text = holder.itemView.context.getString(
            if (installed) R.string.installed_status else R.string.available_status
        )
        holder.binding.favoriteButton.text = if (favoritePackages.contains(service.packageName)) "★" else "☆"
        holder.binding.favoriteButton.contentDescription = holder.itemView.context.getString(
            if (favoritePackages.contains(service.packageName)) R.string.remove_favorite else R.string.add_favorite
        )
        holder.binding.root.setOnClickListener { onClick(service, installed) }
        holder.binding.favoriteButton.setOnClickListener {
            (holder.itemView.context as? MainActivity)?.toggleFavorite(service)
        }
    }

    override fun getItemCount(): Int = items.size

    fun replaceItems(newItems: List<StreamingService>, newFavorites: Set<String>) {
        items = newItems
        favoritePackages = newFavorites
        notifyDataSetChanged()
    }

    fun updateFavorites(newFavorites: Set<String>) {
        favoritePackages = newFavorites
        notifyDataSetChanged()
    }

    private fun isAppInstalled(packageName: String): Boolean = try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
