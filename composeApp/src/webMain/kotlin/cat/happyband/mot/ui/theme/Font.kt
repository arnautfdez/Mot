package cat.happyband.mot.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import mot.composeapp.generated.resources.Res
import mot.composeapp.generated.resources.Poppins_Bold
import mot.composeapp.generated.resources.Poppins_Regular
import org.jetbrains.compose.resources.Font

internal val PoppinsFontFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.Poppins_Bold, FontWeight.Normal),
        Font(Res.font.Poppins_Regular, FontWeight.Bold)
    )
