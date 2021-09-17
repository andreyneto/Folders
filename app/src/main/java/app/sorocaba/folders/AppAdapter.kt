package app.sorocaba.folders

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_item.view.*


class AppAdapter(private val items: MutableList<AppModel>,
                 private var selectedApps: MutableList<String>): RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    fun getSelected() = vh.selectedApps

    fun setSelected(selectedApps: MutableList<String>) {
        this.selectedApps = selectedApps
        selectedApps.forEach { app ->
            items.find { it.packageName == app }?.let {
                moveItem(items.indexOf(it), 0)
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(private val v: View,
                     val selectedApps: MutableList<String>): RecyclerView.ViewHolder(v) {

        fun bind(item: AppModel, click: (item: AppModel, to: Int, scrollTo: Boolean) -> Unit) = with(v) {

            if(selectedApps.contains(item.packageName)) {
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_100))
                card.strokeColor = ContextCompat.getColor(context, R.color.purple_500)
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
                card.strokeColor = Color.parseColor("#e5e5e5")
            }
            setOnClickListener {
                if(selectedApps.contains(item.packageName)) {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.surface))
                    card.strokeColor = Color.parseColor("#e5e5e5")
                    selectedApps.remove(item.packageName)
                    click.invoke(item, selectedApps.size, false)
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_100))
                    card.strokeColor = ContextCompat.getColor(context, R.color.purple_500)
                    click.invoke(item, selectedApps.size, true)
                    selectedApps.add(0, item.packageName)
                }
            }
            img.circleBackgroundColor = context.getColor()
            img.setImageBitmap(item.bitmap)
            label.text = item.name
        }
    }

    private var mRecyclerView: RecyclerView? = null

    private lateinit var vh: ViewHolder

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        vh = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false),
            selectedApps
        )
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position]) { item, to, scrollTo ->
        val from = items.indexOf(item)
        moveItem(from, to)
        notifyItemMoved(from, to)
    }

    override fun getItemCount() = items.size

    fun moveItem(from: Int, to: Int) {
        val appModel = items[from]
        items.remove(appModel)
        items.add(to, appModel)
    }
}