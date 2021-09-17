package app.sorocaba.folders

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.*
import android.graphics.drawable.*
import android.util.TypedValue
import android.view.ContextThemeWrapper
import com.google.gson.Gson
import kotlin.math.roundToInt


fun Any.toJson() = Gson().toJson(this)

inline fun <reified T> String.fromJson() = Gson().fromJson(this, T::class.java)

fun Context.addShortcut(model: FolderModel, icon: Bitmap?) = getSystemService(
        ShortcutManager::class.java
)?.let { shortcutManager->
    // Assumes there's already a shortcut with the ID "my-shortcut".
    // The shortcut must be enabled.
    val tintImage = icon?.tintImage(this)
    val createWithBitmap = Icon.createWithBitmap(tintImage)
    val pinShortcutInfo = ShortcutInfo.Builder(this, model.id)
            .setShortLabel(
                    if (model.label.isNullOrEmpty()) "Nova pasta" else model.label
                            ?: "Nova pasta"
            )
            .setIcon(createWithBitmap)
            .setIntent(
                    Intent(this, OpenFolderActivity::class.java).apply {
                        putExtra("extra", model.id)
                        action = Intent.ACTION_VIEW
                    }
            )
            .build()
    save(model)
    shortcutManager.addDynamicShortcuts(listOf(pinShortcutInfo))
    requestInstall(pinShortcutInfo)
}

fun Context.read(id: String): FolderModel? = getSharedPreferences("SHARED_FOLDERS_SOROCABA", Context.MODE_PRIVATE)?.let {
    with(it) {
        val string = getString(id, "{}") ?: "{}"
        return@let string.fromJson<FolderModel>()
    }
}

fun Context.save(model: FolderModel) = getSharedPreferences("SHARED_FOLDERS_SOROCABA", Context.MODE_PRIVATE)?.edit()?.let {
    with(it) {
        putString(model.id, model.toJson())
        commit()
    }
}

fun Context.deleteShortcut(pinShortcutInfo: ShortcutInfo) = getSystemService(ShortcutManager::class.java)?.let { shortcutManager ->
    shortcutManager.removeDynamicShortcuts(listOf(pinShortcutInfo.id))
}

fun Context.requestInstall(pinShortcutInfo: ShortcutInfo) = getSystemService(ShortcutManager::class.java)?.let { shortcutManager->
    // Create the PendingIntent object only if your app needs to be notified
    // that the user allowed the shortcut to be pinned. Note that, if the
    // pinning operation fails, your app isn't notified. We assume here that the
    // app has implemented a method called createShortcutResultIntent() that
    // returns a broadcast intent.
    val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(
            pinShortcutInfo
    )

    // Configure the intent so that your app's broadcast receiver gets
    // the callback successfully.For details, see PendingIntent.getBroadcast().
    val successCallback = PendingIntent.getBroadcast(
            this, /* request code */ 0,
            pinnedShortcutCallbackIntent, /* flags */ 0
    )
    shortcutManager.requestPinShortcut(
            pinShortcutInfo,
            successCallback.intentSender
    )
}

fun Bitmap.tintImage(context: Context): Bitmap? {
    val paint = Paint()
    paint.colorFilter = PorterDuffColorFilter(context.getColor(), PorterDuff.Mode.SRC_IN)
    val bitmapResult = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmapResult)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return bitmapResult
}

fun Drawable.toBitmap(): Bitmap? {
    if (this is BitmapDrawable) {
        return this.bitmap
    }
    val bitmap = Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Context.getAlphaColor(color: Int?): Int {
    val str = String.format("#%06X", 0xFFFFFF and (color?:getColor()))
    return Color.parseColor("#AA${str.split("#").last()}")
}

fun Context.parseColor(color: Int?): Int {
    val str = String.format("#%06X", 0xFFFFFF and (color?:getColor()))
    return Color.parseColor(str)
}

fun Context.getColor(): Int {
    return getColor(R.color.black)
    val typedValue = TypedValue()
    val contextThemeWrapper = ContextThemeWrapper(
            this,
            android.R.style.Theme_DeviceDefault
    )
    contextThemeWrapper.theme.resolveAttribute(
            android.R.attr.colorAccent,
            typedValue, true
    )
    return typedValue.data
}

fun Context.getApps(): MutableList<ResolveInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    return packageManager.queryIntentActivities(intent, 0)
}

fun Bitmap.padding(padding_x: Float = 32f, padding_y: Float = 32f): Bitmap? {
    val outputImage =
        Bitmap.createBitmap((width + padding_x * 2).roundToInt(), (height + padding_y * 2).roundToInt(), Bitmap.Config.ARGB_8888)
    val can = Canvas(outputImage)
    can.drawARGB(0, 0, 0, 0) //This represents White color
    can.drawBitmap(this, padding_x, padding_y, null)
    return outputImage
}

fun PackageManager.getAppIcon(packageName: String): Bitmap? {
    try {
        val drawable = getApplicationIcon(packageName)
        if (drawable is BitmapDrawable) {
            return drawable.bitmap.padding()
        } else if (drawable is AdaptiveIconDrawable) {
            return drawable.toBitmap()
            val backgroundDr = drawable.background
            val foregroundDr = drawable.foreground
            val drr = arrayOfNulls<Drawable>(2)
            drr[0] = backgroundDr
            drr[1] = foregroundDr
            val layerDrawable = LayerDrawable(drr)
            val width = layerDrawable.intrinsicWidth
            val height = layerDrawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
            layerDrawable.draw(canvas)
            return bitmap
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return null
}