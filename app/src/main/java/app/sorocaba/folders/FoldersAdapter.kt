package app.sorocaba.folders

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.android.synthetic.main.folder_item.view.*

class FoldersAdapter(private val items: List<ShortcutInfo>): RecyclerView.Adapter<FoldersAdapter.ViewHolder>() {

    class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {

        fun bind(item: ShortcutInfo) = with(v) {
            label.text = item.shortLabel
            val icon = App.iconPack?.getIcon(item.id.split("###").first().toIntOrNull()?:812)
            btn.setImageBitmap(icon?.drawable?.toBitmap()?.tintImage(context))
            btn.setOnLongClickListener {
                context.requestInstall(item) == true
            }
            btn.setOnClickListener {
                context.startActivity(Intent(context, FolderActivity::class.java).apply {
                    putExtra("extra", item.id)
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = with(parent) {
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.folder_item, this, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}