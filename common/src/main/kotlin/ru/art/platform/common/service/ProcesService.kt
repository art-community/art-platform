package ru.art.platform.common.service

import org.apache.logging.log4j.Logger
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.stream.LogOutputStream
import ru.art.core.constants.StringConstants.EMPTY_STRING
import ru.art.logging.LoggingModule.loggingModule
import java.nio.file.Path
import java.nio.file.Paths.get

fun process(command: List<String>, path: Path = get(EMPTY_STRING)) = ProcessRunner(ProcessExecutor().command(command).directory(path.toFile()))

class ProcessRunner(private var executor: ProcessExecutor) {
    var logger: Logger = loggingModule().getLogger(ProcessRunner::class.java)
    var then: MutableList<ProcessRunner> = mutableListOf()
    var onOutput: (String) -> Unit = {}
    var onError: (String) -> Unit = {}

    fun log(logger: Logger): ProcessRunner {
        this.logger = logger

        executor.redirectOutputAlsoTo(object : LogOutputStream() {
            override fun processLine(line: String) {
                logger.info(line)
            }
        })

        executor.redirectErrorAlsoTo(object : LogOutputStream() {
            override fun processLine(line: String) {
                logger.error(line)
            }
        })

        return this
    }

    fun onOutput(consumer: (String) -> Unit): ProcessRunner {
        onOutput = consumer
        executor.redirectOutputAlsoTo(object : LogOutputStream() {
            override fun processLine(line: String) {
                consumer(line)
            }
        })
        return this
    }

    fun onError(consumer: (String) -> Unit): ProcessRunner {
        onError = consumer
        executor.redirectErrorAlsoTo(object : LogOutputStream() {
            override fun processLine(line: String) {
                consumer(line)
            }
        })
        return this
    }

    fun environment(key: String, value: String): ProcessRunner {
        executor.environment(key, value)
        return this
    }

    fun then(runner: ProcessRunner): ProcessRunner {
        then.add(runner)
        return this
    }

    fun then(command: List<String>): ProcessRunner {
        val nextRunner = process(command, executor.directory.toPath())
        nextRunner.onOutput(this.onOutput)
        nextRunner.onError(this.onError)
        then.add(nextRunner)
        return this
    }

    fun run(consumer: (Process) -> Unit = {}) {
        consumer(executor.start().process)
    }

    fun execute(consumer: (Process) -> Unit = {}) {
        val process = executor.start().process
        process.waitFor()
        consumer(process)
    }

    fun execute(): Process {
        val process = executor.start().process
        process.waitFor()
        return process
    }

    fun executeAll(): List<Process> {
        val process = executor.start().process
        process.waitFor()
        return then.map { runner -> runner.executor.start().process.apply { waitFor() } }
    }
}

class ProcessOutputListener(private var eventHandler: (String) -> Unit = { _ -> }, private var errorHandler: (String, Throwable?) -> Unit = { _, _ -> }) {
    fun onEvent(handler: (String) -> Unit = { _ -> }): ProcessOutputListener {
        eventHandler = handler
        return this
    }

    fun onError(handler: (String, Throwable?) -> Unit = { _, _ -> }): ProcessOutputListener {
        errorHandler = handler
        return this
    }

    fun onError(handler: (String) -> Unit = { _ -> }): ProcessOutputListener {
        errorHandler = { message, _ -> handler(message) }
        return this
    }

    fun produceEvent(event: String) = eventHandler(event)

    fun produceError(event: String) = errorHandler(event, null)

    fun produceError(event: String, throwable: Throwable?) = errorHandler(event, throwable)
}