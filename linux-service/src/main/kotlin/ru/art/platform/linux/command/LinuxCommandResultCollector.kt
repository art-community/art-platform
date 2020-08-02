package ru.art.platform.linux.command

import net.schmizz.sshj.connection.channel.direct.*
import ru.art.core.colorizer.AnsiColorizer.*
import ru.art.core.constants.StringConstants.*
import ru.art.platform.linux.constants.LinuxConstants.SUDO_LOG
import kotlin.streams.*

fun Session.Command.collectCommandExecutionResult(preparedCommand: LinuxCommand): LinuxCommandResult {
    val errors = errorStream.bufferedReader().lines().toList()
    val output = inputStream.bufferedReader().lines().toList()

    join()
    close()

    val outputLogs = mutableListOf("Executing Linux command: $preparedCommand on ${preparedCommand.host}")
            .apply {
                exitSignal?.let { add("Exit signal: $exitSignal") }
                exitStatus?.let { add("Exit status: $exitStatus") }
                exitErrorMessage?.let {
                    if (exitErrorMessage.isNotEmpty()) {
                        add("Exit error message: $exitErrorMessage")
                    }
                }
            }

    val pidAsLong = if (preparedCommand.asynchronous) output
            .map { line -> line.replace(SUDO_LOG, EMPTY_STRING).replace(SPACE, EMPTY_STRING) }
            .firstOrNull { line -> line.isNotBlank() }
            ?.toLongOrNull() else null

    outputLogs += pidAsLong?.let { output.drop(1) } ?: output

    val logs = errors.asSequence()
            .map { line -> line.replace(SUDO_LOG, EMPTY_STRING).trim() }
            .filter { line -> line.isNotBlank() }
            .map { line -> error(line) }
            .toMutableList()

    logs += outputLogs
            .map { line -> line.replace(SUDO_LOG, EMPTY_STRING).trim() }
            .filter { line -> line.isNotBlank() }
            .map { line -> success(line) }

    return LinuxCommandResult(pid = pidAsLong,
            exitErrorMessage = exitErrorMessage,
            exitStatus = exitStatus,
            exitSignal = exitSignal,
            output = output,
            errors = errors,
            logs = logs,
            command = preparedCommand)
}