package cat.happyband.mot.utils
import kotlin.js.JsName

@JsName("openBrowserWindow")
private fun openBrowserWindow(url: String) {
    js("window.open(url, '_blank')")
}

actual fun shareResult(text: String) {
    val encodedText = encodeURIComponent(text)

    val universalUrl = "https://api.whatsapp.com/send?text=$encodedText"

    openBrowserWindow(universalUrl)
}
private external fun encodeURIComponent(uri: String): String