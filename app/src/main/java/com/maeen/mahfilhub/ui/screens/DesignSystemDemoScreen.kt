package com.maeen.mahfilhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maeen.mahfilhub.R
import com.maeen.mahfilhub.ui.components.*
import com.maeen.mahfilhub.ui.theme.Spacing

/**
 * Design System Demo Screen
 * Showcases all the reusable components in both light and dark themes
 */
@Composable
fun DesignSystemDemoScreen(modifier: Modifier = Modifier) {
    var textFieldValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var searchValue by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.large)
    ) {
        // Header
        Text(
            text = stringResource(R.string.demo_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        HorizontalDivider()
        
        // Colors Section
        SectionTitle(stringResource(R.string.demo_colors))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            ColorBox("Primary", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            ColorBox("Secondary", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
            ColorBox("Tertiary", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
        }
        
        // Buttons Section
        SectionTitle(stringResource(R.string.demo_buttons))
        PrimaryButton(
            text = stringResource(R.string.demo_primary_button),
            onClick = {}
        )
        
        SecondaryButton(
            text = stringResource(R.string.demo_secondary_button),
            onClick = {}
        )
        
        TertiaryButton(
            text = stringResource(R.string.demo_tertiary_button),
            onClick = {}
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            SmallButton(
                text = stringResource(R.string.demo_small_button),
                onClick = {},
                modifier = Modifier.weight(1f)
            )
            SmallButton(
                text = stringResource(R.string.demo_disabled),
                onClick = {},
                enabled = false,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Text Fields Section
        SectionTitle(stringResource(R.string.demo_inputs))
        MahfilTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = stringResource(R.string.input_name),
            placeholder = stringResource(R.string.input_enter_name)
        )
        
        PasswordTextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            label = stringResource(R.string.auth_password),
            placeholder = stringResource(R.string.input_enter_password)
        )
        
        SearchTextField(
            value = searchValue,
            onValueChange = { searchValue = it },
            placeholder = stringResource(R.string.input_search_events),
            leadingIcon = Icons.Default.Search
        )
        
        // Cards Section
        SectionTitle(stringResource(R.string.demo_cards))
        MahfilCard {
            Text(
                text = stringResource(R.string.demo_standard_card),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = stringResource(R.string.demo_standard_card_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        ElevatedMahfilCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.demo_elevated_card),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(Spacing.small))
                    Text(
                        text = stringResource(R.string.demo_elevated_card_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SponsoredBadge()
            }
        }
        
        OutlinedMahfilCard {
            Text(
                text = stringResource(R.string.demo_outlined_card),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = stringResource(R.string.demo_outlined_card_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Badges Section
        SectionTitle(stringResource(R.string.demo_badges))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            VerifiedBadge(size = BadgeSize.Small)
            VerifiedBadge(size = BadgeSize.Medium)
            VerifiedBadge(size = BadgeSize.Large)
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            StatusBadge(text = stringResource(R.string.demo_active))
            StatusBadge(
                text = stringResource(R.string.demo_upcoming),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
            CountBadge(count = 5)
            CountBadge(count = 125)
        }
        
        // States Section
        SectionTitle(stringResource(R.string.demo_states))
        MahfilCard {
            LoadingIndicator(message = stringResource(R.string.status_loading))
        }
        
        DividerWithText(text = stringResource(R.string.auth_or))
        
        // Typography Section
        SectionTitle(stringResource(R.string.demo_typography))
        MahfilCard {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
                Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
                Text("Title Large", style = MaterialTheme.typography.titleLarge)
                Text("Title Medium", style = MaterialTheme.typography.titleMedium)
                Text("Body Large", style = MaterialTheme.typography.bodyLarge)
                Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
                Text("Label Large", style = MaterialTheme.typography.labelLarge)
                Text("Label Small", style = MaterialTheme.typography.labelSmall)
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.large))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun ColorBox(
    name: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(60.dp),
        color = color,
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.small),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
