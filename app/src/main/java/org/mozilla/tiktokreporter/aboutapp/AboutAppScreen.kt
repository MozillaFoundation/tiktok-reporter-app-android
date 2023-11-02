package org.mozilla.tiktokreporter.aboutapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mozilla.tiktokreporter.ui.components.MozillaScaffold
import org.mozilla.tiktokreporter.ui.components.MozillaTopAppBar
import org.mozilla.tiktokreporter.ui.theme.MozillaColor
import org.mozilla.tiktokreporter.ui.theme.MozillaDimension
import org.mozilla.tiktokreporter.ui.theme.MozillaTypography


@Composable
fun AboutAppScreen(
    onNavigateBack: () -> Unit
) {

    MozillaScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MozillaTopAppBar(
                modifier = Modifier.fillMaxWidth(),
                action = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "",
                            tint = MozillaColor.TextColor
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = MozillaDimension.M,
                vertical = MozillaDimension.L
            ),
            verticalArrangement = Arrangement.spacedBy(MozillaDimension.L),
            content = {
                item {
                    Text(
                        text = "About TikTok Reporter",
                        style = MozillaTypography.H3
                    )
                }

                item {
                    Text(
                        text = "“TikTok Reporter” serves as a vital tool in an ongoing sociological study, seeking to understand the broader implications of content shared on TikTok. By reporting harmful videos, users contribute to a wealth of data that will be instrumental in identifying and addressing the social issues, influences, and trends within the platform.\n\nJoin us in shaping the future of digital interaction by participating in this sociological study through “TikTok Reporter” Your reports make a difference in the quest to foster a safer, more informed, and socially conscious digital environment.\n\nAnonymous Reporting: SocialSafeguard ensures users can report troubling TikTok content discreetly, preserving their privacy.\n\nCategorization: Users can categorize the type of harm observed in videos, such as bullying, misinformation, hate speech, or other sociological factors.\n\nCommentary: The app allows users to provide context and insights, fostering a deeper understanding of the content’s impact.\n\nData Collection: Reports are collated into a comprehensive database, allowing sociologists to analyze and identify trends and patterns.",
                        style = MozillaTypography.Body2
                    )
                }
            }
        )
    }
}