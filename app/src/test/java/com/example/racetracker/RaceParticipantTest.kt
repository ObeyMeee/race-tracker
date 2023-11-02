/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.racetracker

import com.example.racetracker.ui.RaceParticipant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L,
        initialProgress = 0,
        progressIncrement = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RaceStarted_ProgressIncremented() = runTest {
        testRaceParticipant(1, raceParticipant.progressDelayMillis)
    }

    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() {
        val maxProgress = raceParticipant.maxProgress
        testRaceParticipant(maxProgress, raceParticipant.progressDelayMillis * maxProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun testRaceParticipant(expectedProgress: Int, delayTimeMillis: Long) = runTest {
        launch { raceParticipant.run() }
        advanceTimeBy(delayTimeMillis)
        runCurrent()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RacePaused_ProgressUpdated() = runTest {
        val expectedProgress = 5
        val racerJob = launch { raceParticipant.run() }
        advanceTimeBy(expectedProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        racerJob.cancelAndJoin()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_IncorrectMaxProgress_ThrowsException() {
        RaceParticipant(name = "Test", maxProgress = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_IncorrectProgressIncrement_ThrowsException() {
        RaceParticipant(name = "Test", maxProgress = 0)
    }

}
