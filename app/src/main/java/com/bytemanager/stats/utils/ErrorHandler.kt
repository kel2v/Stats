package com.bytemanager.stats.utils

import android.content.Context
import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteDiskIOException
import android.util.Log
import androidx.sqlite.SQLiteException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class ErrorHandler @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    private val logMutex = Mutex()

    fun catchAndReturnExceptionCodeNonSuspending(block: () -> Unit): Int {
        return try {
            block()
            0
        } catch (e: SQLiteConstraintException) {
            Log.e("ErrorHandler", "Constraint violation", e)
            -1
        } catch (e: SQLiteAbortException) {
            Log.e("ErrorHandler", "Abort strategy triggered", e)
            -2
        } catch (e: SQLiteDatabaseLockedException) {
            Log.e("ErrorHandler", "Database locked", e)
            -3
        } catch (e: SQLiteDiskIOException) {
            Log.e("ErrorHandler", "Disk I/O error", e)
            -4
        } catch (e: SQLiteException) {
            Log.e("ErrorHandler", "SQLite error", e)
            -5
        } catch (e: OutOfMemoryError) {
            Log.e("ErrorHandler", "Out of memory", e)
            -6
        } catch (e: StackOverflowError) {
            Log.e("ErrorHandler", "Stack overflow", e)
            -7
        } catch (e: Throwable) {
            Log.e("ErrorHandler", "Unexpected error", e)
            -8
        } catch (e: CancellationException) {
            Log.w("ErrorHandler", "Operation cancelled")
            throw e  // Re-throw cancellation
        }
    }

    suspend fun catchAndReturnExceptionCodeSuspending(block: suspend () -> Unit): Int {
        return try {
            block()
            0
        } catch (e: SQLiteConstraintException) {
            Log.e("ErrorHandler", "Constraint violation", e)
            -1
        } catch (e: SQLiteAbortException) {
            Log.e("ErrorHandler", "Abort strategy triggered", e)
            -2
        } catch (e: SQLiteDatabaseLockedException) {
            Log.e("ErrorHandler", "Database locked", e)
            -3
        } catch (e: SQLiteDiskIOException) {
            Log.e("ErrorHandler", "Disk I/O error", e)
            -4
        } catch (e: SQLiteException) {
            Log.e("ErrorHandler", "SQLite error", e)
            -5
        } catch (e: OutOfMemoryError) {
            Log.e("ErrorHandler", "Out of memory", e)
            -6
        } catch (e: StackOverflowError) {
            Log.e("ErrorHandler", "Stack overflow", e)
            -7
        } catch (e: Throwable) {
            Log.e("ErrorHandler", "Unexpected error", e)
            -8
        } catch (e: CancellationException) {
            Log.w("ErrorHandler", "Operation cancelled")
            throw e  // Re-throw cancellation
        }
    }

    fun createErrorMessage(
        function: String,
        error: Throwable,
        message: String
    ): ErrorMessage {
        return ErrorMessage(
            time = StatsTimeUtils().currentTime(),
            originFunctionName = function,
            errorType = error,
            message = message
        )
    }

    suspend fun logError(errorMessage: ErrorMessage) {
        logMutex.withLock {
            withContext(Dispatchers.IO) {
                File(appContext.filesDir, "error_logs.txt").appendText(
                    "${errorMessage.time}\t"
                            + "${errorMessage.originFunctionName}\t"
                            + "${errorMessage.errorType}\t"
                            + "${errorMessage.message}]\t"
                )
            }
        }
    }
}

data class ErrorMessage(
    val time: LocalTime,
    val errorType: Throwable,
    val originFunctionName: String,
    val message: String
)