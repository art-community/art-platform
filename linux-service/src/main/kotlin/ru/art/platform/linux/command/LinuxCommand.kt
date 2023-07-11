package ru.art.platform.linux.command

import net.schmizz.sshj.connection.channel.direct.*
import ru.art.core.constants.*
import ru.art.core.constants.NetworkConstants.*
import ru.art.core.constants.StringConstants.*
import ru.art.platform.linux.constants.LinuxConstants.COMMAND_FLAG
import ru.art.platform.linux.constants.LinuxConstants.DEV_NULL
import ru.art.platform.linux.constants.LinuxConstants.ECHO
import ru.art.platform.linux.constants.LinuxConstants.NOHUP
import ru.art.platform.linux.constants.LinuxConstants.NOHUP_ERR
import ru.art.platform.linux.constants.LinuxConstants.NOHUP_OUT
import ru.art.platform.linux.constants.LinuxConstants.SH
import ru.art.platform.linux.constants.LinuxConstants.STDERR
import ru.art.platform.linux.constants.LinuxConstants.STDIN
import ru.art.platform.linux.constants.LinuxConstants.STDOUT
import ru.art.platform.linux.constants.LinuxConstants.SUDO
import ru.art.platform.linux.constants.LinuxConstants.SUDO_SHELL_FLAG

fun linuxCommand(host: String = LOCALHOST, command: String) = LinuxCommandBuilder(host, command)

fun linuxCommand() = LinuxCommandBuilder()

data class LinuxCommandResult(val pid: Long?,
                              val command: LinuxCommand,
                              val logs: List<String>,
                              val output: List<String>,
                              val errors: List<String>,
                              val exitErrorMessage: String?,
                              val exitStatus: Int?,
                              val exitSignal: Signal?)

data class LinuxCommand(val command: String = EMPTY_STRING,
                        val host: String = EMPTY_STRING,
                        val password: String = EMPTY_STRING,
                        val rooted: Boolean = false,
                        val nohup: Boolean = false,
                        val stdoutLogPath: String = EMPTY_STRING,
                        val stderrLogPath: String = EMPTY_STRING,
                        val asynchronous: Boolean = false) {
    override fun toString(): String {
        if (password.isNotBlank() && command.contains(password)) {
            return command.replace(password, EMPTY_STRING.padEnd(password.length, CharConstants.WILDCARD))
        }
        return command
    }
}

class LinuxCommandBuilder(var host: String = LOCALHOST, var command: String = EMPTY_STRING) {
    private var password = EMPTY_STRING
    private var rooted = false
    private var nohup = false
    private var asynchronous = false
    private var stdoutLogPath: String = NOHUP_OUT
    private var stderrLogPath: String = NOHUP_ERR

    fun rooted(password: String): LinuxCommandBuilder {
        this.rooted = true
        this.password = password
        return this
    }

    fun nohup(stdoutLogPath: String = NOHUP_OUT, stderrLogPath: String = NOHUP_ERR): LinuxCommandBuilder {
        this.nohup = true
        this.stderrLogPath = stderrLogPath
        this.stdoutLogPath = stdoutLogPath
        return this
    }

    fun asynchronous(): LinuxCommandBuilder {
        this.asynchronous = true
        return this
    }

    fun addCommand(command: String): LinuxCommandBuilder {
        this.command = "${this.command} $command"
        return this
    }

    fun command(command: String): LinuxCommandBuilder {
        this.command = command
        return this
    }

    fun build(): LinuxCommand {
        if (rooted) {
            if (nohup) {
                if (asynchronous) {
                    val command = "$ECHO $password $PIPE $SUDO $SUDO_SHELL_FLAG $SH $COMMAND_FLAG '$NOHUP $STDIN$DEV_NULL $STDERR$stdoutLogPath $STDERR$stderrLogPath $command $AMPERSAND $ECHO $DOLLAR$EXCLAMATION_MARK'"
                    return LinuxCommand(command = command,
                            host = host,
                            password = password,
                            rooted = rooted,
                            asynchronous = asynchronous,
                            stdoutLogPath = stdoutLogPath,
                            stderrLogPath = stderrLogPath,
                            nohup = nohup)

                }
                val command = "$ECHO $password $PIPE $SUDO $SUDO_SHELL_FLAG $SH $COMMAND_FLAG '$NOHUP $STDIN$DEV_NULL $STDOUT$stdoutLogPath $STDERR$stderrLogPath $command'"
                return LinuxCommand(command,
                        host = host,
                        password = password,
                        rooted = rooted,
                        asynchronous = asynchronous,
                        stdoutLogPath = stdoutLogPath,
                        stderrLogPath = stderrLogPath,
                        nohup = nohup)
            }
            if (asynchronous) {
                val command = "$ECHO $password $PIPE $SUDO $SUDO_SHELL_FLAG $SH $COMMAND_FLAG '$command $AMPERSAND $ECHO $DOLLAR$EXCLAMATION_MARK'"
                return LinuxCommand(command,
                        host = host,
                        password = password,
                        rooted = rooted,
                        asynchronous = asynchronous,
                        stdoutLogPath = stdoutLogPath,
                        stderrLogPath = stderrLogPath,
                        nohup = nohup)
            }
            val command = "$ECHO $password $PIPE $SUDO $SUDO_SHELL_FLAG $SH $COMMAND_FLAG '$command'"
            return LinuxCommand(command,
                    host = host,
                    password = password,
                    rooted = rooted,
                    asynchronous = asynchronous,
                    stdoutLogPath = stdoutLogPath,
                    stderrLogPath = stderrLogPath,
                    nohup = nohup)
        }
        if (nohup) {
            if (asynchronous) {
                val command = "$SH $COMMAND_FLAG '$NOHUP $STDIN$DEV_NULL $STDOUT$stdoutLogPath $STDERR$stderrLogPath $command $AMPERSAND $ECHO $DOLLAR$EXCLAMATION_MARK'"
                return LinuxCommand(command,
                        host = host,
                        password = password,
                        rooted = rooted,
                        asynchronous = asynchronous,
                        stdoutLogPath = stdoutLogPath,
                        stderrLogPath = stderrLogPath,
                        nohup = nohup)
            }
            val command = "$SH $COMMAND_FLAG '$NOHUP $STDIN$DEV_NULL $STDOUT$stdoutLogPath $STDERR$stderrLogPath $command'"
            return LinuxCommand(command,
                    host = host,
                    password = password,
                    rooted = rooted,
                    asynchronous = asynchronous,
                    stdoutLogPath = stdoutLogPath,
                    stderrLogPath = stderrLogPath,
                    nohup = nohup)
        }
        if (asynchronous) {
            val command = "$SH $COMMAND_FLAG '$command $AMPERSAND $ECHO $DOLLAR$EXCLAMATION_MARK'"
            return LinuxCommand(command,
                    host = host,
                    password = password,
                    rooted = rooted,
                    asynchronous = asynchronous,
                    stdoutLogPath = stdoutLogPath,
                    stderrLogPath = stderrLogPath,
                    nohup = nohup)
        }
        return LinuxCommand(command,
                host = host,
                password = password,
                rooted = rooted,
                asynchronous = asynchronous,
                stdoutLogPath = stdoutLogPath,
                stderrLogPath = stderrLogPath,
                nohup = nohup)
    }
}
