package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.maeen.mahfilhub.ui.theme.FieldErrorRed
import com.maeen.mahfilhub.ui.theme.GlassBackground
import com.maeen.mahfilhub.ui.theme.GlassBorder
import com.maeen.mahfilhub.ui.theme.InputBackground
import com.maeen.mahfilhub.ui.theme.InputBorder
import com.maeen.mahfilhub.ui.theme.IslamicDark1
import com.maeen.mahfilhub.ui.theme.IslamicDark2
import com.maeen.mahfilhub.ui.theme.IslamicDark3
import com.maeen.mahfilhub.ui.theme.IslamicGold
import com.maeen.mahfilhub.ui.theme.IslamicGoldLight
import com.maeen.mahfilhub.ui.theme.IslamicGoldMuted
import com.maeen.mahfilhub.ui.theme.MahfilHubTheme
import com.maeen.mahfilhub.ui.theme.MutedText
import com.maeen.mahfilhub.ui.theme.PrimaryTeal
import com.maeen.mahfilhub.ui.theme.PrimaryTealLight
import com.maeen.mahfilhub.ui.theme.SubtleText
import com.maeen.mahfilhub.ui.viewmodel.LoginUiState
import com.maeen.mahfilhub.ui.viewmodel.LoginViewModel
import com.maeen.mahfilhub.util.GoogleSignInHelper
import com.maeen.mahfilhub.util.SessionManager
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════════════
// Login Screen — Islamic Premium Design
// ══════════════════════════════════════════════════════════════════════════


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onGuestMode: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Navigate on successful login
    LaunchedEffect(uiState.loginResponse) {
        uiState.loginResponse?.let { response ->
            SessionManager.login(
                context = context,
                token = response.token,
                role = response.role,
                email = email
            )
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    // Show error via Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // Show success via Snackbar
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSuccess()
        }
    }

    // Clear field errors when user starts typing
    LaunchedEffect(email) {
        if (uiState.emailError != null) viewModel.clearFieldErrors()
    }
    LaunchedEffect(password) {
        if (uiState.passwordError != null) viewModel.clearFieldErrors()
    }

    LoginScreenContent(
        modifier = modifier,
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onLoginClick = {
            keyboardController?.hide()
            viewModel.login(email, password)
        },
        onGoogleClick = {
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
        onNavigateToRegister = onNavigateToRegister,
        onGuestMode = onGuestMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenContent(
    modifier: Modifier = Modifier,
    email: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    uiState: LoginUiState = LoginUiState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onPasswordVisibilityToggle: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onGuestMode: () -> Unit = {}
) {

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
            Spacer(modifier = Modifier.height(36.dp))

            // ── Crescent + Star icon ──────────────────────────────────
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCrescentMoonAndStar()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Bismillah ─────────────────────────────────────────────
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontSize = 20.sp,
                color = IslamicGold,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Welcome text ──────────────────────────────────────────
            Text(
                text = "Assalamu Alaikum",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sign in to discover Islamic events near you",
                fontSize = 14.sp,
                color = SubtleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            // ── Email Field ───────────────────────────────────────────
            IslamicInputField(
                label = "Email Address",
                value = email,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.input_enter_email),
                icon = Icons.Outlined.Email,
                isError = uiState.emailError != null
            )
            if (uiState.emailError != null) {
                Text(
                    text = uiState.emailError,
                    color = FieldErrorRed,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    onValueChange = onPasswordChange,
                    placeholder = {
                        Text(stringResource(R.string.input_enter_password), color = MutedText)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = onPasswordVisibilityToggle) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible)
                                    stringResource(R.string.cd_hide_password)
                                else stringResource(R.string.cd_show_password),
                                tint = MutedText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.passwordError != null,
                    shape = RoundedCornerShape(14.dp),
                    colors = islamicFieldColors()
                )
                if (uiState.passwordError != null) {
                    Text(
                        text = uiState.passwordError,
                        color = FieldErrorRed,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            // Forgot Password
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = stringResource(R.string.auth_forgot_password),
                    fontSize = 13.sp,
                    color = IslamicGoldLight,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Login Button ──────────────────────────────────────────
            Button(
                onClick = onLoginClick,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(PrimaryTeal, PrimaryTealLight)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.auth_login),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── OR Divider with Islamic motif ─────────────────────────
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

            Spacer(modifier = Modifier.height(28.dp))

            // ── Social Login ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedButton(
                    onClick = onGoogleClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .weight(1f)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = Brush.linearGradient(listOf(GlassBorder, GlassBorder))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = GlassBackground)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guest Mode
            TextButton(onClick = { onGuestMode() }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.auth_guest_mode),
                    fontSize = 14.sp,
                    color = MutedText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom: Register link ─────────────────────────────────
            Row(
                modifier = Modifier.padding(bottom = 24.dp).clickable { onNavigateToRegister() }.padding( all = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auth_no_account) + " ",
                    fontSize = 14.sp,
                    color = SubtleText
                )
                Text(
                    text = stringResource(R.string.auth_register),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    //modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }

        // Snackbar host – rendered on top of content, at the bottom
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Shared Islamic drawing helpers
// ══════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawIslamicBackground() {
    val w = size.width
    val h = size.height

    // Soft golden glow top-right
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(IslamicGold.copy(alpha = 0.07f), Color.Transparent),
            center = Offset(w * 0.85f, h * 0.06f),
            radius = 280f
        ),
        radius = 280f,
        center = Offset(w * 0.85f, h * 0.06f)
    )

    // Teal glow left
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(PrimaryTeal.copy(alpha = 0.08f), Color.Transparent),
            center = Offset(w * 0.08f, h * 0.4f),
            radius = 220f
        ),
        radius = 220f,
        center = Offset(w * 0.08f, h * 0.4f)
    )

    // Gold glow bottom
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(IslamicGold.copy(alpha = 0.05f), Color.Transparent),
            center = Offset(w * 0.6f, h * 0.9f),
            radius = 200f
        ),
        radius = 200f,
        center = Offset(w * 0.6f, h * 0.9f)
    )

    // ── Mosque silhouette at the bottom ───────────────────────────────
    val mosqueColor = Color.White.copy(alpha = 0.03f)
    val mosqueY = h * 0.88f

    // Central dome
    val domePath = Path().apply {
        moveTo(w * 0.3f, mosqueY)
        quadraticTo(w * 0.5f, mosqueY - 80f, w * 0.7f, mosqueY)
        lineTo(w * 0.7f, h)
        lineTo(w * 0.3f, h)
        close()
    }
    drawPath(domePath, mosqueColor)

    // Left minaret
    val minaretLeft = Path().apply {
        moveTo(w * 0.18f, mosqueY + 10f)
        lineTo(w * 0.18f, mosqueY - 50f)
        // Small dome on minaret
        quadraticTo(w * 0.20f, mosqueY - 70f, w * 0.22f, mosqueY - 50f)
        lineTo(w * 0.22f, mosqueY + 10f)
        lineTo(w * 0.22f, h)
        lineTo(w * 0.18f, h)
        close()
    }
    drawPath(minaretLeft, mosqueColor)

    // Right minaret
    val minaretRight = Path().apply {
        moveTo(w * 0.78f, mosqueY + 10f)
        lineTo(w * 0.78f, mosqueY - 50f)
        quadraticTo(w * 0.80f, mosqueY - 70f, w * 0.82f, mosqueY - 50f)
        lineTo(w * 0.82f, mosqueY + 10f)
        lineTo(w * 0.82f, h)
        lineTo(w * 0.78f, h)
        close()
    }
    drawPath(minaretRight, mosqueColor)

    // Small crescent on top of the dome
    val crescentTop = Offset(w * 0.5f, mosqueY - 85f)
    drawCircle(IslamicGold.copy(alpha = 0.06f), 6f, crescentTop)

    // ── Subtle geometric border patterns (top corners) ────────────────
    val patternColor = IslamicGold.copy(alpha = 0.04f)
    // Top-left corner arcs
    for (i in 0..2) {
        drawArc(
            color = patternColor,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(-30f + i * 20f, -30f + i * 20f),
            size = Size(80f - i * 20f, 80f - i * 20f),
            style = Stroke(width = 1f)
        )
    }
    // Top-right corner arcs
    for (i in 0..2) {
        drawArc(
            color = patternColor,
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(w - 50f + i * 20f, -30f + i * 20f),
            size = Size(80f - i * 20f, 80f - i * 20f),
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawCrescentMoonAndStar() {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerR = size.width * 0.38f

    // Outer circle (moon)
    drawCircle(
        color = IslamicGold,
        radius = outerR,
        center = Offset(cx, cy),
        style = Fill
    )
    // Inner circle to cut crescent
    drawCircle(
        color = IslamicDark1,
        radius = outerR * 0.78f,
        center = Offset(cx + outerR * 0.35f, cy - outerR * 0.1f),
        style = Fill
    )

    // Star next to crescent
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
// Shared Input Field & Colors
// ══════════════════════════════════════════════════════════════════════════

@Composable
internal fun IslamicInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false
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
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors = islamicFieldColors()
        )
    }
}

@Composable
internal fun islamicFieldColors() = OutlinedTextFieldDefaults.colors(
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
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MahfilHubTheme {
        LoginScreenContent()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) {
        LoginScreenContent()
    }
}
