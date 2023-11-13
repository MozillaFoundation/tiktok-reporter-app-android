package org.mozilla.tiktokreporter.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.mozilla.tiktokreporter.R
import org.mozilla.tiktokreporter.ui.components.SecondaryButton
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography
import org.mozilla.tiktokreporter.ui.theme.TikTokReporterTheme

@Composable
fun ErrorContent(
    modifier: Modifier = Modifier,
    @DrawableRes drawable: Int? = null,
    title: String? = null,
    message: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            drawable?.let {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    painter = painterResource(
                        id = it
                    ),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(MozillaDimension.XXL))
            }
            title?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.H3,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MozillaDimension.L))
            }
            message?.let {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = MozillaTypography.H5,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        action?.let {
            it()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    TikTokReporterTheme {
        ErrorContent(
            drawable = R.drawable.error_cat,
            title = "Ups! Something went wrong", 
            message = "The system doesn’t seem to respond. Our cat must have been chewing the wires again.\n\nPlease forgive Tom and try again later.",
            action = {
                SecondaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Refresh",
                    onClick = {  }
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStudiesListScreenPreview() {
    TikTokReporterTheme {
        ErrorContent(
            modifier = Modifier.fillMaxSize(),
            drawable = R.drawable.meditation,
            title = "No studies available",
            message = "We are sorry to announce that there are no studies available for your location.\n\nCome back at a later date to check if any new study has been opened."
        )
    }
}