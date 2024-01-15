package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.core_app.repository.Resource
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.presentation.screen.detail.cast_crew.CastCrewList
import com.movieappjc.presentation.screen.detail.genres.GenreList
import com.movieappjc.presentation.utils.ComponentUtil
import com.movieappjc.theme.kColorVulcan

@Composable
fun MainMovieDetail(
    modifier: Modifier = Modifier,
    data: MovieDetailEntity,
    castState: () -> Resource<List<CastEntity>>,
    isMovieFavorite: Boolean,
    openTrailerMovie: () -> Unit,
    saveFavoriteMovie: () -> Unit,
    onBack: () -> Unit,
) {

    val gradientColors = remember {
        mutableListOf(
            kColorVulcan.copy(alpha = 0.2f),
            kColorVulcan.copy(alpha = 0.1f),
            kColorVulcan.copy(alpha = 0.0f)
        )
    }
    ConstraintLayout(modifier = modifier) {
        val (appbar, backdrop, poster,
            title , review, time,
            overview, genres, cast) = createRefs()
        val startGuideline = createGuidelineFromStart(16.dp)
        val endGuideline = createGuidelineFromEnd(16.dp)
        BackdropMovieDetail(
            modifier = Modifier.constrainAs(backdrop) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            backdropPath = data.backdropPath,
            contentDescription = data.title
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ScreenUtil.getStatusBarHeight() + 10.dp)
                .background(
                    brush = ComponentUtil.createGradientBrush(gradientColors)
                )
        )

        MovieDetailAppBar(
            modifier = Modifier
                .requiredWidth(
                    ScreenUtil
                        .getScreenHeight()
                        .div(2.3f)
                )
                .constrainAs(appbar) {
                    centerTo(poster)
                }
                .offset{ IntOffset(x = 0, y = 24.dp.roundToPx()) },
            isMovieFavorite = { isMovieFavorite },
            onSaveMovie = { saveFavoriteMovie() },
            onBack = { onBack() }
        )

        PosterMovieDetail(
            modifier = Modifier.constrainAs(poster) {
                centerAround(backdrop.bottom)
                linkTo(startGuideline, endGuideline)
            },
            posterUrl = data.getPosterUrl(),
            contentDescription = data.title
        )

        TitleMovieDetail(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(poster.bottom)
                linkTo(startGuideline, endGuideline)
            },
            title = data.title
        )

        ReviewButton(
            modifier = Modifier
                .constrainAs(review) {
                    top.linkTo(title.bottom, margin = 20.dp)
                    linkTo(startGuideline, endGuideline)
                }
                .height(20.dp),
            voteAverage = data.voteAverage,
        )

        TimeMovieDetail(
            modifier = Modifier.constrainAs(time) {
                top.linkTo(review.bottom, margin = 10.dp)
                linkTo(startGuideline, endGuideline)
            },
            releaseDate = data.releaseDate,
            duration = data.duration,
        )

        GenreList(
            modifier = Modifier.constrainAs(genres) {
                top.linkTo(time.bottom, margin = 5.dp)
            },
            genres = { data.genres }
        )

        DescriptionMovieDetail(
            modifier = Modifier.constrainAs(overview) {
                top.linkTo(genres.bottom, margin = 10.dp)
            },
            overview = data.overview
        )

        val casts = castState()
        if (casts is Resource.Success && casts.data.isNotEmpty()) {
            CastCrewList(
                modifier = Modifier.constrainAs(cast) {
                    top.linkTo(overview.bottom)
                },
                cast = { casts.data }
            )
        }
    }
}