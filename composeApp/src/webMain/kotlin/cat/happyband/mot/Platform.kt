package cat.happyband.mot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform