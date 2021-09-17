package app.sorocaba.folders

import android.content.pm.ShortcutManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.android.synthetic.main.activity_folder.*
import java.util.*


class FolderActivity: AppCompatActivity(), IconDialog.Callback {

    private var folderModel = FolderModel()
    private var bmp: Bitmap? = null
    private var iconId: String? = null
    private val adapter by lazy { AppAdapter(App.apps, mutableListOf()) }

    private val iconDialog = supportFragmentManager.findFragmentByTag(App.ICON_DIALOG_TAG) as IconDialog?
        ?: IconDialog.newInstance(IconDialogSettings())

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        bmp = ContextCompat.getDrawable(this, R.drawable.ic_round_folder_open_24)?.toBitmap()

        delete.hide()

        apps.layoutManager = GridLayoutManager(this, 4)
        apps.adapter = adapter
        itemTouchHelper.attachToRecyclerView(apps)

        intent.getStringExtra("extra")?.let { id ->
            adapter.setSelected(read(id)?.apps ?: mutableListOf())
            group.visibility = View.GONE
            getSystemService(ShortcutManager::class.java)?.let { shortcutManager ->
                shortcutManager.dynamicShortcuts.find { it.id == id }?.let { item ->
                    delete.show()
                    delete.setOnClickListener {
                        this.deleteShortcut(item)
                        super.onBackPressed()
                    }
                }
            }
        }

        icon.setOnClickListener {
            iconDialog.show(supportFragmentManager, App.ICON_DIALOG_TAG)
        }

        save.setOnClickListener {
            folderModel.label = edt.text.toString()
            folderModel.id = iconId ?: folderModel.id// + "###" + UUID.randomUUID()
            folderModel.apps = adapter.getSelected()
            this.addShortcut(folderModel, bmp)
            super.onBackPressed()
        }

    }

    override val iconDialogIconPack: IconPack?
        get() = App.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        // Show a toast with the list of selected icon IDs.
        icons.firstOrNull()?.let {
            folderModel.id = it.id.toString()
            it.drawable?.toBitmap()?.tintImage(this)?.let {
                icon.setImageBitmap(it)
                bmp = it
            }
        }
    }

    private val itemTouchHelper by lazy {
        // 1. Note that I am specifying all 4 directions.
        //    Specifying START and END also allows
        //    more organic dragging than just specifying UP and DOWN.
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END, 0) {

                private var strokeColor = 0

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?,
                                               actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.scaleX = 1.05f
                        viewHolder?.itemView?.scaleY = 1.05f
                        viewHolder?.itemView?.findViewById<MaterialCardView>(R.id.card)?.let {
                            strokeColor = it.strokeColor
                            it.strokeColor = Color.parseColor("#00000000")
                            it.cardElevation = 8f
                        }
                    }
                }
                // 2. This callback is called when the ViewHolder is
                //    unselected (dropped). We unhighlight the ViewHolder here.
                override fun clearView(recyclerView: RecyclerView,
                                       viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.scaleX = 1f
                    viewHolder.itemView.scaleY = 1f
                    viewHolder.itemView.findViewById<MaterialCardView>(R.id.card)?.let {
                        it.strokeColor = strokeColor
                        it.cardElevation = 0f
                    }
                }
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {

                    val adapter = recyclerView.adapter as AppAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    // 2. Update the backing model. Custom implementation in
                    //    MainRecyclerViewAdapter. You need to implement
                    //    reordering of the backing model inside the method.
                    adapter.moveItem(from, to)
                    // 3. Tell adapter to render the model update.
                    adapter.notifyItemMoved(from, to)

                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }
}