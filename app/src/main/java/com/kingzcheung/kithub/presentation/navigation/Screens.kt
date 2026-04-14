package com.kingzcheung.kithub.presentation.navigation

object Screens {
    const val Auth = "auth"
    const val Home = "home"
    const val Search = "search"
    const val Settings = "settings"
    
    fun Repository(owner: String, repo: String) = "repo/$owner/$repo"
    fun Issue(owner: String, repo: String, number: Int) = "issue/$owner/$repo/$number"
    fun PullRequest(owner: String, repo: String, number: Int) = "pr/$owner/$repo/$number"
    fun User(username: String) = "user/$username"
    fun Commit(owner: String, repo: String, sha: String) = "commit/$owner/$repo/$sha"
    fun CodeBrowser(owner: String, repo: String, path: String = "") = "code/$owner/$repo/$path"
}