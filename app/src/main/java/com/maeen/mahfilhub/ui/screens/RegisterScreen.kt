package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.theme.*
import com.maeen.mahfilhub.ui.viewmodel.LoginViewModel
import com.maeen.mahfilhub.util.GoogleSignInHelper
import com.maeen.mahfilhub.util.SessionManager
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════════════
// Register Screen — Islamic Premium Design
// ══════════════════════════════════════════════════════════════════════════


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var name by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.loginResponse) {
        uiState.loginResponse?.let { response ->
            SessionManager.login(
                context = context,
                token = response.token,
                role = response.role
            )
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ── Gradient background ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(IslamicDark1, IslamicDark2, IslamicDark3)
                    )
                )
        )

        // ── Islamic decorative canvas ─────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawIslamicBackground()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // ── Crescent + Star icon (smaller for register) ───────────
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCrescentMoonAndStar()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Bismillah ─────────────────────────────────────────────
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontSize = 17.sp,
                color = IslamicGold,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join the Ummah",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Create your account to explore Islamic events",
                fontSize = 13.sp,
                color = SubtleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Name Field ────────────────────────────────────────────
            RegisterIslamicField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.input_enter_name),
                icon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Email or Phone Field ──────────────────────────────────
            RegisterIslamicField(
                label = stringResource(R.string.input_email_or_phone_label),
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                placeholder = stringResource(R.string.input_enter_email_or_phone),
                icon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Password Field ────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SubtleText,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text(stringResource(R.string.input_enter_password), color = MutedText) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = IslamicGold, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Info else Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MutedText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = registerIslamicColors()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Terms Checkbox ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeTerms,
                    onCheckedChange = { agreeTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryTealLight,
                        uncheckedColor = MutedText,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy",
                    fontSize = 12.sp,
                    color = SubtleText,
                    lineHeight = 16.sp,
                    modifier = Modifier.clickable { agreeTerms = !agreeTerms }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Register Button ───────────────────────────────────────
            Button(
                onClick = { onRegisterSuccess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                ),
                contentPadding = PaddingValues(),
                enabled = agreeTerms
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (agreeTerms)
                                Brush.horizontalGradient(listOf(PrimaryTeal, PrimaryTealLight))
                            else
                                Brush.horizontalGradient(
                                    listOf(PrimaryTeal.copy(alpha = 0.3f), PrimaryTealLight.copy(alpha = 0.3f))
                                ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── OR Divider with Islamic star ──────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = IslamicGoldMuted)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawIslamicStar(center, 14f, IslamicGold.copy(alpha = 0.5f))
                    }
                }
                HorizontalDivider(modifier = Modifier.weight(1f), color = IslamicGoldMuted)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Google Sign-up ────────────────────────────────────────
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        GoogleSignInHelper.getIdToken(context)
                            .onSuccess { idToken ->
                                viewModel.googleLogin(idToken)
                            }
                            .onFailure { error ->
                                snackbarHostState.showSnackbar(
                                    message = error.localizedMessage ?: "Google sign-in failed",
                                    duration = SnackbarDuration.Short
                                )
                            }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(listOf(GlassBorder, GlassBorder))
                ),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = GlassBackground)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign up with Google",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Bottom: Login link ────────────────────────────────────
            Row(
                modifier = Modifier.padding(bottom = 24.dp)
                    .clickable{ onNavigateToLogin() }.padding( all = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auth_have_account) + " ",
                    fontSize = 14.sp,
                    color = SubtleText
                )
                Text(
                    text = stringResource(R.string.auth_login),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Reusable field component
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun RegisterIslamicField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SubtleText,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedText) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = registerIslamicColors()
        )
    }
}

@Composable
private fun registerIslamicColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = IslamicGoldLight,
    focusedBorderColor = PrimaryTealLight,
    unfocusedBorderColor = InputBorder,
    focusedContainerColor = InputBackground,
    unfocusedContainerColor = InputBackground,
    focusedLeadingIconColor = IslamicGold,
    unfocusedLeadingIconColor = IslamicGold
)

// ══════════════════════════════════════════════════════════════════════════
// Islamic drawing helpers (private copies for this file)
// ══════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawIslamicBackground() {
    val w = size.width
    val h = size.height

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(IslamicGold.copy(alpha = 0.07f), Color.Transparent),
            center = Offset(w * 0.85f, h * 0.06f), radius = 280f
        ), radius = 280f, center = Offset(w * 0.85f, h * 0.06f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(PrimaryTeal.copy(alpha = 0.08f), Color.Transparent),
            center = Offset(w * 0.08f, h * 0.4f), radius = 220f
        ), radius = 220f, center = Offset(w * 0.08f, h * 0.4f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(IslamicGold.copy(alpha = 0.05f), Color.Transparent),
            center = Offset(w * 0.6f, h * 0.9f), radius = 200f
        ), radius = 200f, center = Offset(w * 0.6f, h * 0.9f)
    )

    val mosqueColor = Color.White.copy(alpha = 0.03f)
    val mosqueY = h * 0.88f
    val domePath = Path().apply {
        moveTo(w * 0.3f, mosqueY)
        quadraticTo(w * 0.5f, mosqueY - 80f, w * 0.7f, mosqueY)
        lineTo(w * 0.7f, h); lineTo(w * 0.3f, h); close()
    }
    drawPath(domePath, mosqueColor)
    val minaretLeft = Path().apply {
        moveTo(w * 0.18f, mosqueY + 10f); lineTo(w * 0.18f, mosqueY - 50f)
        quadraticTo(w * 0.20f, mosqueY - 70f, w * 0.22f, mosqueY - 50f)
        lineTo(w * 0.22f, mosqueY + 10f); lineTo(w * 0.22f, h); lineTo(w * 0.18f, h); close()
    }
    drawPath(minaretLeft, mosqueColor)
    val minaretRight = Path().apply {
        moveTo(w * 0.78f, mosqueY + 10f); lineTo(w * 0.78f, mosqueY - 50f)
        quadraticTo(w * 0.80f, mosqueY - 70f, w * 0.82f, mosqueY - 50f)
        lineTo(w * 0.82f, mosqueY + 10f); lineTo(w * 0.82f, h); lineTo(w * 0.78f, h); close()
    }
    drawPath(minaretRight, mosqueColor)
    drawCircle(IslamicGold.copy(alpha = 0.06f), 6f, Offset(w * 0.5f, mosqueY - 85f))

    val patternColor = IslamicGold.copy(alpha = 0.04f)
    for (i in 0..2) {
        drawArc(patternColor, 0f, 90f, false, Offset(-30f + i * 20f, -30f + i * 20f), Size(80f - i * 20f, 80f - i * 20f), style = Stroke(1f))
    }
    for (i in 0..2) {
        drawArc(patternColor, 90f, 90f, false, Offset(w - 50f + i * 20f, -30f + i * 20f), Size(80f - i * 20f, 80f - i * 20f), style = Stroke(1f))
    }
}

private fun DrawScope.drawCrescentMoonAndStar() {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerR = size.width * 0.38f
    drawCircle(color = IslamicGold, radius = outerR, center = Offset(cx, cy), style = Fill)
    drawCircle(color = IslamicDark1, radius = outerR * 0.78f, center = Offset(cx + outerR * 0.35f, cy - outerR * 0.1f), style = Fill)
    val starCx = cx + outerR * 0.15f
    val starCy = cy - outerR * 0.05f
    drawIslamicStar(Offset(starCx, starCy), outerR * 0.2f, IslamicGold)
}

private fun DrawScope.drawIslamicStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val points = 5
    val angleOffset = -PI / 2.0
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) radius else radius * 0.45f
        val angle = angleOffset + i * PI / points
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    MahfilHubTheme {
        RegisterScreen()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun RegisterScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        RegisterScreen()
    }
}
