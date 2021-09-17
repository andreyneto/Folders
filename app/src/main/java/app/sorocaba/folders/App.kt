package app.sorocaba.folders

import android.app.Application
import android.content.pm.PackageManager
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack

class App: Application() {

    fun start() {
        loadIconPack()
        startApps()
    }

    private fun loadIconPack() {
        // Create an icon pack loader with application context.
        val loader = IconPackLoader(this)

        // Create an icon pack and load all drawables.
        val iconPack = createDefaultIconPack(loader)
        iconPack.loadDrawables(loader.drawableLoader)

        App.iconPack = iconPack
    }

    private fun startApps() = with(applicationContext) {
        apps.clear()
        getApps().forEach { item ->
            try {
                val name = item.loadLabel(packageManager).toString()
                val packageName = item.activityInfo.packageName
                val icon = packageManager.getAppIcon(packageName)
                apps.add(AppModel(packageName, name, icon))
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        apps.sortBy { it.name }
    }

    companion object {
        var iconPack: IconPack? = null
        const val ICON_DIALOG_TAG = "ICON_DIALOG_TAG"
        var apps: MutableList<AppModel> = mutableListOf()
    }
}