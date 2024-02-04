package com.movieappjc.app.common.constants

import com.movieappjc.app.common.localization.nonTranslatable
import com.movieappjc.app.common.localization.translatable
import com.movieappjc.app.common.localization.registerSupportedLocales
import java.util.Locale

val VIETNAMESE = Locale("vi", "VN")

val supportedLocalesNow = registerSupportedLocales(VIETNAMESE)

val movieText = nonTranslatable(
    "MOVIE",
)

val favoriteMoviesText = translatable(
    "Favorite Movies",
    hashMapOf(
        VIETNAMESE to "Phim yêu thích"
    )
)

val languageText = translatable(
    "Language",
    hashMapOf(
        VIETNAMESE to "Ngôn ngữ",
    )
)

val feedbackText = translatable(
    "Feedback",
    hashMapOf(
        VIETNAMESE to "Nhận xét",
    )
)

val aboutText = translatable(
    "About",
    hashMapOf(
        VIETNAMESE to "Giới thiệu",
    )
)

val popularText = translatable(
    "POPULAR",
    hashMapOf(
        VIETNAMESE to "Phổ biến",
    )
)

val nowText = translatable(
    "NOW",
    hashMapOf(
        VIETNAMESE to "Đang chiếu",
    )
)

val soonText = translatable(
    "SOON",
    hashMapOf(
        VIETNAMESE to "Sắp chiếu",
    )
)

val okayText = translatable(
    "Okay",
    hashMapOf(
        VIETNAMESE to "Đồng ý",
    )
)

val sendText = translatable(
    "Send",
    hashMapOf(
        VIETNAMESE to "Gửi",
    )
)

val cancelText = translatable(
    "Cancel",
    hashMapOf(
        VIETNAMESE to "Hủy",
    )
)

val retryText = translatable(
    "Retry",
    hashMapOf(
        VIETNAMESE to "Thử lại",
    )
)

val aboutDescriptionText = translatable(
    "This product uses the TMDb API but is not endorsed or certified by TMDb. This app is developed for education purpose.",
    hashMapOf(
        VIETNAMESE to "Sản phẩm này sử dụng API TMDb nhưng không được TMDb xác nhận hoặc chứng nhận. Ứng dụng này được phát triển cho mục đích giáo dục.",
    )
)

val castText = translatable(
    "Cast",
    hashMapOf(
        VIETNAMESE to "Diễn viên",
    )
)

val watchTrailersText = translatable(
    "Watch Trailers",
    hashMapOf(
        VIETNAMESE to "Xem giới thiệu",
    )
)

val noTrailerVideoText = translatable(
    "No trailer video",
    hashMapOf(
        VIETNAMESE to "Không có video giới thiệu",
    )
)

val noMoviesSearchedText = translatable(
    "No movies found, please search with another word",
    hashMapOf(
        VIETNAMESE to "Không tìm thấy phim, vui lòng tìm kiếm bằng từ khác",
    )
)

val noFavoriteMovieText = translatable(
    "No Favorite Movie",
    hashMapOf(
        VIETNAMESE to "Không có phim yêu thích",
    )
)

val minutesText = translatable(
    "Minutes",
    hashMapOf(
        VIETNAMESE to "Phút",
    )
)

val showMoreText = translatable(
    "Show more",
    hashMapOf(
        VIETNAMESE to "Xem thêm",
    )
)

val showLessText = translatable(
    "Show less",
    hashMapOf(
        VIETNAMESE to "Ẩn bớt",
    )
)

val enterSearchText = translatable(
    "Enter keyword to search",
    hashMapOf(
        VIETNAMESE to "Nhập từ khóa tìm kiếm",
    )
)

val somethingWentWrongText = translatable(
    "Something went wrong, please try again!",
    hashMapOf(
        VIETNAMESE to "Có lỗi xảy ra, vui lòng thử lại!",
    )
)

val noNetworkText = translatable(
    "No network connection, please try again!",
    hashMapOf(
        VIETNAMESE to "Không có kết nối mạng, vui thử lại!",
    )
)

val titleFeedbackText = translatable(
    "Send us your feedback",
    hashMapOf(
        VIETNAMESE to "Gửi phản hồi cho chúng tôi",
    )
)

val descriptionFeedbackText = translatable(
    "Add a short description of what you encountered",
    hashMapOf(
        VIETNAMESE to "Thêm một mô tả ngắn về những gì bạn gặp phải",
    )
)
