package com.kingzcheung.kithub.presentation.viewmodel

import app.cash.turbine.test
import com.kingzcheung.kithub.data.repository.UserRepository
import com.kingzcheung.kithub.data.remote.dto.*
import com.kingzcheung.kithub.util.ErrorNotifier
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    
    private lateinit var userRepository: UserRepository
    private lateinit var errorNotifier: ErrorNotifier
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        errorNotifier = mockk(relaxed = true)
        viewModel = HomeViewModel(userRepository, errorNotifier)
    }
    
    @After
    fun teardown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should have loading false`() = runTest {
        val state = viewModel.state.value
        assertFalse(state.loading)
    }
    
    @Test
    fun `loadHomeData should update state with user data`() = runTest {
        val mockUser = com.kingzcheung.kithub.domain.model.User(
            id = 1,
            login = "testuser",
            avatarUrl = "https://example.com/avatar.png",
            htmlUrl = "https://github.com/testuser",
            name = "Test User",
            bio = "Test bio",
            publicRepos = 10,
            followers = 100,
            following = 50,
            type = "User"
        )
        
        val mockRepo = com.kingzcheung.kithub.domain.model.Repository(
            id = 100,
            name = "testrepo",
            fullName = "testuser/testrepo",
            owner = com.kingzcheung.kithub.domain.model.UserBrief(
                id = 1,
                login = "testuser",
                avatarUrl = "https://example.com/avatar.png",
                type = "User"
            ),
            description = "Test repo",
            htmlUrl = "https://github.com/testuser/testrepo",
            stargazersCount = 50,
            forksCount = 10,
            openIssuesCount = 5,
            language = "Kotlin",
            defaultBranch = "main",
            license = null,
            isPrivate = false
        )
        
        val mockEvent = com.kingzcheung.kithub.domain.model.Event(
            id = "1",
            type = com.kingzcheung.kithub.domain.model.EventType.PushEvent,
            actor = com.kingzcheung.kithub.domain.model.UserBrief(
                id = 1,
                login = "testuser",
                avatarUrl = "https://example.com/avatar.png",
                type = "User"
            ),
            repo = com.kingzcheung.kithub.domain.model.EventRepo(
                id = 100,
                name = "testuser/testrepo",
                url = "https://api.github.com/repos/testuser/testrepo"
            ),
            createdAt = "2024-01-01T00:00:00Z"
        )
        
        val mockOrg = com.kingzcheung.kithub.domain.model.UserBrief(
            id = 2,
            login = "testorg",
            avatarUrl = "https://example.com/org.png",
            type = "Organization"
        )
        
        coEvery { userRepository.getCurrentUser() } returns mockUser
        coEvery { userRepository.getCurrentUserRepos(1) } returns listOf(mockRepo)
        coEvery { userRepository.getStarredRepos("testuser", 1) } returns listOf(mockRepo)
        coEvery { userRepository.getUserEvents("testuser", 1) } returns listOf(mockEvent)
        coEvery { userRepository.getCurrentUserOrgs(1) } returns listOf(mockOrg)
        
        viewModel.state.test {
            val initialState = awaitItem()
            assertFalse(initialState.loading)
            
            viewModel.loadHomeData()
            
            val loadingState = awaitItem()
            assertTrue(loadingState.loading)
            
            val finalState = awaitItem()
            assertFalse(finalState.loading)
            assertEquals("testuser", finalState.user?.login)
            assertTrue(finalState.repos.isNotEmpty())
            assertTrue(finalState.starred.isNotEmpty())
            assertTrue(finalState.events.isNotEmpty())
            assertTrue(finalState.orgs.isNotEmpty())
        }
    }
    
    @Test
    fun `loadHomeData should call errorNotifier on failure`() = runTest {
        coEvery { userRepository.getCurrentUser() } throws Exception("Network error")
        
        viewModel.loadHomeData()
        testDispatcher.scheduler.advanceUntilIdle()
        
        verify { errorNotifier.showError(any(), any()) }
    }
    
    @Test
    fun `refresh should reload home data`() = runTest {
        val mockUser = com.kingzcheung.kithub.domain.model.User(
            id = 1,
            login = "testuser",
            avatarUrl = "https://example.com/avatar.png",
            htmlUrl = "https://github.com/testuser",
            name = "Test User",
            bio = null,
            publicRepos = 0,
            followers = 0,
            following = 0,
            type = "User"
        )
        
        coEvery { userRepository.getCurrentUser() } returns mockUser
        coEvery { userRepository.getCurrentUserRepos(1) } returns emptyList()
        coEvery { userRepository.getStarredRepos("testuser", 1) } returns emptyList()
        coEvery { userRepository.getUserEvents("testuser", 1) } returns emptyList()
        coEvery { userRepository.getCurrentUserOrgs(1) } returns emptyList()
        
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("testuser", viewModel.state.value.user?.login)
    }
}