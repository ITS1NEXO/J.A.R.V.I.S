package com.zyvix.jarvisos

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.text.Editable
import android.text.TextWatcher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val bg = Color.rgb(8, 17, 30)
    private val panel = Color.rgb(15, 31, 49)
    private val blue = Color.rgb(42, 151, 255)
    private val white = Color.rgb(235, 245, 255)
    private val muted = Color.rgb(139, 164, 190)
    private lateinit var grid: GridLayout
    private lateinit var scanGrid: GridLayout
    private lateinit var search: EditText
    private var apps: List<ResolveInfo> = emptyList()
    private var scanCandidates: List<ApplicationInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = 0
        buildUi()
        loadApps()
    }

    override fun onResume() {
        super.onResume()
        if (::grid.isInitialized) loadApps()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(bg); setPadding(dp(20), dp(22), dp(20), dp(12)) }
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val brand = text("J A R V I S   O S", 13f, blue, true).apply { letterSpacing = 0.16f }
        content.addView(brand, params(WRAP, dp(30)))
        val clock = text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()), 48f, white, true)
        content.addView(clock, params(WRAP, dp(62)))
        val date = text(SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()), 14f, muted, false)
        content.addView(date, params(WRAP, dp(28)))
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(17), dp(16), dp(17), dp(16)); background = rounded(panel, 24) }
        card.addView(text("JARVIS SYSTEM", 12f, blue, true))
        card.addView(text("Your device. Your command.", 19f, white, true).apply { setPadding(0, dp(8), 0, dp(5)) })
        card.addView(text("Native Android launcher • Expanded app discovery", 12f, muted, false))
        content.addView(card, params(MATCH, WRAP).apply { topMargin = dp(22); bottomMargin = dp(22) })
        search = EditText(this).apply {
            hint = "Search installed apps…"; setHintTextColor(muted); setTextColor(white); textSize = 14f
            setSingleLine(true); setPadding(dp(15), 0, dp(15), 0); background = rounded(panel, 15)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { renderApps(s?.toString().orEmpty()) }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        content.addView(search, params(MATCH, dp(50)))
        content.addView(text("YOUR APPLICATIONS", 12f, muted, true).apply { setPadding(0, dp(24), 0, dp(13)) })
        grid = GridLayout(this).apply { columnCount = 4; alignmentMode = GridLayout.ALIGN_BOUNDS; useDefaultMargins = false }
        content.addView(grid, params(MATCH, WRAP))
        content.addView(text("APP VISIBILITY SCAN", 12f, blue, true).apply { setPadding(0, dp(28), 0, dp(8)) })
        content.addView(text("Installed apps without a normal launcher shortcut. These may be system-only apps or disabled app entries — Android does not reveal OPPO Game Space or ColorOS hidden-app status to ordinary launchers.", 11f, muted, false))
        val scanButton = TextView(this).apply {
            text = "SCAN INSTALLED APPS"; textSize = 11f; setTextColor(white); gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD); background = rounded(panel, 14); setPadding(dp(14), dp(13), dp(14), dp(13))
            setOnClickListener { scanInstalledApps() }
        }
        content.addView(scanButton, params(MATCH, WRAP).apply { topMargin = dp(12); bottomMargin = dp(12) })
        scanGrid = GridLayout(this).apply { columnCount = 2; alignmentMode = GridLayout.ALIGN_BOUNDS; useDefaultMargins = false }
        content.addView(scanGrid, params(MATCH, WRAP))
        content.addView(text("Long-press HOME and choose JARVIS OS to set it as your default launcher.", 11f, muted, false).apply { setPadding(0, dp(22), 0, dp(20)) })
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(MATCH, 0, 1f))
        root.addView(text("ZYVIX  •  JARVIS OS 1.0.0", 10f, muted, true).apply { gravity = Gravity.CENTER; setPadding(0, dp(12), 0, dp(5)) })
        setContentView(root)
    }

    private fun loadApps() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .distinctBy { it.activityInfo.packageName }
            .sortedBy { it.loadLabel(packageManager).toString().lowercase(Locale.getDefault()) }
        renderApps(search.text?.toString().orEmpty())
    }

    private fun renderApps(query: String) {
        if (!::grid.isInitialized) return
        grid.removeAllViews()
        val filtered = apps.filter { it.loadLabel(packageManager).toString().contains(query, true) }
        filtered.forEach { info ->
            val item = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(3), dp(8), dp(3), dp(12)); isClickable = true; isFocusable = true }
            val icon = ImageView(this).apply { setImageDrawable(info.loadIcon(packageManager)); scaleType = ImageView.ScaleType.FIT_CENTER; contentDescription = info.loadLabel(packageManager) }
            item.addView(icon, LinearLayout.LayoutParams(dp(48), dp(48)))
            val label = text(info.loadLabel(packageManager).toString(), 10f, white, false).apply { gravity = Gravity.CENTER; maxLines = 2; setPadding(0, dp(6), 0, 0) }
            item.addView(label, params(MATCH, WRAP))
            item.setOnClickListener {
                val launch = packageManager.getLaunchIntentForPackage(info.activityInfo.packageName)
                if (launch != null) startActivity(launch)
            }
            grid.addView(item, GridLayout.LayoutParams().apply { width = 0; height = dp(94); columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); setMargins(dp(2), dp(2), dp(2), dp(2)) })
        }
        if (filtered.isEmpty()) {
            val empty = text("No matching apps found", 13f, muted, false)
            grid.addView(empty, GridLayout.LayoutParams().apply { width = MATCH; height = dp(60); columnSpec = GridLayout.spec(0, 4) })
        }
    }

    @Suppress("DEPRECATION")
    private fun scanInstalledApps() {
        scanGrid.removeAllViews()
        val launcherPackages = apps.map { it.activityInfo.packageName }.toSet()
        scanCandidates = packageManager.getInstalledApplications(0)
            .filter { it.packageName != packageName && it.packageName !in launcherPackages && packageManager.getLaunchIntentForPackage(it.packageName) == null }
            .sortedBy { it.loadLabel(packageManager).toString().lowercase(Locale.getDefault()) }
        if (scanCandidates.isEmpty()) {
            scanGrid.addView(text("No apps without launcher shortcuts were found.", 12f, muted, false), GridLayout.LayoutParams().apply { width = MATCH; height = dp(48); columnSpec = GridLayout.spec(0, 2) })
            return
        }
        scanCandidates.forEach { app ->
            val name = try { app.loadLabel(packageManager).toString() } catch (_: Exception) { app.packageName }
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL; setPadding(dp(10), dp(10), dp(10), dp(10))
                background = rounded(panel, 12)
            }
            row.addView(text(name, 12f, white, true))
            row.addView(text("No launcher shortcut", 10f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
            row.setOnClickListener {
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.net.Uri.parse("package:" + app.packageName))
                startActivity(intent)
            }
            scanGrid.addView(row, GridLayout.LayoutParams().apply {
                width = 0; height = WRAP; columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(3), dp(3), dp(3), dp(3))
            })
        }
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        text = value; textSize = size; setTextColor(color); if (bold) setTypeface(typeface, Typeface.BOLD)
    }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun params(w: Int, h: Int) = LinearLayout.LayoutParams(w, h)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    companion object { private const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT; private const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT }
}
