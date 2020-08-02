package ru.art.platform.linux.service

import net.schmizz.sshj.*
import net.schmizz.sshj.connection.channel.direct.*
import net.schmizz.sshj.sftp.*
import net.schmizz.sshj.sftp.OpenMode.*
import net.schmizz.sshj.transport.verification.*
import ru.art.core.constants.StringConstants.*
import ru.art.platform.linux.command.*
import ru.art.platform.linux.constants.LinuxConstants.CHMOD_DIRECTORY
import ru.art.platform.linux.constants.LinuxConstants.CHMOD_EXECUTABLE
import ru.art.platform.linux.constants.LinuxConstants.CHMOD_FILE
import ru.art.platform.linux.constants.LinuxConstants.CHOWN_RECURSIVE
import ru.art.platform.linux.constants.LinuxConstants.COPY_RECURSIVE
import ru.art.platform.linux.constants.LinuxConstants.JAVA_JAR
import ru.art.platform.linux.constants.LinuxConstants.KILL
import ru.art.platform.linux.constants.LinuxConstants.MKDIR_WITH_PARENTS
import ru.art.platform.linux.constants.LinuxConstants.MOVE
import ru.art.platform.linux.constants.LinuxConstants.PID_OF
import ru.art.platform.linux.constants.LinuxConstants.REMOVE_FORCE
import ru.art.platform.linux.service.LinuxService.execute
import java.nio.file.*
import java.nio.file.Files.*

object LinuxService {
    fun <T> ssh(host: String, user: String, password: String, executor: SSHClient.() -> T): T {
        SSHClient().use { client ->
            with(client) {
                addHostKeyVerifier(PromiscuousVerifier())
                addAlgorithmsVerifier { true }
                connect(host)
                authPassword(user, password)
                return executor(client)
            }
        }
    }

    fun <T> ssh(host: String, user: String, publicKey: Path, executor: SSHClient.() -> T): T {
        SSHClient().use { client ->
            with(client) {
                addHostKeyVerifier(PromiscuousVerifier())
                addAlgorithmsVerifier { true }
                connect(host)
                authPublickey(user, publicKey.toAbsolutePath().toString())
                return executor(client)
            }
        }
    }

    fun SSHClient.execute(command: LinuxCommandBuilder, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }): LinuxCommandResult =
            session {
                with(commandBuilder(command).build()) {
                    exec(this.command).collectCommandExecutionResult(this)
                }
            }

    fun SSHClient.session(executor: Session.() -> LinuxCommandResult) = startSession().use { session ->
        executor(session)
    }

    fun SSHClient.execute(baseCommand: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute(linuxCommand(socket.inetAddress.hostAddress, baseCommand), commandBuilder)

    fun SSHClient.mkdir(directory: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$MKDIR_WITH_PARENTS $directory", commandBuilder)

    fun SSHClient.copy(file: String, toDirectory: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            arrayOf(mkdir(toDirectory, commandBuilder), execute("$COPY_RECURSIVE $file $toDirectory", commandBuilder))

    fun SSHClient.move(file: String, toDirectory: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            arrayOf(mkdir(toDirectory, commandBuilder), execute("$MOVE $file $toDirectory", commandBuilder))

    fun SSHClient.rename(from: String, to: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$MOVE $from $to", commandBuilder)

    fun SSHClient.delete(file: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$REMOVE_FORCE $file", commandBuilder)

    fun SSHClient.kill(id: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$KILL $id", commandBuilder)

    fun SSHClient.exists(id: String, application: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            id in processes(application, commandBuilder)

    fun SSHClient.processes(application: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$PID_OF $application", commandBuilder).output.firstOrNull()?.split(SPACE) ?: emptyList()


    fun SSHClient.chown(user: String, file: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$CHOWN_RECURSIVE $user:$user $file", commandBuilder)

    fun SSHClient.chmodToFile(file: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$CHMOD_FILE $file", commandBuilder)

    fun SSHClient.chmodToDirectory(file: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$CHMOD_DIRECTORY $file", commandBuilder)

    fun SSHClient.chmodToExecutable(file: String, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute("$CHMOD_EXECUTABLE $file", commandBuilder)


    fun SSHClient.upload(localPath: Path, remotePath: String) = sftp { put(localPath.toAbsolutePath().toString(), remotePath) }

    fun SSHClient.download(remotePath: String, localPath: Path) = sftp { get(remotePath, createDirectories(localPath).toString()) }


    fun SSHClient.readFile(remotePath: String): ByteArray = sftp { open(remotePath, setOf(READ)).RemoteFileInputStream().readBytes() }

    fun SSHClient.readFileText(remotePath: String): String = sftp { open(remotePath, setOf(READ)).RemoteFileInputStream().reader().readText() }

    fun SSHClient.readFileLines(remotePath: String): List<String> = sftp { open(remotePath, setOf(READ)).RemoteFileInputStream().reader().readLines() }


    fun SSHClient.writeFile(remotePath: String, content: String): Unit = sftp { open(remotePath, setOf(CREAT, WRITE, TRUNC)).RemoteFileOutputStream().writer().write(content) }

    fun SSHClient.writeFileText(remotePath: String, content: ByteArray): Unit = sftp { open(remotePath, setOf(CREAT, WRITE, TRUNC)).RemoteFileOutputStream().write(content) }


    fun <T> SSHClient.sftp(executor: SFTPClient.() -> T): T = newSFTPClient().use { client ->
        executor(client)
    }
}

object LinuxJvmService {
    fun SSHClient.runJar(remoteJar: String, options: String = EMPTY_STRING, commandBuilder: LinuxCommandBuilder.() -> LinuxCommandBuilder = { this }) =
            execute(JAVA_JAR) {
                commandBuilder(addCommand("$options $remoteJar"))
            }
}