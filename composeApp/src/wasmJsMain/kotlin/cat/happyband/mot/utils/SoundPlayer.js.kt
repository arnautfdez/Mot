package cat.happyband.mot.utils

fun playVictoryJsSound(): Unit = js("new Audio('victory.mp3').play()")

fun playLossJsSound(): Unit = js("new Audio('loss.mp3').play()")

actual object SoundPlayer {

    actual fun playVictorySound() {
        playVictoryJsSound()
    }

    actual fun playLossSound() {
        playLossJsSound()
    }
}