package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.classifier.ModelAvailability
import ai.thanasakis.nothotdog.classifier.ModelChoice
import ai.thanasakis.nothotdog.classifier.ModelChoiceInfo
import ai.thanasakis.nothotdog.ui.theme.HomeBackground
import ai.thanasakis.nothotdog.ui.theme.HotdogGreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val MAX_OUTPUT_TOKEN_OPTIONS = listOf(3, 16, 64, 256)
private val SecondaryText = Color.White.copy(alpha = 0.7f)

@Composable
fun SettingsDialog(
    choices: List<ModelChoiceInfo>?,
    selected: ModelChoice,
    maxOutputTokens: Int,
    onSelectModel: (ModelChoice) -> Unit,
    onSelectMaxOutputTokens: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        SettingsDialogContent(choices, selected, maxOutputTokens, onSelectModel, onSelectMaxOutputTokens, onDismiss)
    }
}

@Composable
internal fun SettingsDialogContent(
    choices: List<ModelChoiceInfo>?,
    selected: ModelChoice,
    maxOutputTokens: Int,
    onSelectModel: (ModelChoice) -> Unit,
    onSelectMaxOutputTokens: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = HomeBackground,
        border = BorderStroke(2.dp, Color.White),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
        ) {
            OutlinedText(
                text = stringResource(R.string.settings),
                fontSize = 32.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            SectionTitle(stringResource(R.string.settings_model))
            if (choices == null) {
                Text(stringResource(R.string.settings_checking_models), color = SecondaryText)
            } else {
                Column {
                    choices.forEach { info ->
                        ModelRow(info, selected = info.choice == selected, onClick = { onSelectModel(info.choice) })
                    }
                }
            }
            SectionTitle(stringResource(R.string.settings_max_output_tokens))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MAX_OUTPUT_TOKEN_OPTIONS.forEach { tokens ->
                    FilterChip(
                        selected = tokens == maxOutputTokens,
                        onClick = { onSelectMaxOutputTokens(tokens) },
                        label = { Text(tokens.toString()) },
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = Color.White,
                            selectedLabelColor = Color.White,
                            selectedContainerColor = HotdogGreen,
                        ),
                    )
                }
            }
            Text(stringResource(R.string.settings_max_output_tokens_hint), color = SecondaryText, fontSize = 13.sp)
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(stringResource(R.string.close), color = Color.White)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontWeight = FontWeight.Bold)
}

@Composable
private fun ModelRow(info: ModelChoiceInfo, selected: Boolean, onClick: () -> Unit) {
    val enabled = info.availability != ModelAvailability.UNAVAILABLE
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = HotdogGreen,
                unselectedColor = Color.White,
                disabledUnselectedColor = Color.White.copy(alpha = 0.3f),
            ),
            modifier = Modifier.padding(12.dp),
        )
        Column {
            Text(modelLabel(info.choice), color = if (enabled) Color.White else SecondaryText)
            Text(modelDetail(info), color = SecondaryText, fontSize = 13.sp)
        }
    }
}

@Composable
private fun modelLabel(choice: ModelChoice) = stringResource(
    when {
        choice.isPreview && choice.isFast -> R.string.model_preview_fast
        choice.isPreview -> R.string.model_preview_full
        choice.isFast -> R.string.model_stable_fast
        else -> R.string.model_stable_full
    },
)

@Composable
private fun modelDetail(info: ModelChoiceInfo): String = when (info.availability) {
    ModelAvailability.INSTALLED -> stringResource(R.string.model_installed, info.name.orEmpty(), info.tokenLimit ?: 0)
    ModelAvailability.DOWNLOADABLE -> stringResource(R.string.model_downloadable)
    ModelAvailability.DOWNLOADING -> stringResource(R.string.model_status_downloading)
    ModelAvailability.UNAVAILABLE -> stringResource(R.string.model_status_unavailable)
}
