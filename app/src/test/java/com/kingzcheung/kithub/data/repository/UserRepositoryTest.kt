package com.kingzcheung.kithub.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRepositoryTest {
    
    private lateinit var api: com.kingzcheung.kithub.data.remote.api.GitHubApi
    private lateinit var repository: UserRepository
    
    @Before
    fun setup() {
        api = mockk()
        repository = UserRepository(api)
    }
    
    @Test
    fun `getCurrentUser should return user`() = runTest {
        val mockUserDto = com.kingzcheung.kithub.data.remote.dto.UserDto(
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
        
        coEvery { api.getCurrentUser() } returns mockUserDto
        
        val user = repository.getCurrentUser()
        
        assertNotNull(user)
        assertEquals("testuser", user.login)
        assertEquals("Test User", user.name)
    }
    
    @Test
    fun `getUser should return user`() = runTest {
        val mockUserDto = com.kingzcheung.kithub.data.remote.dto.UserDto(
            id = 2,
            login = "otheruser",
            avatarUrl = "https://example.com/avatar2.png",
            htmlUrl = "https://github.com/otheruser",
            name = "Other User",
            bio = null,
            publicRepos = 5,
            followers = 20,
            following = 10,
            type = "User"
        )
        
        coEvery { api.getUser("otheruser") } returns mockUserDto
        
        val user = repository.getUser("otheruser")
        
        assertNotNull(user)
        assertEquals("otheruser", user.login)
    }
    
    @Test
    fun `getUserRepos should return repository list`() = runTest {
        val mockRepoDto = com.kingzcheung.kithub.data.remote.dto.RepositoryDto(
            id = 100,
            name = "testrepo",
            fullName = "testuser/testrepo",
            owner = com.kingzcheung.kithub.data.remote.dto.UserBriefDto(
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
        
        coEvery { api.getUserRepos("testuser", 1) } returns listOf(mockRepoDto)
        
        val repos = repository.getUserRepos("testuser", 1)
        
        assertNotNull(repos)
        assertEquals(1, repos.size)
        assertEquals("testrepo", repos[0].name)
    }
}