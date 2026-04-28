package com.kingzcheung.kithub.util

import android.content.Context
import com.kingzcheung.kithub.R

object Strings {
    
    fun getAppName(context: Context): String = context.getString(R.string.app_name)
    
    fun getLoading(context: Context): String = context.getString(R.string.loading)
    
    fun getErrorRetry(context: Context): String = context.getString(R.string.error_retry)
    
    fun getNoResults(context: Context): String = context.getString(R.string.no_results)
    
    fun getHome(context: Context): String = context.getString(R.string.home)
    
    fun getSearch(context: Context): String = context.getString(R.string.search)
    
    fun getNotifications(context: Context): String = context.getString(R.string.notifications)
    
    fun getProfile(context: Context): String = context.getString(R.string.profile)
    
    fun getSettings(context: Context): String = context.getString(R.string.settings)
    
    fun getLogout(context: Context): String = context.getString(R.string.logout)
    
    fun getDarkMode(context: Context): String = context.getString(R.string.dark_mode)
    
    fun getSignInGithub(context: Context): String = context.getString(R.string.sign_in_github)
    
    fun getSignInDevice(context: Context): String = context.getString(R.string.sign_in_device)
    
    fun getStars(context: Context): String = context.getString(R.string.stars)
    
    fun getForks(context: Context): String = context.getString(R.string.forks)
    
    fun getWatchers(context: Context): String = context.getString(R.string.watchers)
    
    fun getIssues(context: Context): String = context.getString(R.string.issues)
    
    fun getPulls(context: Context): String = context.getString(R.string.pulls)
    
    fun getCode(context: Context): String = context.getString(R.string.code)
    
    fun getCommits(context: Context): String = context.getString(R.string.commits)
    
    fun getRepositories(context: Context): String = context.getString(R.string.repositories)
    
    fun getFollowers(context: Context): String = context.getString(R.string.followers)
    
    fun getFollowing(context: Context): String = context.getString(R.string.following)
    
    fun getOpen(context: Context): String = context.getString(R.string.open)
    
    fun getClosed(context: Context): String = context.getString(R.string.closed)
    
    fun getMerged(context: Context): String = context.getString(R.string.merged)
    
    fun getDraft(context: Context): String = context.getString(R.string.draft)
    
    fun getBack(context: Context): String = context.getString(R.string.back)
    
    fun getRetry(context: Context): String = context.getString(R.string.retry)
    
    fun getLanguage(context: Context): String = context.getString(R.string.language)
    
    fun getLicense(context: Context): String = context.getString(R.string.license)
    
    fun getDefaultBranch(context: Context): String = context.getString(R.string.default_branch)
    
    fun getViewCode(context: Context): String = context.getString(R.string.view_code)
    
    fun getGeneral(context: Context): String = context.getString(R.string.general)
    
    fun getTheme(context: Context): String = context.getString(R.string.theme)
    
    fun getThemeColor(context: Context): String = context.getString(R.string.theme_color)
    
    fun getAccount(context: Context): String = context.getString(R.string.account)
    
    fun getGithubAccount(context: Context): String = context.getString(R.string.github_account)
    
    fun getLoggedIn(context: Context): String = context.getString(R.string.logged_in)
    
    fun getAbout(context: Context): String = context.getString(R.string.about)
    
    fun getSourceCode(context: Context): String = context.getString(R.string.source_code)
    
    fun getViewOnGithub(context: Context): String = context.getString(R.string.view_on_github)
    
    fun getReportBug(context: Context): String = context.getString(R.string.report_bug)
    
    fun getSubmitIssue(context: Context): String = context.getString(R.string.submit_issue)
    
    fun getGithubApiDocs(context: Context): String = context.getString(R.string.github_api_docs)
    
    fun getRestApiDocs(context: Context): String = context.getString(R.string.rest_api_docs)
    
    fun getVersion(context: Context, version: String): String = 
        context.getString(R.string.version_format, version)
    
    fun getLogoutConfirmTitle(context: Context): String = context.getString(R.string.logout_confirm_title)
    
    fun getLogoutConfirmMessage(context: Context): String = context.getString(R.string.logout_confirm_message)
    
    fun getCancel(context: Context): String = context.getString(R.string.cancel)
    
    fun getDone(context: Context): String = context.getString(R.string.done)
    
    fun getLight(context: Context): String = context.getString(R.string.light)
    
    fun getDark(context: Context): String = context.getString(R.string.dark)
    
    fun getFollowSystem(context: Context): String = context.getString(R.string.follow_system)
    
    fun getEnglish(context: Context): String = context.getString(R.string.english)
    
    fun getChinese(context: Context): String = context.getString(R.string.chinese)
    
    fun getCodeOptions(context: Context): String = context.getString(R.string.code_options)
    
    fun getCodeOptionsSubtitle(context: Context): String = context.getString(R.string.code_options_subtitle)
    
    fun getWordWrap(context: Context): String = context.getString(R.string.word_wrap)
    
    fun getLineNumbers(context: Context): String = context.getString(R.string.line_numbers)
    
    fun getQuickAccess(context: Context): String = context.getString(R.string.quick_access)
    
    fun getYourRepositories(context: Context): String = context.getString(R.string.your_repositories)
    
    fun getStarred(context: Context): String = context.getString(R.string.starred)
    
    fun getRecentActivity(context: Context): String = context.getString(R.string.recent_activity)
    
    fun getOrganizations(context: Context): String = context.getString(R.string.organizations)
    
    fun getActions(context: Context): String = context.getString(R.string.actions)
    
    fun getContributors(context: Context): String = context.getString(R.string.contributors)
    
    fun getReleases(context: Context): String = context.getString(R.string.releases)
    
    fun getSelectBranch(context: Context): String = context.getString(R.string.select_branch)
    
    fun getClose(context: Context): String = context.getString(R.string.close)
    
    fun getReadme(context: Context): String = context.getString(R.string.readme)
    
    fun getNetworkError(context: Context): String = context.getString(R.string.network_error)
    
    fun getAddIssue(context: Context): String = context.getString(R.string.add_issue)
    
    fun getStar(context: Context): String = context.getString(R.string.star)
    
    fun getShare(context: Context): String = context.getString(R.string.share)
    
    fun getBranch(context: Context): String = context.getString(R.string.branch)
    
    fun getChangeBranch(context: Context): String = context.getString(R.string.change_branch)
    
    fun getSelected(context: Context): String = context.getString(R.string.selected)
    
    fun getPushedTo(context: Context): String = context.getString(R.string.pushed_to)
    
    fun getOpenedPrIn(context: Context): String = context.getString(R.string.opened_pr_in)
    
    fun getOpenedIssueIn(context: Context): String = context.getString(R.string.opened_issue_in)
    
    fun getStarredRepo(context: Context): String = context.getString(R.string.starred_repo)
    
    fun getForked(context: Context): String = context.getString(R.string.forked)
    
    fun getCreated(context: Context): String = context.getString(R.string.created)
    
    fun getDeleted(context: Context): String = context.getString(R.string.deleted)
    
    fun getReleased(context: Context): String = context.getString(R.string.released)
    
    fun getCommentedOn(context: Context): String = context.getString(R.string.commented_on)
    
    fun getCommentedOnCommitIn(context: Context): String = context.getString(R.string.commented_on_commit_in)
    
    fun getReviewedPrIn(context: Context): String = context.getString(R.string.reviewed_pr_in)
    
    fun getCommentedOnPrIn(context: Context): String = context.getString(R.string.commented_on_pr_in)
    
    fun getActedOn(context: Context): String = context.getString(R.string.acted_on)
    
    fun getGithubClient(context: Context): String = context.getString(R.string.github_client)
    
    fun getAuthenticateGithub(context: Context): String = context.getString(R.string.authenticate_github)
    
    fun getOpenUrl(context: Context): String = context.getString(R.string.open_url)
    
    fun getEnterCode(context: Context): String = context.getString(R.string.enter_code)
    
    fun getWaitingAuth(context: Context): String = context.getString(R.string.waiting_auth)
    
    fun getContinueWithoutSignIn(context: Context): String = context.getString(R.string.continue_without_sign_in)
    
    fun getExplore(context: Context): String = context.getString(R.string.explore)
    
    fun getLoadMore(context: Context): String = context.getString(R.string.load_more)
    
    fun getDownload(context: Context): String = context.getString(R.string.download)
    
    fun getFilterIssues(context: Context): String = context.getString(R.string.filter_issues)
    
    fun getFilterPullRequests(context: Context): String = context.getString(R.string.filter_pull_requests)
    
    fun getApply(context: Context): String = context.getString(R.string.apply)
    
    fun getShowAllNotifications(context: Context): String = context.getString(R.string.show_all_notifications)
    
    fun getMarkAllReadTitle(context: Context): String = context.getString(R.string.mark_all_read_title)
    
    fun getMarkAllReadMessage(context: Context): String = context.getString(R.string.mark_all_read_message)
    
    fun getMarkAllRead(context: Context): String = context.getString(R.string.mark_all_read)
    
    fun getRecentlyStarred(context: Context): String = context.getString(R.string.recently_starred)
    
    fun getRecentlyUpdated(context: Context): String = context.getString(R.string.recently_updated)
    
    fun getFullName(context: Context): String = context.getString(R.string.full_name)
    
    fun getPushed(context: Context): String = context.getString(R.string.pushed)
    
    fun getReposFormat(context: Context, count: Int): String = context.getString(R.string.repos_format, count)
    
    fun getAddWorkflowFiles(context: Context): String = context.getString(R.string.add_workflow_files)
    
    fun getWorkflowFormat(context: Context, count: Int): String = context.getString(R.string.workflow_format, count)
    
    fun getRunFormat(context: Context, count: Int): String = context.getString(R.string.run_format, count)
    
    fun getWorkflowRuns(context: Context): String = context.getString(R.string.workflow_runs)
    
    fun getPinnedRepositories(context: Context): String = context.getString(R.string.pinned_repositories)
    
    fun getContributionActivity(context: Context): String = context.getString(R.string.contribution_activity)
    
    fun getUnknown(context: Context): String = context.getString(R.string.unknown)
    
    fun getMadePublic(context: Context, repoName: String): String = context.getString(R.string.made_public, repoName)
    
    fun getActivityIn(context: Context, repoName: String): String = context.getString(R.string.activity_in, repoName)
    
    fun getPublishedRelease(context: Context, repoName: String): String = context.getString(R.string.published_release, repoName)
    
    fun getNoReleasesFound(context: Context): String = context.getString(R.string.no_releases_found)
    
    fun getNoOrganizationsFound(context: Context): String = context.getString(R.string.no_organizations_found)
    
    fun getNoRepositoriesFound(context: Context): String = context.getString(R.string.no_repositories_found)
    
    fun getRepositoryFormat(context: Context, count: Int): String = context.getString(R.string.repository_format, count)
    
    fun getStarredRepositoryFormat(context: Context, count: Int): String = context.getString(R.string.starred_repository_format, count)
    
    fun getOrganizationFormat(context: Context, count: Int): String = context.getString(R.string.organization_format, count)
    
    fun getBio(context: Context): String = context.getString(R.string.bio)
    
    fun getState(context: Context): String = context.getString(R.string.state)
    
    fun getSearchPlaceholder(context: Context): String = context.getString(R.string.search_placeholder)
    
    fun getSearchContents(context: Context): String = context.getString(R.string.search_contents)
    
    fun getSearchForReposUsersIssues(context: Context): String = context.getString(R.string.search_for_repos_users_issues)
    
    fun getRateLimitReached(context: Context): String = context.getString(R.string.rate_limit_reached)
    
    fun getResultsFormat(context: Context, count: Int): String = context.getString(R.string.results_format, count)
    
    fun getShownFormat(context: Context, count: Int): String = context.getString(R.string.shown_format, count)
    
    fun getNoActivity(context: Context): String = context.getString(R.string.no_activity)
    
    fun getFollowSomeUsers(context: Context): String = context.getString(R.string.follow_some_users)
    
    fun getPrivateRepo(context: Context): String = context.getString(R.string.private_repo)
    
    fun getForkRepo(context: Context): String = context.getString(R.string.fork_repo)
    
    fun getForkedTo(context: Context, fullName: String): String = context.getString(R.string.forked_to, fullName)
    
    fun getOrganizationType(context: Context): String = context.getString(R.string.organization_type)
    
    fun getStaff(context: Context): String = context.getString(R.string.staff)
    
    fun getAnonymous(context: Context): String = context.getString(R.string.anonymous)
    
    fun getCommitsCount(context: Context, count: Int): String = context.getString(R.string.commits_count, count)
    
    fun getAssetsFormat(context: Context, count: Int): String = context.getString(R.string.assets_format, count)
    
    fun getPreRelease(context: Context): String = context.getString(R.string.pre_release)
    
    fun getBinaryFile(context: Context): String = context.getString(R.string.binary_file)
    
    fun getBytesFormat(context: Context, bytes: Int): String = context.getString(R.string.bytes_format, bytes)
    
    fun getBranchFormat(context: Context, branch: String): String = context.getString(R.string.branch_format, branch)
    
    fun getCommitsInBranch(context: Context, count: Int): String = context.getString(R.string.commits_in_branch, count)
    
    fun getPushedCommits(context: Context, count: Int): String = context.getString(R.string.pushed_commits, count)
    
    fun getFromBranch(context: Context): String = context.getString(R.string.from_branch)
    
    fun getIntoBranch(context: Context): String = context.getString(R.string.into_branch)
    
    fun getWantsToMerge(context: Context): String = context.getString(R.string.wants_to_merge)
    
    fun getAdditions(context: Context): String = context.getString(R.string.additions)
    
    fun getDeletions(context: Context): String = context.getString(R.string.deletions)
    
    fun getFilesChanged(context: Context): String = context.getString(R.string.files_changed)
    
    fun getTotalChanges(context: Context): String = context.getString(R.string.total_changes)
    
    fun getAuthor(context: Context): String = context.getString(R.string.author)
    
    fun getCommitter(context: Context): String = context.getString(R.string.committer)
    
    fun getChangedFiles(context: Context, count: Int): String = context.getString(R.string.changed_files, count)
    
    fun getDescription(context: Context): String = context.getString(R.string.description)
    
    fun getLabels(context: Context): String = context.getString(R.string.labels)
    
    fun getAssignees(context: Context): String = context.getString(R.string.assignees)
    
    fun getCommentsFormat(context: Context, count: Int): String = context.getString(R.string.comments_format, count)
    
    fun getOpenedThisIssue(context: Context): String = context.getString(R.string.opened_this_issue)
    
    fun getReviewsFormat(context: Context, count: Int): String = context.getString(R.string.reviews_format, count)
    
    fun getRefresh(context: Context): String = context.getString(R.string.refresh)
    
    fun getFilter(context: Context): String = context.getString(R.string.filter)
    
    fun getSort(context: Context): String = context.getString(R.string.sort)
    
    fun getReposTab(context: Context): String = context.getString(R.string.repos_tab)
    
    fun getStarredTab(context: Context): String = context.getString(R.string.starred_tab)
    
    fun getFollowersTab(context: Context): String = context.getString(R.string.followers_tab)
    
    fun getFollowingTab(context: Context): String = context.getString(R.string.following_tab)
    
    fun getLoadingMore(context: Context): String = context.getString(R.string.loading_more)
    
    fun getDraftLabel(context: Context): String = context.getString(R.string.draft_label)
    
    fun getTotalFilter(context: Context): String = context.getString(R.string.total_filter)
    
    fun getCompletedFilter(context: Context): String = context.getString(R.string.completed)
    
    fun getRunningFilter(context: Context): String = context.getString(R.string.running)
    
    fun getQueuedFilter(context: Context): String = context.getString(R.string.queued)
    
    fun getNoUnreadNotifications(context: Context): String = context.getString(R.string.no_unread_notifications)
    
    fun getViewProfile(context: Context): String = context.getString(R.string.view_profile)
    
    fun getNoCommits(context: Context): String = context.getString(R.string.no_commits)
    
    fun getReposCount(context: Context, count: Int): String = context.getString(R.string.repos_count, count)
    
    fun getCreatedRef(context: Context, refType: String): String = context.getString(R.string.created_ref, refType)
    
    fun getDeletedRef(context: Context, refType: String): String = context.getString(R.string.deleted_ref, refType)
    
    fun getMergedPr(context: Context, number: Int): String = context.getString(R.string.merged_pr, number)
    
    fun getClosedPr(context: Context, number: Int): String = context.getString(R.string.closed_pr, number)
    
    fun getOpenedPr(context: Context, number: Int): String = context.getString(R.string.opened_pr, number)
    
    fun getReopenedPr(context: Context, number: Int): String = context.getString(R.string.reopened_pr, number)
    
    fun getUpdatedPr(context: Context, number: Int): String = context.getString(R.string.updated_pr, number)
    
    fun getReviewedPr(context: Context, state: String): String = context.getString(R.string.reviewed_pr, state)
    
    fun getCommentedOnPrReview(context: Context): String = context.getString(R.string.commented_on_pr_review)
    
    fun getPullRequest(context: Context): String = context.getString(R.string.pull_request)
    
    fun getShowUnreadOnly(context: Context): String = context.getString(R.string.show_unread_only)
    
    fun getShowAll(context: Context): String = context.getString(R.string.show_all)
    
    fun getMarkAsRead(context: Context): String = context.getString(R.string.mark_as_read)
    
    fun getMarkAsDone(context: Context): String = context.getString(R.string.mark_as_done)
    
    fun getIncludeAnonymous(context: Context): String = context.getString(R.string.include_anonymous)
}