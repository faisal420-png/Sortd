package com.sortd.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class DockApp(
    val name: String,
    val packageName: String,
    val icon: ImageVector? = null
)

val defaultDockApps = listOf(
    DockApp("Phone", "com.android.dialer", Icons.Default.Phone),
    DockApp("Messages", "com.android.mms", Icons.Default.Email),
    DockApp("Browser", "com.android.browser", Icons.Default.Language),
    DockApp("Camera", "com.android.camera", Icons.Default.Camera)
)

@Composable
fun BottomDock(
    onAppClick: (String) -> Unit,
    onDrawerOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            defaultDockApps.forEach { dockApp ->
                IconButton(onClick = { onAppClick(dockApp.packageName) }, modifier = Modifier.size(48.dp)) {
                    if (dockApp.icon != null) {
                        Icon(imageVector = dockApp.icon, contentDescription = dockApp.name, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(28.dp))
                    }
                }
            }
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .clickable(onClick = onDrawerOpen),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.GridView, contentDescription = "App Drawer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
    }
}