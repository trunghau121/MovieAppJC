package com.core_app

import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import java.util.*

class ViewHolderJobScheduler {
    private val MAX_JOB_TIME_MS: Float = 4f
    private var elapsed = 0L
    private val jobQueue = ArrayDeque<() -> Unit>()
    private val isOverMaxTime get() = elapsed > MAX_JOB_TIME_MS * 1_000_000
    private val handler = Handler(Looper.getMainLooper())
    private var isDestroyJob = false

    fun submitJob(job: () -> Unit) {
        jobQueue.add(job)
        if (jobQueue.size == 1) {
            handler.post { processJobs() }
        }
    }

    private fun processJobs() {
        while (!isDestroyJob && !jobQueue.isEmpty() && !isOverMaxTime) {
            val start = System.nanoTime()
            jobQueue.poll()?.invoke()
            elapsed += System.nanoTime() - start
        }
        if (isDestroyJob) {
            jobQueue.clear()
        }
        if (jobQueue.isEmpty()) {
            elapsed = 0
        } else if (isOverMaxTime) {
            onNextFrame {
                elapsed = 0
                processJobs()
            }
        }
    }

    private fun onNextFrame(callback: () -> Unit) =
        Choreographer.getInstance().postFrameCallback { callback() }

    fun setDestroyJob(isDestroyJob: Boolean) {
        this.isDestroyJob = isDestroyJob
    }
}