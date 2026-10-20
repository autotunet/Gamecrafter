package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PowerUpType
import com.example.ui.theme.AccentGold
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.RighteousFontFamily

@Composable
fun PowerUpBar(
    coins: Int,
    activePowerUp: PowerUpType?,
    onPowerUpClick: (PowerUpType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PowerUpButton(
            type = PowerUpType.ROTATE,
            icon = Icons.Default.RotateRight,
            isActive = false,
            canAfford = coins >= PowerUpType.ROTATE.cost,
            onClick = { onPowerUpClick(PowerUpType.ROTATE) }
        )
        PowerUpButton(
            type = PowerUpType.HAMMER,
            icon = Icons.Default.Build,
            isActive = activePowerUp == PowerUpType.HAMMER,
            canAfford = coins >= PowerUpType.HAMMER.cost,
            onClick = { onPowerUpClick(PowerUpType.HAMMER) }
        )
        PowerUpButton(
            type = PowerUpType.BOMB,
            icon = Icons.Default.AutoAwesome,
            isActive = activePowerUp == PowerUpType.BOMB,
            canAfford = coins >= PowerUpType.BOMB.cost,
            onClick = { onPowerUpClick(PowerUpType.BOMB) }
        )
        PowerUpButton(
            type = PowerUpType.REFRESH,
            icon = Icons.Default.Refresh,
            isActive = false,
            canAfford = coins >= PowerUpType.REFRESH.cost,
            onClick = { onPowerUpClick(PowerUpType.REFRESH) }
        )
    }
}

@Composable
fun PowerUpButton(
    type: PowerUpType,
    icon: ImageVector,
    isActive: Boolean,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    val strings = com.example.localization.LocalAppStrings.current
    val powerUpTitle = when (type) {
        PowerUpType.ROTATE -> strings.powerUpRotate
        PowerUpType.HAMMER -> strings.powerUpHammer
        PowerUpType.BOMB -> strings.powerUpBomb
        PowerUpType.REFRESH -> strings.powerUpRefresh
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag("powerup_${type.name.lowercase()}")
            .clickable(enabled = canAfford, onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) Color(0xFFFF3D71)
                    else if (canAfford) Color(0xFF281E4B)
                    else Color(0xFF1E1E2E).copy(alpha = 0.6f)
                )
                .border(
                    width = if (isActive) 2.5.dp else 1.2.dp,
                    color = if (isActive) AccentGold else Color(0xFF4C3A7A),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = powerUpTitle,
                tint = if (isActive) Color.White else if (canAfford) AccentGold else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = powerUpTitle,
            fontFamily = FredokaFontFamily,
            fontSize = 11.sp,
            color = if (canAfford) Color.White else Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🪙 ${type.cost}",
                fontFamily = RighteousFontFamily,
                fontSize = 10.sp,
                color = AccentGold
            )
        }
    }
}
