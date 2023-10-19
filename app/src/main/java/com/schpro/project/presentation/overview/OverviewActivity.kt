package com.schpro.project.presentation.overview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.schpro.project.R
import com.schpro.project.core.extension.changeStatusBarColor
import com.schpro.project.databinding.ActivityOverviewBinding
import com.schpro.project.presentation.auth.AuthActivity
import com.schpro.project.presentation.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOverviewBinding

    companion object {
        fun callingIntent(context: Context, clearStack: Boolean = true) {
            val intent = Intent(context, OverviewActivity::class.java)
            if (clearStack) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent)
            if (clearStack) {
                (context as Activity).finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOverviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        changeStatusBarColor(getColor(R.color.black), true)
        initComponents()
    }

    private fun initComponents() {
        binding.btnMasuk.setOnClickListener {
            AuthActivity.callingIntent(
                this, AuthActivity.Companion.Args(
                    AuthActivity.Companion.Nav.Login
                )
            )
        }

        binding.btnDaftar.setOnClickListener {
            AuthActivity.callingIntent(
                this, AuthActivity.Companion.Args(
                    AuthActivity.Companion.Nav.Registrasi
                )
            )
        }

        spannableFooter()
    }

    private fun spannableFooter() {
        val spannable =
            SpannableString("dengan login kedalam aplikasi, kamu berarti menyetujui Syarat dan Ketentuan dari Schpro")

        spanToc(spannable)
        spanName(spannable)

        binding.tvTermsCondisitons.text = spannable
    }

    private fun spanToc(spannable: SpannableString) {
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(p0: View) {

                }

            }, 55, 75, Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            55,
            75,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.colorPrimary)),
            55, // start
            75, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
    }

    private fun spanName(spannable: SpannableString) {
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            81,
            87,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.colorPrimary)),
            81, // start
            87, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
    }
}