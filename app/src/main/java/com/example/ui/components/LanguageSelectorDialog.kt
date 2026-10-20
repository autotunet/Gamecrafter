package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.AccentGold
import com.example.ui.theme.FredokaFontFamily
import com.example.ui.theme.RighteousFontFamily

@Composable
fun LanguageSelectorDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = AppStrings.get(currentLanguage)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1838)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4C3A7A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("language_selector_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = AccentGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.selectLanguageTitle,
                        fontFamily = RighteousFontFamily,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(AppLanguage.entries) { lang ->
                        val isSelected = lang == currentLanguage
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFF2C2250) else Color(0xFF15102A))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) AccentGold else Color(0xFF382E67),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onSelectLanguage(lang)
                                    onDismiss()
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("lang_item_${lang.code}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = lang.flagEmoji,
                                    fontSize = 22.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = lang.displayName,
                                    fontFamily = FredokaFontFamily,
                                    fontSize = 16.sp,
                                    color = if (isSelected) Color.White else Color(0xFFD1C4E9),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = AccentGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281E4B)),
                    modifier = Modifier.fillMaxWidth().testTag("close_lang_dialog")
                ) {
                    Text(
                        text = "OK",
                        fontFamily = RighteousFontFamily,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
