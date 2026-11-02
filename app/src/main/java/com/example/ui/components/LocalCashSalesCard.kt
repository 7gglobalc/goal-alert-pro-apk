package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.LicenseManager
import com.example.ui.theme.CardRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@Composable
fun LocalCashSalesCard(
    onRelockRequested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val licenseManager = remember { LicenseManager(context) }
    var isActivated by remember { mutableStateOf(licenseManager.isActivated()) }
    var codeInput by remember { mutableStateOf("") }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isMasterPinRevealed by remember { mutableStateOf(false) }
    var isCurrentDevicePinRevealed by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp))
            .testTag("local_cash_sales_card"),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TrophyGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Cash Sales",
                            tint = TrophyGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cash Sales & Passcode Lock",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Device ID: ${licenseManager.deviceId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricCyan
                        )
                    }
                }

                // License status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActivated) Color(0xFF003918) else Color(0xFF422C00))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActivated) "UNLOCKED" else "LOCKED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isActivated) GoalNeonGreen else TrophyGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activation input box with masked Master Admin PIN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isMasterPinRevealed) "Master Admin PIN: munali0736" else "Master Admin PIN: •••••••••• (Hidden)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoalNeonGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMasterPinRevealed) "Keypad Quick Code: 0736 (or 2026)" else "Keypad Quick Code: •••• (Hidden)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TrophyGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = { isMasterPinRevealed = !isMasterPinRevealed },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isMasterPinRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isMasterPinRevealed) "Hide PIN" else "Reveal PIN",
                                tint = TrophyGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                licenseManager.lockApp()
                                isActivated = false
                                onRelockRequested()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CardRed),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("relock_app_button")
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lock App (Test Lock)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                licenseManager.unlockDirectly()
                                isActivated = true
                                feedbackMessage = "Device unlocked successfully"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color(0xFF003918), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Unlock Pro", color = Color(0xFF003918), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (feedbackMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = feedbackMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = GoalNeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // SELLER CUSTOMER PIN GENERATOR TOOL
            // ==========================================
            var customerDeviceIdInput by remember { mutableStateOf("") }
            var generatedCustomerPin by remember { mutableStateOf<String?>(null) }
            var copySuccess by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, TrophyGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .testTag("customer_pin_generator_card"),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔑", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Customer PIN Generator",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TrophyGold
                            )
                        }

                        // Shows this device's own pin masked by default with reveal toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isCurrentDevicePinRevealed) "Device PIN: ${licenseManager.deviceActivationPin}" else "Device PIN: ••••",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                fontSize = 10.sp
                            )
                            IconButton(
                                onClick = { isCurrentDevicePinRevealed = !isCurrentDevicePinRevealed },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCurrentDevicePinRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isCurrentDevicePinRevealed) "Hide PIN" else "Show PIN",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enter the 6-character Device ID shown on the customer's phone lock screen to generate their unique 4-digit PIN.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customerDeviceIdInput,
                            onValueChange = {
                                customerDeviceIdInput = it.take(6).uppercase()
                                generatedCustomerPin = null
                                copySuccess = false
                            },
                            placeholder = { Text("e.g. A3F9D2", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_device_id_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TrophyGold,
                                unfocusedBorderColor = StadiumBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Button(
                            onClick = {
                                if (customerDeviceIdInput.isNotBlank()) {
                                    val pin = LicenseManager.generateDeviceActivationPin(customerDeviceIdInput)
                                    generatedCustomerPin = pin
                                    copySuccess = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("generate_pin_button")
                        ) {
                            Text(
                                text = "Generate",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Display Generated PIN
                    if (generatedCustomerPin != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF072412))
                                .border(1.dp, GoalNeonGreen, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "CUSTOMER ACTIVATION PIN:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoalNeonGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = generatedCustomerPin!!,
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            letterSpacing = 4.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            val clip = android.content.ClipData.newPlainText("Activation PIN", generatedCustomerPin)
                                            clipboard?.setPrimaryClip(clip)
                                            copySuccess = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (copySuccess) "Copied!" else "Copy PIN",
                                            color = Color(0xFF003918),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🔒 Device-Locked: Only works on Device ID $customerDeviceIdInput. If shared, it will fail on other phones.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step-by-step Local Sales Checklist
            Text(
                text = "HOW TO SELL & INSTALL FOR CASH IN 4 STEPS:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TrophyGold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val steps = listOf(
                "1. Download APK: Use 'Export APK' from AI Studio or copy app-debug.apk from the build folder.",
                "2. Send to Customer: Transfer via WhatsApp, Telegram, USB Cable, or Bluetooth.",
                "3. Enable Sideloading: Guide them to tap 'Allow installation from Unknown Sources' in Android Settings.",
                "4. Demo & Collect Cash: Let them hear the loud stadium roar on the lock screen, collect payment, then enter their unique 4-digit PIN to unlock permanently!"
            )

            steps.forEach { step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "•",
                        color = GoalNeonGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
