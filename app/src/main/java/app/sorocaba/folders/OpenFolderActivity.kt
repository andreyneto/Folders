package app.sorocaba.folders

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.open_folder_activity.*


class OpenFolderActivity: AppCompatActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_folder_activity)
        intent.getStringExtra("extra")?.let { id ->
            parse(read(id)) {
                it.sortBy { it.name }
                apps.layoutManager = GridLayoutManager(this, 2)
                apps.adapter = LaunchAdapter(it)
            }
        }
        back.setOnClickListener {
            super.finish()
        }
    }

    private fun parse(read: FolderModel?, function: (apps: MutableList<AppModel>) -> Unit) = with(packageManager) {
        val l = mutableListOf<AppModel>()
        read?.apps?.forEach {
            try {
                val name = this.getApplicationLabel(this.getApplicationInfo(it, 0)).toString()
                val icon = packageManager.getAppIcon(it)
                l.add(AppModel(it, name, icon))
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        function.invoke(l)
    }

    override fun onPause() {
        super.finish()
        super.onPause()
    }

}