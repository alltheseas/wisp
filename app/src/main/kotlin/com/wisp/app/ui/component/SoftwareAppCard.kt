package com.wisp.app.ui.component

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wisp.app.nostr.Nip82
import com.wisp.app.nostr.Nip31
import com.wisp.app.nostr.NostrEvent
import com.wisp.app.nostr.ProfileData

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SoftwareAppCard(
    event: NostrEvent,
    profile: ProfileData?,
    onProfileClick: () -> Unit = {},
    onNoteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val appName = remember(event.id) { Nip82.getAppName(event.tags) }
    val appIcon = remember(event.id) { Nip82.getAppIcon(event.tags) }
    val platformLabels = remember(event.id) { Nip82.getPlatformLabels(event.tags) }
    val license = remember(event.id) { Nip82.getLicense(event.tags) }
    val repository = remember(event.id) { Nip82.getRepository(event.tags) }
    val identifier = remember(event.id) { Nip82.getIdentifier(event.tags) }
    val isAndroid = remember(event.id) { Nip82.isAndroidApp(event.tags) }
    val description = event.content.takeIf { it.isNotBlank() }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val displayName = remember(event.pubkey, profile?.displayString) {
        profile?.displayString
            ?: event.pubkey.take(8) + "..." + event.pubkey.takeLast(4)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNoteClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Publisher info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            ProfilePicture(
                url = profile?.picture,
                size = 24,
                onClick = onProfileClick
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onProfileClick)
            )
        }

        // App icon + name + identifier
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (appIcon != null) {
                AsyncImage(
                    model = appIcon,
                    contentDescription = appName ?: "App icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appName ?: identifier ?: "Unknown App",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (identifier != null && appName != null) {
                    Text(
                        text = identifier,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Description
        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Platform badges + license
        if (platformLabels.isNotEmpty() || license != null) {
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (platform in platformLabels) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = platform,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (license != null) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = license,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Repository link
        if (repository != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = repository
                    .removePrefix("https://")
                    .removePrefix("http://")
                    .trimEnd('/'),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable {
                    try { uriHandler.openUri(repository) } catch (_: Exception) {}
                }
            )
        }

        // Install with Zapstore button (only for Android apps)
        if (isAndroid) {
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    try {
                        val launchIntent = context.packageManager
                            .getLaunchIntentForPackage("dev.zapstore.app")
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                        } else {
                            // Zapstore not installed, open website
                            val browserIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://zapstore.dev")
                            )
                            context.startActivity(browserIntent)
                        }
                    } catch (_: ActivityNotFoundException) {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://zapstore.dev")
                        )
                        try { context.startActivity(browserIntent) } catch (_: Exception) {}
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Install with Zapstore",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (showDivider) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
        }
    }
}

/**
 * Card for kind 30063 (Software Release) events.
 * Shows version info and download URL.
 */
@Composable
fun SoftwareReleaseCard(
    event: NostrEvent,
    profile: ProfileData?,
    onProfileClick: () -> Unit = {},
    onNoteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val identifier = remember(event.id) { Nip82.getIdentifier(event.tags) }
    val urls = remember(event.id) { Nip82.getUrls(event.tags) }
    val alt = remember(event.id) { Nip31.getAlt(event.tags) }
    val uriHandler = LocalUriHandler.current

    val displayName = remember(event.pubkey, profile?.displayString) {
        profile?.displayString
            ?: event.pubkey.take(8) + "..." + event.pubkey.takeLast(4)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNoteClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Publisher info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            ProfilePicture(
                url = profile?.picture,
                size = 24,
                onClick = onProfileClick
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onProfileClick)
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Software Release",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (identifier != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = identifier,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (alt != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = alt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (urls.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = urls.first()
                            .removePrefix("https://")
                            .removePrefix("http://")
                            .trimEnd('/'),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri(urls.first()) } catch (_: Exception) {}
                        }
                    )
                }
            }
        }

        if (showDivider) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
        }
    }
}

/**
 * Card for kind 3063 (Software Asset) events.
 * Shows file info (version, size, hash).
 */
@Composable
fun SoftwareAssetCard(
    event: NostrEvent,
    profile: ProfileData?,
    onProfileClick: () -> Unit = {},
    onNoteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    val version = remember(event.id) { Nip82.getVersion(event.tags) }
    val versionCode = remember(event.id) { Nip82.getVersionCode(event.tags) }
    val fileSize = remember(event.id) { Nip82.getSize(event.tags) }
    val hash = remember(event.id) { Nip82.getHash(event.tags) }
    val mimeType = remember(event.id) { Nip82.getMimeType(event.tags) }
    val platformLabels = remember(event.id) { Nip82.getPlatformLabels(event.tags) }
    val urls = remember(event.id) { Nip82.getUrls(event.tags) }
    val uriHandler = LocalUriHandler.current

    val displayName = remember(event.pubkey, profile?.displayString) {
        profile?.displayString
            ?: event.pubkey.take(8) + "..." + event.pubkey.takeLast(4)
    }

    val formattedSize = remember(fileSize) {
        fileSize?.toLongOrNull()?.let { bytes ->
            when {
                bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
                bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
                bytes >= 1_000 -> String.format("%.1f KB", bytes / 1_000.0)
                else -> "$bytes B"
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNoteClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Publisher info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            ProfilePicture(
                url = profile?.picture,
                size = 24,
                onClick = onProfileClick
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onProfileClick)
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Software Asset",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (event.content.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = event.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Version + size row
                val infoLine = buildString {
                    if (version != null) {
                        append("v$version")
                        if (versionCode != null) append(" ($versionCode)")
                    }
                    if (formattedSize != null) {
                        if (isNotEmpty()) append(" · ")
                        append(formattedSize)
                    }
                    if (mimeType != null) {
                        if (isNotEmpty()) append(" · ")
                        append(mimeType)
                    }
                }
                if (infoLine.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = infoLine,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (hash != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "SHA-256: ${hash.take(16)}...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (platformLabels.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (platform in platformLabels) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = platform,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
                if (urls.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = urls.first()
                            .removePrefix("https://")
                            .removePrefix("http://")
                            .trimEnd('/'),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable {
                            try { uriHandler.openUri(urls.first()) } catch (_: Exception) {}
                        }
                    )
                }
            }
        }

        if (showDivider) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
        }
    }
}
