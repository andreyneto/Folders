package app.sorocaba.folders

import android.graphics.Bitmap
import java.io.Serializable

data class FolderModel(
    var label: String? = null,
    var id: String = "812",
    var icon: Int = 812,
    var apps: MutableList<String> = mutableListOf()
): Serializable

data class AppModel(
    var packageName: String,
    var name: String,
    var bitmap: Bitmap?
): Serializable