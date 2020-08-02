package ru.art.platform.git.constants

object GitConstants {
    const val REFS_HEADS = "refs/heads/"
    const val REFS_TAGS = "refs/tags/"
    const val ADD_REFS_HEADS = "+refs/heads/*:refs/heads/*"
    const val ADD_REFS_TAGS = "+refs/tags/*:refs/tags/*"
    const val ORIGIN = "origin"
    const val DOT_GIT = ".git"
    const val CURRENT_BRANCH_LAST_COMMIT_REV = "HEAD^{tree}"
    const val CURRENT_BRANCH_PREVIOUS_COMMIT_REV = "HEAD~1^{tree}"
}