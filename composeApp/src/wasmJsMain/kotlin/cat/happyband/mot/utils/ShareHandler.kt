package cat.happyband.mot.utils
import kotlin.js.JsName

@JsName("openWhatsApp")
private fun openWhatsApp(url: String) {
    js("window.open(url, '_blank')")
}

actual fun shareResult(text: String) {
    val encodedText = encodeURIComponent(text)

    val whatsappUrl = "https://web.whatsapp.com/send?text=$encodedText"

    openWhatsApp(whatsappUrl)
}
private external fun encodeURIComponent(uri: String): String