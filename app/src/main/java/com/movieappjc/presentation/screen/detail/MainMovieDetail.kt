package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.core_app.repository.Resource
import com.movieappjc.R
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.presentation.screen.detail.cast_crew.CastCrewList
import com.movieappjc.presentation.screen.detail.genres.GenreList

@Composable
fun MainMovieDetail(
    modifier: Modifier = Modifier,
    data: MovieDetailEntity,
    castState: () -> Resource<List<CastEntity>>,
    openTrailerMovie: () -> Unit
) {

    var castList by remember {
        mutableStateOf(listOf<CastEntity>())
    }
    ConstraintLayout(modifier = modifier) {
        val (backdrop, trailer, poster,
            title, review, time,
            overview, genres, cast) = createRefs()
        val startGuideline = createGuidelineFromStart(16.dp)
        val endGuideline = createGuidelineFromEnd(16.dp)
        val castBarrier = createBottomBarrier(overview, genres, time)
        BackdropMovieDetail(
            modifier = Modifier.constrainAs(backdrop) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            backdropPath = data.backdropPath,
            contentDescription = data.title
        )

        Image(
            modifier = Modifier
                .size(50.dp)
                .clickable { openTrailerMovie() }
                .constrainAs(trailer) { centerTo(backdrop) },
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_play),
            contentDescription = ""
        )

        PosterMovieDetail(
            modifier = Modifier.constrainAs(poster) {
                start.linkTo(backdrop.start, margin = 20.dp)
                centerAround(backdrop.bottom)
            },
            posterUrl = data.getPosterUrl(),
            contentDescription = data.title
        )

        TitleMovieDetail(
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(backdrop.bottom)
                    start.linkTo(poster.end)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                },
            title = data.title
        )

        ReviewButton(
            modifier = Modifier
                .constrainAs(review) {
                    top.linkTo(poster.bottom, margin = 30.dp)
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

        if (data.overview.isNotEmpty()) {
            DescriptionMovieDetail(
                modifier = Modifier.constrainAs(overview) {
                    top.linkTo(genres.bottom, margin = 10.dp)
                },
                overview = data.overview
            )
        }

        val casts = castState()
        if (casts is Resource.Success && casts.data.isNotEmpty()) {
            castList = casts.data
            CastCrewList(
                modifier = Modifier.constrainAs(cast) {
                    top.linkTo(castBarrier)
                },
                cast = { castList }
            )
        }
    }
}