package dev.zornov.repomine.audio

import jakarta.inject.Singleton
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

@Singleton
class SpeechMemoryManager {
    companion object {
        const val MAX_SEGMENTS_PER_PLAYER = 1
        const val RECORD_TIME = 5
        const val SAMPLES_PER_RECORD = RECORD_TIME * 48_000
    }

    val buffers = ConcurrentHashMap<UUID, ConcurrentLinkedDeque<Short>>()

    val completedSegments = ConcurrentHashMap<UUID, ConcurrentLinkedDeque<ShortArray>>()

    fun addSamples(playerId: UUID, samples: ShortArray) {
        val queue = buffers.computeIfAbsent(playerId) { ConcurrentLinkedDeque() }
        synchronized(queue) {
            for (s in samples) {
                queue.addLast(s)
                if (queue.size >= SAMPLES_PER_RECORD) {
                    val segment = ShortArray(SAMPLES_PER_RECORD)
                    for (i in 0 until SAMPLES_PER_RECORD) {
                        segment[i] = queue.removeFirst()
                    }
                    processSegment(playerId, segment)
                }
            }
        }
    }

    fun processSegment(playerId: UUID, segment: ShortArray) {
        val deque = completedSegments.computeIfAbsent(playerId) { ConcurrentLinkedDeque() }
        synchronized(deque) {
            deque.addLast(segment)
            if (deque.size > MAX_SEGMENTS_PER_PLAYER) {
                deque.removeFirst()
            }
        }
    }

    fun getSegments(playerId: UUID): List<ShortArray> {
        val deque = completedSegments[playerId] ?: return emptyList()
        synchronized(deque) {
            return ArrayList(deque)
        }
    }

    fun clearPlayer(playerId: UUID) {
        buffers.remove(playerId)
        completedSegments.remove(playerId)
    }

    fun clearAll() {
        buffers.clear()
        completedSegments.clear()
    }
}