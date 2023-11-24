package org.mozilla.tiktokreporter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MozillaProgressIndicator(
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(MozillaDimension.L))
        Text(
            text = stringResource(R.string.loading_tiktok_reporter),
            style = MozillaTypography.H5
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun LoadingScreenPrev() {
    TikTokReporterTheme {
        LoadingScreen()
    }
}

@Composable
fun MozillaProgressIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MozillaColor.Red,
        strokeWidth = 8.dp,
        trackColor = MozillaColor.Divider,
        strokeCap = StrokeCap.Round
    )
}