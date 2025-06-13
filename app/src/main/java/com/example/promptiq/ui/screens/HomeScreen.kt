package com.example.promptiq.ui.utils.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.promptiq.R
import com.example.promptiq.ui.theme.roboto

@Composable
fun HomeScreen(
    userName: String,
    onTeleprompterClick: () -> Unit,
    onScriptManagementClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A192F))
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo_hor),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BIENVENIDA
        Text(
            text = "Bienvenido, $userName",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = roboto,
            color = Color(0xFFDFDCCC)
        )

        Spacer(modifier = Modifier.height(32.dp))

        MenuItem("Teleprompter", Icons.Filled.Slideshow, onTeleprompterClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuItem("Gestión de guiones", Icons.Filled.Article, onScriptManagementClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuItem("Ajustes", Icons.Filled.Settings, onSettingsClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuItem("AI Ayuda rápida", Icons.Filled.Help, onHelpClick)

        Spacer(modifier = Modifier.weight(1f))

        MenuItem("Cerrar sesión", Icons.Filled.Logout, onLogoutClick)
    }
}

@Composable
fun MenuItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFF3A5A91),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = Color(0xFFDFDCCC)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = roboto,
                color = Color(0xFFDFDCCC)
            )
        }
    }
}
