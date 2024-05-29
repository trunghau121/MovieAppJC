#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_movieappjc_app_common_utils_KeyUtils_apiKey(JNIEnv *env, jobject thiz) {
    std::string api_key = "2472f33b26e67da5bb1456ffa226d78a";

    return env->NewStringUTF(api_key.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_movieappjc_app_common_utils_KeyUtils_baseUrl(JNIEnv *env, jobject thiz) {
    std::string base_url = "https://api.themoviedb.org/3/";

    return env->NewStringUTF(base_url.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_movieappjc_app_common_utils_KeyUtils_baseUrlImage(JNIEnv *env, jobject thiz) {
    std::string base_url_image = "https://image.tmdb.org/t/p/w500";

    return env->NewStringUTF(base_url_image.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_movieappjc_app_common_utils_KeyUtils_baseUrlImage200(JNIEnv *env, jobject thiz) {
    std::string base_url_image_200 = "https://image.tmdb.org/t/p/w200";

    return env->NewStringUTF(base_url_image_200.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_movieappjc_app_common_utils_KeyUtils_urlOriginalImage(JNIEnv *env, jobject thiz) {
    std::string url_original_image = "https://image.tmdb.org/t/p/original";

    return env->NewStringUTF(url_original_image.c_str());
}
