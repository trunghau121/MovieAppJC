package com.movieappjc.presentation.screen.person

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.R
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.common.utils.ComponentUtil
import com.movieappjc.app.theme.fontCustomMedium
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.app.theme.kColorVulcan
import com.movieappjc.domain.entities.PersonEntity

@OptIn(ExperimentalMotionApi::class)
@Composable
fun ProfileHeader(
    personEntity: PersonEntity,
    progress: () -> Float,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.header_person_motion_scene)
            .readBytes()
            .decodeToString()
    }

    val gradientColors = remember {
        mutableListOf(
            kColorVulcan.copy(alpha = 0.3f),
            kColorVulcan.copy(alpha = 0.2f),
            kColorVulcan.copy(alpha = 0.1f),
            kColorVulcan.copy(alpha = 0.0f)
        )
    }

    MotionLayout(
        modifier = Modifier.fillMaxWidth(),
        motionScene = MotionScene(content = motionScene),
        progress = progress()
    ) {

        val avatarModifier = remember {
            Modifier.layoutId("profile_avatar")
        }

        AsyncImage(
            modifier = avatarModifier,
            model = ImageRequest.Builder(LocalContext.current)
                .data(personEntity.getProfileUrl())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = personEntity.getName()
        )

        Spacer(modifier = Modifier
            .height(ScreenUtil.getStatusBarHeight())
            .layoutId("status_bar"))

        Box(
            modifier = Modifier
                .background(
                    brush = ComponentUtil.createGradientBrush(gradientColors)
                )
                .layoutId("gradient")
        )

        val modifier = remember {
            Modifier
                .padding(horizontal = 10.aDp)
                .layoutId("icon_back")
                .clickable { onBack() }
        }

        Icon(
            modifier = modifier,
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "",
            tint = Color.White
        )

        Text(
            modifier = Modifier.layoutId("profile_name"),
            text = personEntity.getName(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomSemiBold,
            fontSize = 20.aSp,
            textAlign = TextAlign.Start
        )

        Text(
            modifier = Modifier.layoutId("profile_birthday"),
            text = personEntity.getBirthday(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.aSp,
            textAlign = TextAlign.Start
        )

        Text(
            modifier = Modifier.layoutId("profile_place"),
            text = personEntity.getPlaceOfBirth(),
            color = Color.White,
            style = MaterialTheme.typography.fontCustomMedium,
            fontSize = 16.aSp,
            textAlign = TextAlign.Start
        )
    }
}