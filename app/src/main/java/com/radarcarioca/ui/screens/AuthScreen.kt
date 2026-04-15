package com.radarcarioca.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radarcarioca.ui.AuthUiState
import com.radarcarioca.ui.AuthViewModel
import com.radarcarioca.ui.theme.RadarColors

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetState()
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColors.NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo / Título
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "RADAR CARIOCA",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp
                    ),
                    color = RadarColors.Gold
                )
                Text(
                    "Para motoristas de app",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = RadarColors.TextSecondary
                )
            }

            when (val state = uiState) {
                is AuthUiState.PhoneOtpSent -> {
                    OtpVerificationStep(
                        phoneHint = state.hint,
                        onVerify = { viewModel.verifyPhoneOtp(it) },
                        onBack = { viewModel.resetState() },
                        isLoading = false
                    )
                }
                else -> {
                    MainAuthOptions(
                        isLoading = state is AuthUiState.Loading,
                        errorMessage = (state as? AuthUiState.Error)?.message,
                        onGoogleSignIn = { viewModel.signInWithGoogle(activity) },
                        onPhoneSignIn = { phone -> viewModel.sendPhoneOtp(phone, activity) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainAuthOptions(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var showPhoneField by remember { mutableStateOf(false) }

    // Botão Google
    Button(
        onClick = onGoogleSignIn,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RadarColors.Gold.copy(alpha = 0.15f)),
        border = BorderStroke(1.5.dp, RadarColors.Gold)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = RadarColors.Gold,
                strokeWidth = 2.dp
            )
        } else {
            Text("Entrar com Google", color = RadarColors.Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }

    // Divisor
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = RadarColors.GlassBorder)
        Text("ou", color = RadarColors.TextMuted, fontSize = 12.sp)
        HorizontalDivider(modifier = Modifier.weight(1f), color = RadarColors.GlassBorder)
    }

    // Opção telefone
    if (!showPhoneField) {
        OutlinedButton(
            onClick = { showPhoneField = true },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, RadarColors.GoldDim)
        ) {
            Text("Entrar com Telefone", color = RadarColors.TextSecondary, fontSize = 15.sp)
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Telefone (ex: +5521999999999)", color = RadarColors.TextMuted, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RadarColors.Gold,
                    unfocusedBorderColor = RadarColors.GoldDim,
                    focusedTextColor = RadarColors.TextPrimary,
                    unfocusedTextColor = RadarColors.TextPrimary
                )
            )
            Button(
                onClick = { if (phoneNumber.isNotBlank()) onPhoneSignIn(phoneNumber) },
                enabled = !isLoading && phoneNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RadarColors.NavyMid),
                border = BorderStroke(1.dp, RadarColors.GoldDim)
            ) {
                Text("Enviar Código SMS", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Erro
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = RadarColors.DangerRedGlow,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(RadarColors.DangerRedGlow.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        )
    }
}

@Composable
private fun OtpVerificationStep(
    phoneHint: String,
    onVerify: (String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    var otp by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.GlassWhite, RoundedCornerShape(16.dp))
            .border(1.dp, RadarColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Código enviado para", color = RadarColors.TextSecondary, fontSize = 13.sp)
        Text(phoneHint, color = RadarColors.Gold, fontWeight = FontWeight.Bold, fontSize = 15.sp)

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("Código de 6 dígitos", color = RadarColors.TextMuted, fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RadarColors.Gold,
                unfocusedBorderColor = RadarColors.GoldDim,
                focusedTextColor = RadarColors.TextPrimary,
                unfocusedTextColor = RadarColors.TextPrimary
            )
        )

        Button(
            onClick = { onVerify(otp) },
            enabled = !isLoading && otp.length == 6,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RadarColors.Gold.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, RadarColors.Gold)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = RadarColors.Gold, strokeWidth = 2.dp)
            } else {
                Text("Verificar Código", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
            }
        }

        TextButton(onClick = onBack) {
            Text("← Voltar", color = RadarColors.TextSecondary, fontSize = 12.sp)
        }
    }
}
