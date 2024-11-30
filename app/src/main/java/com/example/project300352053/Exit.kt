package com.example.project300352053

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay

import kotlin.system.exitProcess

class Exit {
}

@Composable
fun exit(){
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // Blinking speed (500ms)
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(){
        Image(painter = painterResource( id = R.drawable.pexels_karolina_grabowska_4475523 ),
            contentDescription = "Background Image",
            modifier = Modifier.graphicsLayer(alpha = alpha).fillMaxHeight(),
           contentScale = ContentScale.Crop )

    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Exiting", fontSize = 40.sp)
    }
    LaunchedEffect(Unit) {
        delay(5000) // Delay before exiting (5 seconds)
        exitProcess(0) // Forcefully exits the app
    }



}