package ru.art.platform.git.service

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.TRACK
import org.eclipse.jgit.api.Git.cloneRepository
import org.eclipse.jgit.api.Git.open
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffEntry.ChangeType.*
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.TagOpt.FETCH_TAGS
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import ru.art.core.constants.StringConstants.DOT
import ru.art.logging.LoggingModule.loggingModule
import ru.art.platform.api.model.git.GitChanges
import ru.art.platform.api.model.resource.GitResource
import ru.art.platform.git.constants.GitConstants.ADD_REFS_HEADS
import ru.art.platform.git.constants.GitConstants.ADD_REFS_TAGS
import ru.art.platform.git.constants.GitConstants.CURRENT_BRANCH_LAST_COMMIT_REV
import ru.art.platform.git.constants.GitConstants.CURRENT_BRANCH_PREVIOUS_COMMIT_REV
import ru.art.platform.git.constants.GitConstants.DOT_GIT
import ru.art.platform.git.constants.GitConstants.ORIGIN
import ru.art.platform.git.constants.GitConstants.REFS_HEADS
import ru.art.platform.git.constants.GitConstants.REFS_TAGS
import java.io.File
import java.nio.file.Files.createDirectories
import java.nio.file.Path
import java.nio.file.Paths.get
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

fun List<DiffEntry>.changes(): GitChanges {
    val gitChangesBuilder = GitChanges.builder()
    forEach { entry ->
        when (entry.changeType!!) {
            ADD -> gitChangesBuilder.add(entry.newPath)
            MODIFY -> gitChangesBuilder.modify(entry.newPath)
            DELETE -> gitChangesBuilder.delete(entry.oldPath)
            RENAME -> gitChangesBuilder.rename(entry.oldPath, entry.newPath)
            COPY -> gitChangesBuilder.rename(entry.oldPath, entry.newPath)
        }
    }
    return gitChangesBuilder.build()
}

object GitService {
    private val lock = ReentrantLock()
    private val logger = loggingModule().getLogger(GitService::class.java)

    fun cloneProject(gitResource: GitResource, directory: String): Path = lock.withLock {
        val path = get(directory)
        createDirectories(path)
        cloneRepository()
                .setURI(gitResource.url)
                .setDirectory(path.toFile())
                .setRemote(ORIGIN)
                .setCloneAllBranches(true)
                .apply {
                    if (!gitResource.userName.isNullOrBlank() && !gitResource.password.isNullOrBlank()) {
                        setCredentialsProvider(UsernamePasswordCredentialsProvider(gitResource.userName, gitResource.password))
                    }
                }
                .call()
                .close()
        logger.info("Cloning ${gitResource.url} into $directory")
        return path
    }

    fun fetchProject(gitResource: GitResource, directory: String): Path = lock.withLock {
        val path = get(directory)
        val directoryFile = path.toFile()
        if (!directoryFile.exists() && !directoryFile.isDirectory && directoryFile.listFiles()?.any { file -> file.name == DOT_GIT } != true) {
            cloneProject(gitResource, directory)
            return path
        }
        try {
            open(File(directoryFile.absolutePath)).use { repository ->
                with(repository) {
                    fetch()
                            .setRefSpecs(RefSpec(ADD_REFS_HEADS), RefSpec(ADD_REFS_TAGS))
                            .setTagOpt(FETCH_TAGS)
                            .setRemoveDeletedRefs(true)
                            .setForceUpdate(true)
                            .setCredentialsProvider(UsernamePasswordCredentialsProvider(gitResource.userName, gitResource.password))
                            .call()
                    logger.info("Fetching $directory")
                }
            }
        } catch (exception: RepositoryNotFoundException) {
            directoryFile.delete()
            cloneProject(gitResource, directory)
        }
        return path
    }


    fun checkoutLocalReference(directory: String, localReferenceName: String): Path = lock.withLock {
        val path = get(directory)
        open(path.toFile()).use { git ->
            with(git) {
                stashCreate().setIncludeUntracked(true).call()
                logger.info("Stashing $directory")
                checkout()
                        .setName(localReferenceName)
                        .setCreateBranch(!localBranchExists(directory, localReferenceName))
                        .call()
                logger.info("Checkout $directory to $localReferenceName from local")
            }
        }
        return path
    }

    fun fetchLocalReference(gitResource: GitResource, directory: String, localReferenceName: String): Path = lock.withLock {
        val path = fetchProject(gitResource, directory)
        open(path.toFile()).use { git ->
            with(git) {
                stashCreate().setIncludeUntracked(true).call()
                logger.info("Stashing $directory")
                checkout()
                        .setName(localReferenceName)
                        .setCreateBranch(!localBranchExists(directory, localReferenceName))
                        .call()
                logger.info("Checkout $directory to $localReferenceName from local")
            }
        }
        return path
    }

    fun localRefExists(directory: String, tag: String) = lock.withLock {
        open(get(directory).toFile()).use { git ->
            git.branchList()
                    .call()
                    .any { ref -> ref.name == "$REFS_HEADS$tag" }
                    ||
                    open(get(directory).toFile())
                            .tagList()
                            .call()
                            .any { ref -> ref.name == "$REFS_TAGS$tag" }
        }
    }

    fun localBranchExists(directory: String, branch: String) = lock.withLock {
        open(get(directory).toFile()).use { git ->
            git.branchList()
                    .call()
                    .any { ref -> ref.name == "$REFS_HEADS$branch" }
        }
    }

    fun localTagExists(directory: String, tag: String) = lock.withLock {
        open(get(directory).toFile()).use { git ->
            git.branchList()
                    .call()
                    .any { ref -> ref.name == "$REFS_TAGS$tag" }
        }
    }


    fun checkoutRemoteReference(gitResource: GitResource, directory: String, localReferenceName: String): Path = lock.withLock {
        val path = get(directory)
        open(path.toFile()).use { git ->
            with(git) {
                stashCreate().setIncludeUntracked(true).call()
                logger.info("Stashing $directory")
                logger.info("Local reference name $localReferenceName")
                val localBranchExists = localBranchExists(directory, localReferenceName)
                val remoteBranchExists = remoteBranchExists(gitResource, directory, localReferenceName)
                if (!remoteBranchExists) {
                    checkout()
                            .setName(localReferenceName)
                            .setUpstreamMode(TRACK)
                            .setCreateBranch(!localBranchExists)
                            .call()
                    logger.info("Checkout $directory to $localReferenceName")
                    return@use
                }
                checkout()
                        .setName(localReferenceName)
                        .setUpstreamMode(TRACK)
                        .setStartPoint("$ORIGIN/$localReferenceName")
                        .setCreateBranch(!localBranchExists)
                        .call()
                logger.info("Checkout $directory to $localReferenceName from remote $ORIGIN/$localReferenceName")
            }
        }
        return path
    }

    fun fetchRemoteReference(gitResource: GitResource, directory: String, localReferenceName: String): Path = lock.withLock {
        val path = fetchProject(gitResource, directory)
        open(path.toFile()).use { git ->
            with(git) {
                stashCreate().setIncludeUntracked(true).call()
                logger.info("Stashing $directory")
                val localBranchExists = localBranchExists(directory, localReferenceName)
                val remoteBranchExists = remoteBranchExists(gitResource, directory, localReferenceName)
                if (!remoteBranchExists) {
                    checkout()
                            .setName(localReferenceName)
                            .setUpstreamMode(TRACK)
                            .setCreateBranch(!localBranchExists)
                            .call()
                    logger.info("Checkout $directory to $localReferenceName from remote")
                    return@use
                }
                checkout()
                        .setName(localReferenceName)
                        .setUpstreamMode(TRACK)
                        .setStartPoint("$ORIGIN/$localReferenceName")
                        .setCreateBranch(!localBranchExists)
                        .call()
                logger.info("Checkout $directory to $localReferenceName from remote $ORIGIN/$localReferenceName")
            }
        }
        return path
    }

    fun getRemoteReferences(gitResource: GitResource, directory: String) = lock.withLock {
        open(get(directory).toFile()).use { git ->
            git.lsRemote()
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider(gitResource.userName, gitResource.password))
                    .callAsMap()
                    .values
                    .map { ref -> ref.name }
        }
    }

    fun getRemoteBranches(gitResource: GitResource, directory: String) = getRemoteReferences(gitResource, directory)
            .filter { ref -> ref.startsWith(REFS_HEADS) }
            .map { ref -> ref.substringAfter(REFS_HEADS) }
            .toSet()

    fun getRemoteTags(gitResource: GitResource, directory: String) = getRemoteReferences(gitResource, directory)
            .filter { ref -> ref.startsWith(REFS_TAGS) }
            .map { ref -> ref.substringAfter(REFS_TAGS) }
            .toSet()

    fun getRemoteTagsAndBranches(gitResource: GitResource, directory: String) = getRemoteTags(gitResource, directory) + getRemoteBranches(gitResource, directory)

    fun remoteBranchExists(gitResource: GitResource, directory: String, branch: String) = getRemoteBranches(gitResource, directory).contains(branch)

    fun remoteTagExists(gitResource: GitResource, directory: String, tag: String) = getRemoteTags(gitResource, directory).contains(tag)


    fun differenceWithPreviousCommit(gitResource: GitResource, directory: String, reference: String): List<DiffEntry> = lock.withLock {
        checkoutRemoteReference(gitResource, directory, reference)
        open(get(directory).toFile()).use { git ->
            val current: ObjectId? = getLastGitCommitOnBranch(git.repository)
            current ?: return emptyList()
            val previous: ObjectId? = getPreviousGitCommitOnBranch(git.repository)
            previous ?: return emptyList()
            git.repository.newObjectReader().use { reader ->
                val previousTreeParser = CanonicalTreeParser()
                previousTreeParser.reset(reader, previous)
                val currentTreeParser = CanonicalTreeParser()
                currentTreeParser.reset(reader, current)
                return git.diff()
                        .setNewTree(currentTreeParser)
                        .setOldTree(previousTreeParser)
                        .call()

            }
        }
    }

    fun differenceWithPreviousCommit(directory: String): List<DiffEntry> = lock.withLock {
        open(get(directory).toFile()).use { git ->
            val current: ObjectId? = getLastGitCommitOnBranch(git.repository)
            current ?: return emptyList()
            val previous: ObjectId? = getPreviousGitCommitOnBranch(git.repository)
            previous ?: return emptyList()
            git.repository.newObjectReader().use { reader ->
                val previousTreeParser = CanonicalTreeParser()
                previousTreeParser.reset(reader, previous)
                val currentTreeParser = CanonicalTreeParser()
                currentTreeParser.reset(reader, current)
                return git.diff()
                        .setNewTree(currentTreeParser)
                        .setOldTree(previousTreeParser)
                        .call()

            }
        }
    }


    fun difference(directory: String, fromHash: String, toHash: String): List<DiffEntry> = lock.withLock {
        open(get(directory).toFile()).use { git ->
            val from: ObjectId? = git.repository.resolve(fromHash)
            from ?: return emptyList()
            val to: ObjectId? = git.repository.resolve(toHash)
            to ?: return emptyList()
            git.repository.newObjectReader().use { reader ->
                val toTreeParser = CanonicalTreeParser()
                toTreeParser.reset(reader, to)
                val fromTreeParser = CanonicalTreeParser()
                fromTreeParser.reset(reader, from)
                return git.diff()
                        .setNewTree(toTreeParser)
                        .setOldTree(fromTreeParser)
                        .call()

            }
        }
    }


    fun getLastGitCommitOnBranch(repository: Repository): ObjectId = repository.resolve(CURRENT_BRANCH_LAST_COMMIT_REV)

    fun getPreviousGitCommitOnBranch(repository: Repository): ObjectId = repository.resolve(CURRENT_BRANCH_PREVIOUS_COMMIT_REV)


    fun add(directory: String, vararg patterns: String = arrayOf(DOT)) {
        lock.withLock {
            open(get(directory).toFile()).use { git ->
                val addCommand = git.add()
                patterns.forEach { pattern -> addCommand.addFilepattern(pattern) }
                addCommand.call()
                logger.info("Adding files by pattern ${patterns.contentToString()} on $directory")
            }
        }
    }

    fun commit(directory: String, message: String) {
        lock.withLock {
            open(get(directory).toFile()).use { git ->
                git.commit()
                        .setMessage(message)
                        .setNoVerify(true)
                        .call()
                logger.info("Committing on $directory")
            }
        }
    }

    fun push(gitResource: GitResource, directory: String, remoteReference: String) {
        lock.withLock {
            open(get(directory).toFile()).use { git ->
                git.push()
                        .setRemote(ORIGIN)
                        .setCredentialsProvider(UsernamePasswordCredentialsProvider(gitResource.userName, gitResource.password))
                        .setRefSpecs(RefSpec("$REFS_HEADS$remoteReference:$REFS_HEADS$remoteReference"))
                        .call()
                logger.info("Pushing in $directory to $remoteReference")
            }
        }
    }
}
