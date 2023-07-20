package com.dionchang.podcasttolearn.util

import com.dionchang.podcasttolearn.error.Failure

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val failure: Failure) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}