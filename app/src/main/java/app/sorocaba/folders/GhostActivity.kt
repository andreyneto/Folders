package app.sorocaba.folders

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class GhostActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            intent.getStringExtra("extra")?.let {
                FolderDialog.newInstance(it, supportFragmentManager) {
                    finish()
                }
            }
        }, 300)
    }
}

class FolderDialog: BottomSheetDialogFragment() {

    override fun getTheme(): Int  = R.style.Theme_NoWiredStrapInNavigationBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.folder_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { with(it) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.navigationBarColor = ContextCompat.getColor(this, R.color.surface)
        } }
    }

    override fun onDismiss(dialog: DialogInterface) {
        dismissFun?.invoke()
        super.onDismiss(dialog)
    }

    companion object {

        private var currentId: String? = null

        private var dismissFun: (()->Unit)? = null

        internal fun newInstance(id: String, manager: FragmentManager, onDismiss: () -> Unit) {
            currentId = id
            dismissFun = onDismiss
            FolderDialog().show(manager, currentId)
        }
    }
}