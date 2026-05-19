package com.maeen.mahfilhub.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maeen.mahfilhub.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════════════
// Maulana Sign Up Screen — Multi-Step Scholar Registration
// ══════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaulanaSignupScreen(
    modifier: Modifier = Modifier,
    onSignupSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Step tracking ────────────────────────────────────────────────
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 3

    // ── Step 1: Account Info ─────────────────────────────────────────
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ── Step 2: Scholar Profile ──────────────────────────────────────
    var title by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }

    // ── Step 3: Bio & Verification ───────────────────────────────────
    var bio by remember { mutableStateOf("") }
    var referenceContact by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val stepTitles = listOf("Account Info", "Scholar Profile", "Bio & Verification")
    val stepSubtitles = listOf(
        "Create your Maulana account",
        "Tell us about your expertise",
        "Complete your scholar profile"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // ── Background ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(IslamicDark1, IslamicDark2, IslamicDark3)
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawMaulanaSignupBackground()
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
            Spacer(modifier = Modifier.height(12.dp))

            // ── Top Bar: Back + Title ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentStep > 0) currentStep-- else onBack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Step ${currentStep + 1} of $totalSteps",
                    fontSize = 13.sp,
                    color = IslamicGold,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Progress Indicator ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = if (i <= currentStep) PrimaryTealLight
                                else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Crescent icon ────────────────────────────────────────
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawMaulanaCrescent()
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Bismillah ────────────────────────────────────────────
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                fontSize = 16.sp,
                color = IslamicGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stepTitles[currentStep],
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stepSubtitles[currentStep],
                fontSize = 13.sp,
                color = SubtleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Step Content ─────────────────────────────────────────
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "step"
            ) { step ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        0 -> MaulanaStep1(
                            fullName, { fullName = it },
                            email, { email = it },
                            phone, { phone = it },
                            password, { password = it },
                            confirmPassword, { confirmPassword = it },
                            passwordVisible, { passwordVisible = !passwordVisible }
                        )
                        1 -> MaulanaStep2(
                            title, { title = it },
                            specialization, { specialization = it },
                            location, { location = it },
                            experience, { experience = it },
                            qualification, { qualification = it }
                        )
                        2 -> MaulanaStep3(
                            bio, { bio = it },
                            referenceContact, { referenceContact = it },
                            agreeTerms, { agreeTerms = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Action Buttons ───────────────────────────────────────
            if (currentStep < totalSteps - 1) {
                // Next button
                Button(
                    onClick = { currentStep++ },
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
                                Brush.horizontalGradient(listOf(PrimaryTeal, PrimaryTealLight)),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                Icons.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                // Submit button
                Button(
                    onClick = { onSignupSuccess() },
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
                                        listOf(
                                            PrimaryTeal.copy(alpha = 0.3f),
                                            PrimaryTealLight.copy(alpha = 0.3f)
                                        )
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Submit Application",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Bottom: Login link ───────────────────────────────────
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .clickable { onNavigateToLogin() }
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = SubtleText
                )
                Text(
                    text = "Login",
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
// Step 1: Account Info
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaStep1(
    fullName: String, onFullNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean, onTogglePasswordVisibility: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MaulanaField("Full Name", fullName, onFullNameChange, "Enter your full name", Icons.Outlined.Person)
        MaulanaField("Email Address", email, onEmailChange, "Enter your email", Icons.Outlined.Email)
        MaulanaField("Phone Number", phone, onPhoneChange, "+880 1XX-XXXX-XXX", Icons.Outlined.Phone)

        // Password
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Password", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SubtleText,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
            OutlinedTextField(
                value = password, onValueChange = onPasswordChange,
                placeholder = { Text("Create a password", color = MutedText) },
                leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = IslamicGold, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    IconButton(onClick = onTogglePasswordVisibility) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            null, tint = MutedText, modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(14.dp), colors = maulanaFieldColors()
            )
        }

        MaulanaField("Confirm Password", confirmPassword, onConfirmPasswordChange, "Re-enter password", Icons.Outlined.Lock,
            isPassword = true)
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Step 2: Scholar Profile
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaStep2(
    title: String, onTitleChange: (String) -> Unit,
    specialization: String, onSpecializationChange: (String) -> Unit,
    location: String, onLocationChange: (String) -> Unit,
    experience: String, onExperienceChange: (String) -> Unit,
    qualification: String, onQualificationChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section header
        SectionLabel(icon = Icons.Filled.School, text = "Academic & Professional Details")

        MaulanaField("Title / Designation", title, onTitleChange,
            "e.g. Senior Scholar, Hafiz & Qari", Icons.Outlined.Badge)
        MaulanaField("Specialization", specialization, onSpecializationChange,
            "e.g. Tafseer & Hadith, Fiqh", Icons.Outlined.AutoStories)
        MaulanaField("Highest Qualification", qualification, onQualificationChange,
            "e.g. Dawra-e-Hadith, Kamil", Icons.Outlined.WorkspacePremium)
        MaulanaField("Years of Experience", experience, onExperienceChange,
            "e.g. 10", Icons.Outlined.Timer)

        Spacer(modifier = Modifier.height(4.dp))
        SectionLabel(icon = Icons.Filled.LocationOn, text = "Location")

        MaulanaField("City & Country", location, onLocationChange,
            "e.g. Dhaka, Bangladesh", Icons.Outlined.Place)
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Step 3: Bio & Verification
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun MaulanaStep3(
    bio: String, onBioChange: (String) -> Unit,
    referenceContact: String, onReferenceContactChange: (String) -> Unit,
    agreeTerms: Boolean, onAgreeTermsChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionLabel(icon = Icons.Filled.Description, text = "About You")

        // Multi-line bio field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Bio / About", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SubtleText,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
            OutlinedTextField(
                value = bio, onValueChange = onBioChange,
                placeholder = { Text("Tell the community about yourself, your journey and teachings…", color = MutedText) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                maxLines = 6, shape = RoundedCornerShape(14.dp),
                colors = maulanaFieldColors()
            )
            Text(
                text = "${bio.length}/500",
                fontSize = 11.sp, color = MutedText,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp, end = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        SectionLabel(icon = Icons.Filled.VerifiedUser, text = "Verification")

        MaulanaField("Reference Contact", referenceContact, onReferenceContactChange,
            "Phone or email of a known scholar", Icons.Outlined.Contacts)

        // Info card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = PrimaryTeal.copy(alpha = 0.12f),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.linearGradient(listOf(PrimaryTeal.copy(alpha = 0.3f), PrimaryTeal.copy(alpha = 0.3f)))
            )
        ) {
            Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Info, null, tint = PrimaryTealLight, modifier = Modifier.size(18.dp))
                Text(
                    text = "Your profile will be reviewed by our team before activation. Verification usually takes 24–48 hours.",
                    fontSize = 12.sp, color = SubtleText, lineHeight = 17.sp
                )
            }
        }

        // Terms
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = agreeTerms, onCheckedChange = onAgreeTermsChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryTealLight, uncheckedColor = MutedText, checkmarkColor = Color.White
                )
            )
            Text(
                text = "I agree to the Terms of Service, Privacy Policy, and Scholar Guidelines",
                fontSize = 12.sp, color = SubtleText, lineHeight = 16.sp,
                modifier = Modifier.clickable { onAgreeTermsChange(!agreeTerms) }
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════
// Shared Components
// ══════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionLabel(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        Icon(icon, null, tint = IslamicGold, modifier = Modifier.size(18.dp))
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = IslamicGoldLight)
    }
}

@Composable
private fun MaulanaField(
    label: String, value: String, onValueChange: (String) -> Unit,
    placeholder: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SubtleText,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = MutedText) },
            leadingIcon = { Icon(icon, null, tint = IslamicGold, modifier = Modifier.size(20.dp)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(), singleLine = true,
            shape = RoundedCornerShape(14.dp), colors = maulanaFieldColors()
        )
    }
}

@Composable
private fun maulanaFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
    cursorColor = IslamicGoldLight,
    focusedBorderColor = PrimaryTealLight, unfocusedBorderColor = InputBorder,
    focusedContainerColor = InputBackground, unfocusedContainerColor = InputBackground,
    focusedLeadingIconColor = IslamicGold, unfocusedLeadingIconColor = IslamicGold
)

// ══════════════════════════════════════════════════════════════════════════
// Drawing Helpers
// ══════════════════════════════════════════════════════════════════════════

private fun DrawScope.drawMaulanaSignupBackground() {
    val w = size.width; val h = size.height
    drawCircle(Brush.radialGradient(listOf(IslamicGold.copy(alpha = 0.07f), Color.Transparent),
        center = Offset(w * 0.85f, h * 0.06f), radius = 280f), radius = 280f, center = Offset(w * 0.85f, h * 0.06f))
    drawCircle(Brush.radialGradient(listOf(PrimaryTeal.copy(alpha = 0.08f), Color.Transparent),
        center = Offset(w * 0.08f, h * 0.4f), radius = 220f), radius = 220f, center = Offset(w * 0.08f, h * 0.4f))
    drawCircle(Brush.radialGradient(listOf(IslamicGold.copy(alpha = 0.05f), Color.Transparent),
        center = Offset(w * 0.6f, h * 0.9f), radius = 200f), radius = 200f, center = Offset(w * 0.6f, h * 0.9f))

    val mc = Color.White.copy(alpha = 0.03f); val my = h * 0.88f
    val dp = Path().apply { moveTo(w*0.3f,my); quadraticTo(w*0.5f,my-80f,w*0.7f,my); lineTo(w*0.7f,h); lineTo(w*0.3f,h); close() }
    drawPath(dp, mc)
    val ml = Path().apply { moveTo(w*0.18f,my+10f); lineTo(w*0.18f,my-50f); quadraticTo(w*0.20f,my-70f,w*0.22f,my-50f); lineTo(w*0.22f,my+10f); lineTo(w*0.22f,h); lineTo(w*0.18f,h); close() }
    drawPath(ml, mc)
    val mr = Path().apply { moveTo(w*0.78f,my+10f); lineTo(w*0.78f,my-50f); quadraticTo(w*0.80f,my-70f,w*0.82f,my-50f); lineTo(w*0.82f,my+10f); lineTo(w*0.82f,h); lineTo(w*0.78f,h); close() }
    drawPath(mr, mc)
    drawCircle(IslamicGold.copy(alpha = 0.06f), 6f, Offset(w * 0.5f, my - 85f))

    val pc = IslamicGold.copy(alpha = 0.04f)
    for (i in 0..2) { drawArc(pc, 0f, 90f, false, Offset(-30f+i*20f,-30f+i*20f), Size(80f-i*20f,80f-i*20f), style = Stroke(1f)) }
    for (i in 0..2) { drawArc(pc, 90f, 90f, false, Offset(w-50f+i*20f,-30f+i*20f), Size(80f-i*20f,80f-i*20f), style = Stroke(1f)) }
}

private fun DrawScope.drawMaulanaCrescent() {
    val cx = size.width / 2f; val cy = size.height / 2f; val r = size.width * 0.38f
    drawCircle(color = IslamicGold, radius = r, center = Offset(cx, cy), style = Fill)
    drawCircle(color = IslamicDark1, radius = r * 0.78f, center = Offset(cx + r * 0.35f, cy - r * 0.1f), style = Fill)
    val path = Path(); val ao = -PI / 2.0
    for (i in 0 until 10) {
        val sr = if (i % 2 == 0) r * 0.2f else r * 0.09f
        val a = ao + i * PI / 5
        val x = (cx + r * 0.15f) + (sr * cos(a)).toFloat()
        val y = (cy - r * 0.05f) + (sr * sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close(); drawPath(path, IslamicGold)
}

// ══════════════════════════════════════════════════════════════════════════
// Previews
// ══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun MaulanaSignupScreenPreview() {
    MahfilHubTheme { MaulanaSignupScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MaulanaSignupScreenDarkPreview() {
    MahfilHubTheme(darkTheme = true) { MaulanaSignupScreen() }
}
