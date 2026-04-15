package com.radarcarioca.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.radarcarioca.domain.model.UserProfile
import com.radarcarioca.ui.AdminViewModel
import com.radarcarioca.ui.theme.RadarColors

@Composable
fun AdminPanelScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var emailInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = RadarColors.NavyDeep
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(RadarColors.NavyDeep)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(onClick = onBack) {
                    Text("← Voltar", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
                }
                Text(
                    "PAINEL ADMIN",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                    color = RadarColors.Gold
                )
            }

            HorizontalDivider(color = RadarColors.GlassBorder)

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Adicionar Convidado ────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RadarColors.GlassWhite, RoundedCornerShape(14.dp))
                            .border(1.dp, RadarColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "ADICIONAR CONVIDADO (TESTER)",
                            style = MaterialTheme.typography.labelSmall,
                            color = RadarColors.TextMuted
                        )
                        Text(
                            "O usuário precisa ter feito login ao menos uma vez.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = RadarColors.TextSecondary
                        )
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("E-mail do convidado", color = RadarColors.TextMuted, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RadarColors.Gold,
                                unfocusedBorderColor = RadarColors.GoldDim,
                                focusedTextColor = RadarColors.TextPrimary,
                                unfocusedTextColor = RadarColors.TextPrimary
                            )
                        )
                        Button(
                            onClick = {
                                if (emailInput.isNotBlank()) {
                                    viewModel.grantAccess(emailInput)
                                    emailInput = ""
                                }
                            },
                            enabled = emailInput.isNotBlank() && !uiState.isLoading,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RadarColors.Gold.copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(1.dp, RadarColors.Gold)
                        ) {
                            Text("Conceder Acesso", color = RadarColors.Gold, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // ── Lista de Convidados Ativos ─────────────────────────
                item {
                    Text(
                        "CONVIDADOS ATIVOS (${uiState.testers.size})",
                        style = MaterialTheme.typography.labelSmall,
                        color = RadarColors.TextMuted
                    )
                }

                if (uiState.isLoading && uiState.testers.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = RadarColors.Gold, modifier = Modifier.size(28.dp))
                        }
                    }
                } else if (uiState.testers.isEmpty()) {
                    item {
                        Text(
                            "Nenhum convidado ativo.",
                            color = RadarColors.TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    items(uiState.testers) { tester ->
                        TesterCard(
                            tester = tester,
                            onRevoke = { viewModel.revokeAccess(tester.uid, tester.email) }
                        )
                    }
                }

                item { Spacer(Modifier.height(60.dp)) }
            }
        }
    }
}

@Composable
private fun TesterCard(tester: UserProfile, onRevoke: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(RadarColors.GlassWhite, RoundedCornerShape(10.dp))
            .border(1.dp, RadarColors.GlassBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                tester.displayName.ifBlank { "Sem nome" },
                color = RadarColors.TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(tester.email, color = RadarColors.TextSecondary, fontSize = 11.sp)
            Text(
                "UID: ${tester.uid.take(12)}…",
                color = RadarColors.TextMuted,
                fontSize = 10.sp
            )
        }
        TextButton(
            onClick = onRevoke,
            colors = ButtonDefaults.textButtonColors(contentColor = RadarColors.DangerRedGlow)
        ) {
            Text("Revogar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
