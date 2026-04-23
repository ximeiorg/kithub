package com.kingzcheung.kithub.data.repository

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RepositoryRepositoryTest {
    
    private lateinit var api: com.kingzcheung.kithub.data.remote.api.GitHubApi
    private lateinit var repository: RepositoryRepository
    
    @Before
    fun setup() {
        api = mockk()
        repository = RepositoryRepository(api)
    }
    
    @Test
    fun `getRepository should return repository`() = runTest {
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
            description = "Test repository",
            htmlUrl = "https://github.com/testuser/testrepo",
            stargazersCount = 100,
            forksCount = 20,
            openIssuesCount = 10,
            language = "Kotlin",
            defaultBranch = "main",
            license = null,
            isPrivate = false
        )
        
        coEvery { api.getRepository("testuser", "testrepo") } returns mockRepoDto
        
        val repo = repository.getRepository("testuser", "testrepo")
        
        assertNotNull(repo)
        assertEquals("testrepo", repo.name)
        assertEquals("testuser/testrepo", repo.fullName)
        assertEquals(100, repo.stargazersCount)
    }
    
    @Test
    fun `checkIfStarred should return true when starred`() = runTest {
        coEvery { api.checkIfRepoIsStarred("testuser", "testrepo") } returns Response.success(Unit)
        
        val isStarred = repository.checkIfStarred("testuser", "testrepo")
        
        assertTrue(isStarred)
    }
    
    @Test
    fun `checkIfStarred should return false when not starred`() = runTest {
        val mockException = mockk<HttpException>()
        coEvery { mockException.code() } returns 404
        coEvery { api.checkIfRepoIsStarred("testuser", "testrepo") } throws mockException
        
        val isStarred = repository.checkIfStarred("testuser", "testrepo")
        
        assertFalse(isStarred)
    }
    
    @Test
    fun `getLanguages should return language map`() = runTest {
        val languageMap = mapOf(
            "Kotlin" to 50000,
            "Java" to 10000,
            "XML" to 5000
        )
        
        coEvery { api.getLanguages("testuser", "testrepo") } returns languageMap
        
        val languages = repository.getLanguages("testuser", "testrepo")
        
        assertNotNull(languages)
        assertEquals(3, languages.size)
        assertEquals(50000, languages["Kotlin"])
    }
    
    @Test
    fun `getBranches should return branch list`() = runTest {
        val mockBranchDto = com.kingzcheung.kithub.data.remote.dto.BranchDto(
            name = "main",
            commit = com.kingzcheung.kithub.data.remote.dto.BranchCommitDto(
                sha = "abc123",
                url = "https://api.github.com/repos/testuser/testrepo/commits/abc123"
            )
        )
        
        coEvery { api.getBranches("testuser", "testrepo") } returns listOf(mockBranchDto)
        
        val branches = repository.getBranches("testuser", "testrepo")
        
        assertNotNull(branches)
        assertEquals(1, branches.size)
        assertEquals("main", branches[0].name)
    }
}