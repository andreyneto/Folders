package app.sorocaba.folders

import android.Manifest
import android.content.Intent
import android.content.pm.ShortcutManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).start()
        setContentView(R.layout.activity_main)
        create.setOnClickListener {
            startActivity(Intent(this, FolderActivity::class.java))
        }
    }

    private fun requestPermission(function: () -> Unit) {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        function.invoke()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        requestPermission(function)
                    }

                    override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                                            }

                })
                .check()
    }

    override fun onResume() {
        super.onResume()
        requestPermission {
            getSystemService(ShortcutManager::class.java)?.let { shortcutManager ->
                rv.layoutManager = GridLayoutManager(this, 4)
                rv.adapter = FoldersAdapter(shortcutManager.dynamicShortcuts)
                if (shortcutManager.dynamicShortcuts.isNotEmpty()) Snackbar.make(rv, "Toque e segure para adicionar na tela inicial", Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }
}