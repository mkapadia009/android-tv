package com.app.itaptv.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.itaptv.R
import com.app.itaptv.utils.Constant.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_app_info.*

class AppInfoActivity : AppCompatActivity() {

    private lateinit var title: String
    private lateinit var desc: String
    private lateinit var image: String
    private lateinit var maintenanceType: String
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        init()
    }

    private fun init() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            title = bundle.getString(KEY_TITLE)!!
            desc = bundle.getString(KEY_DESCRIPTION)!!
            image = bundle.getString(KEY_IMAGE)!!
            maintenanceType = bundle.getString(KEY_INFO_TYPE)!!
            url = bundle.getString(KEY_APP_URL)!!

            infoTitle.text = title
            infoDescription.text = desc
            Glide.with(this)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(infoImage)

            when (maintenanceType) {
                MAINTENANCE_APP_UPDATE -> {
                    infoBtn.text = resources.getString(R.string.label_app_update)
                }

                MAINTENANCE_SYSTEM_DOWN -> {
                    infoBtn.text = resources.getString(R.string.label_come_later)
                }

                MAINTENANCE_GENERAL -> {
                    infoBtn.text = resources.getString(R.string.label_continue)
                }
            }
        }

        infoBtn.setOnClickListener {
            when (maintenanceType) {
                MAINTENANCE_APP_UPDATE -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    finishAffinity()
                }

                MAINTENANCE_SYSTEM_DOWN -> {
                    finishAffinity()
                }

                MAINTENANCE_GENERAL -> {
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (maintenanceType) {
            MAINTENANCE_APP_UPDATE -> {
                finishAffinity()
            }

            MAINTENANCE_SYSTEM_DOWN -> {
                finishAffinity()
            }

            MAINTENANCE_GENERAL -> {
                finish()
            }
        }
    }
}
