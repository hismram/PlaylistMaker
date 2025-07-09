package com.example.playlistmaker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParams: Boolean,
    action: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null

    return {
        params: T ->
        if (useLastParams) {
            debounceJob?.cancel();
        }

        if (debounceJob?.isCompleted != false || useLastParams) {
            debounceJob = coroutineScope.launch {
                delay(delayMillis)
                action(params)
            }
        }
    }
}
