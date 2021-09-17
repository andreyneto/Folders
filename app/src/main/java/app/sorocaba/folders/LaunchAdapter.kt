package app.sorocaba.folders

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.launch_item.view.*


class LaunchAdapter(private val items: MutableList<AppModel>): RecyclerView.Adapter<LaunchAdapter.ViewHolder>() {

    class ViewHolder(private val v: View): RecyclerView.ViewHolder(v) {

        fun bind(item: AppModel) = with(v) {
            item.bitmap?.let {
                Palette.from(it).generate {
                    card.setCardBackgroundColor(context.parseColor(it?.dominantSwatch?.rgb))
                    it?.dominantSwatch?.bodyTextColor?.let { it1 ->
                        label.setTextColor(it1)

//                        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                        image.eraseColor(context.getAlphaColor(it?.vibrantSwatch?.rgb))
//                        circleImageView.setImageBitmap(image)
                    }
                }
            }
            click.setOnClickListener {
                val launchIntentForPackage = context.packageManager.getLaunchIntentForPackage(item.packageName)
                context.startActivity(launchIntentForPackage)
            }
            img.setImageBitmap(item.bitmap)
            label.text = item.name
        }
    }

    private lateinit var vh: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        vh = ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.launch_item, parent, false))
        return vh
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size
}