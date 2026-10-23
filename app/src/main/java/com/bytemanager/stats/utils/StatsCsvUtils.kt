package com.bytemanager.stats.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.InputStream

object StatsCsvUtils {
    fun createUri(
        appContext: Context,
        fileName: String = "${appContext.packageName.toString().substring(startIndex = 4)}__at__${System.currentTimeMillis()}.csv"
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(
                MediaStore.Downloads.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
        }

        val uri = appContext.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        return uri
    }

    fun exportBatteryStatesToCsv(
        appContext: Context,
        uri: Uri,
        exportList: List<TimestampedRawBatteryState>
    ): Int {
        if(exportList.isEmpty()) {
            return 0
        }

        var appendLineCount = 0
        appContext.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
            exportList.forEach { entry ->
                writer.appendLine(
                    "${escapeCsv(entry.timestamp.toString())},"
                    + "${escapeCsv(entry.level.toString())},"
                    + "${escapeCsv(entry.scale.toString())},"
                    + "${escapeCsv(entry.voltage.toString())},"
                    + "${escapeCsv(entry.temperature.toString())},"
                    + "${escapeCsv(entry.chargingStatus.toString())},"
                    + escapeCsv(entry.batteryHealth.toString())
                )

                appendLineCount ++
            }
        }

        return appendLineCount
    }

    private fun escapeCsv(value: String): String {
        return if (
            value.contains(",") ||
            value.contains("\"") ||
            value.contains("\n")
        ) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }



    suspend fun extractImportList(input: InputStream): List<TimestampedRawBatteryState> =
        withContext(Dispatchers.IO) {
            val items = input.bufferedReader().useLines { lines ->
                lines
                    .filter { line ->
                        line.isNotBlank()
                    }
                    .mapNotNull { line ->
                        val columns = line.split(',')
                        if(columns.size == 7) {
                            val timestamp = columns[0].trim().toLongOrNull()
                            val level = columns[1].trim().toIntOrNull()
                            val scale = columns[2].trim().toIntOrNull()
                            val voltage = columns[3].trim().toIntOrNull()
                            val temperature = columns[4].trim().toIntOrNull()
                            val chargingStatus = columns[5].trim().toIntOrNull()
                            val batteryHealth = columns[6].trim().toIntOrNull()

                            if (timestamp != null) {
                                TimestampedRawBatteryState(
                                    timestamp = timestamp,
                                    level = level,
                                    scale = scale,
                                    voltage = voltage,
                                    temperature = temperature,
                                    chargingStatus = chargingStatus,
                                    batteryHealth = batteryHealth
                                )
                            } else {
                                null
                            }
                        }
                        else {
                            null
                        }
                    }
                    .toList()
            }

            items
        }

    suspend fun exportAll(
        timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
        errorHandler: ErrorHandler,
        appContext: Context,
    ): Int {
        var exitCode = 0
        val uri = createUri(appContext)

        if(uri == null) {
            Log.d("StatsCsvUtils", "Error: uri is null")

            exitCode = -1
            return exitCode
        }

        val resultOfCatchAndReturnExceptionCodeSuspending  = errorHandler.catchAndReturnExceptionCodeSuspending {
            val totalRowsInDatabase = timestampedBatteryStatesDbRepository.dbDao.getCount().first()

            if (totalRowsInDatabase == 0) {
                Log.d("StatsCsvUtils", "Empty database. No export file produced")
                exitCode = 0

            } else {
                val listOfItemsToExport = timestampedBatteryStatesDbRepository.dbDao.getAll().first()

                if(listOfItemsToExport.size != totalRowsInDatabase) {
                    exitCode = -1

                } else {
                    val resultOfExportBatteryStatsToCsv = exportBatteryStatesToCsv(
                        appContext,
                        uri,
                        listOfItemsToExport
                    )

                    when (resultOfExportBatteryStatsToCsv) {
                        totalRowsInDatabase -> {
                            Log.d(
                                "StatsCsvUtils",
                                "All $resultOfExportBatteryStatsToCsv rows exported successfully"
                            )

                            exitCode = 0
                        }

                        in 1 until totalRowsInDatabase -> {
                            Log.w(
                                "StatsCsvUtils",
                                "Warning: only $resultOfExportBatteryStatsToCsv of $totalRowsInDatabase rows exported."
                            )

                            exitCode = 1
                        }

                        0 -> {
                            Log.e("StatsCsvUtils", "Failed to export any rows!")
                            exitCode = -2
                        }

                        else -> {
                            Log.e(
                                "StatsCsvUtils",
                                "Unexpected error: did not expect exported rows count be greater than the total database rows count. exportedRowsCount = $resultOfExportBatteryStatsToCsv, totalRowsInDatabase = $totalRowsInDatabase"
                            )

                            val errorMessage = errorHandler.createErrorMessage(
                                function = "StatsCsvUtils::exportAll",
                                error = Exception("exportResult cannot be greater than totalRowsInDatabase"),
                                message = "result = 0, resultOfExportBatteryStatsToCsv = $resultOfExportBatteryStatsToCsv, totalRowsInDatabase = $totalRowsInDatabase"
                            )

                            errorHandler.logError(errorMessage)

                            exitCode = -3
                        }
                    }
                }
            }
        }

        if(resultOfCatchAndReturnExceptionCodeSuspending != 0) {
            Log.e("StatsCsvUtils", "Export failed! resultOfCatchAndReturnExceptionCodeSuspending = $resultOfCatchAndReturnExceptionCodeSuspending")

            val errorMessage = errorHandler.createErrorMessage(
                function = "StatsCsvUtils::exportAll",
                error = Exception("Export failed! resultOfCatchAndReturnExceptionCodeSuspending = $resultOfCatchAndReturnExceptionCodeSuspending"),
                message = "resultOfCatchAndReturnExceptionCodeSuspending = $resultOfCatchAndReturnExceptionCodeSuspending"
            )

            errorHandler.logError(errorMessage)

            exitCode = -4
        }

        return exitCode
    }
}