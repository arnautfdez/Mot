package cat.happyband.mot.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cat.happyband.mot.utils.shimmerBackground

@Composable
fun StatsSkeleton() {
    val placeholderColor = Color.LightGray // El color base

    // El títol
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f) // Per simular un títol curt
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerBackground()
            .padding(top = 16.dp)
    )
    Spacer(Modifier.height(16.dp))

    // 1. Targeta Personal (La Més Gran)
    Card(modifier = Modifier.fillMaxWidth().height(108.dp)) {
        Row(Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .size(width = 108.dp, height = 64.dp)
                        .shimmerBackground()
                )
            }
        }
    }
    Spacer(Modifier.height(32.dp))

    // 2. Gràfic de Distribució (La Zona Més Llarga)
    Card(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .shimmerBackground()
        )
    }
    Spacer(Modifier.height(32.dp))

    // 3. Files de Rànquing Global
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(40.dp)
            .padding(bottom = 8.dp)
            .background(placeholderColor) // El color de la capçalera
    )

    // 6 Línies d'usuaris
    repeat(6) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerBackground()
        )
    }
}