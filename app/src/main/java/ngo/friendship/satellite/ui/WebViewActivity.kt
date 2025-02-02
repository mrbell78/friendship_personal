package ngo.friendship.satellite.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import ngo.friendship.satellite.App
import ngo.friendship.satellite.constants.ActivityDataKey
import android.webkit.WebViewClient
import android.webkit.WebView
import android.webkit.WebChromeClient
import ngo.friendship.satellite.R
import ngo.friendship.satellite.LanguageContextWrapper
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.databinding.WebviewLayoutBinding
import ngo.friendship.satellite.model.AppSettings
import ngo.friendship.satellite.utility.Utility
import java.lang.Exception

// TODO: Auto-generated Javadoc
/**
 * The Class WebViewActivity.
 */
class WebViewActivity : BaseActivity() {
    private lateinit var binding: WebviewLayoutBinding
    private var TYPE: String? = ""

    /** The url.  */
    var url: String? = null

    /* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.requestFeature(Window.FEATURE_PROGRESS)
        App.loadApplicationData(this)
        binding = WebviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableBackButton();
        title = ""+resources.getString(R.string.menu_help)
        val b = intent.extras
        url = b!!.getString(ActivityDataKey.WEB_URL)
        TYPE = b.getString(ActivityDataKey.TYPE)
        binding.mWebView.loadUrl(url!!)
        binding.mWebView.settings.javaScriptEnabled = true
        binding.mWebView.settings.saveFormData = true
        binding.mWebView.settings.builtInZoomControls = true
        binding.mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // TODO Auto-generated method stub
                view.loadUrl(url)
                return true
            }
        }
        if (TYPE == "ABOUTAA") {
            binding.mWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    val date = Utility.getAppCreateDate(this@WebViewActivity)
                    val version = Utility.getVersionName(this@WebViewActivity)
                    try {
                        view.loadUrl(" javascript:putVersionAndDate('$version', '$date'); ")
                        //view.loadUrl(" javascript:set('"+Constants.T_1+"', '"+(new CryptoUtil()).decrypt(Constants.CRYPTO_KEY,Constants.T_2)+"'); ");
                    } catch (e: Exception) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                }
            }
        }
        binding.mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress)
                this@WebViewActivity.title = "Loading..."
                setProgress(newProgress * 100)
                if (newProgress == 100) {
                    setProgressBarIndeterminateVisibility(false)
                    setProgressBarVisibility(false)
                    this@WebViewActivity.setTitle(R.string.app_name)
                }
            }
        }
    }

    /* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
    override fun onBackPressed() {
        if (binding.mWebView.canGoBack()) binding.mWebView.goBack() else finish()
    }

    public override fun onStart() {
        super.onStart()
        App.getContext().onStartActivity(this)
    }

    public override fun onStop() {
        super.onStop()
        App.getContext().onStartActivity(this)
    }

}