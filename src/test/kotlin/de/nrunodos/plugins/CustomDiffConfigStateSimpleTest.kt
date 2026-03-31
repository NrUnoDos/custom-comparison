package de.nrunodos.plugins

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CustomDiffConfigStateSimpleTest {

    private lateinit var state: CustomDiffConfigState

    @Before
    fun setUp() {
        state = CustomDiffConfigState()
    }

    @Test
    fun testInitialState() {
        Assert.assertTrue(state.ignoreWhitespaces)
        Assert.assertTrue(state.ignorePatterns.isEmpty())
        Assert.assertTrue(state.ignoredPsiPaths.isEmpty())
    }

    @Test
    fun testLoadState() {
        val newState = CustomDiffConfigState()
        newState.ignoreWhitespaces = false
        newState.ignorePatterns.add("pattern1")
        newState.ignoredPsiPaths.add("path1")

        state.loadState(newState)

        Assert.assertFalse(state.ignoreWhitespaces)
        Assert.assertEquals(1, state.ignorePatterns.size)
        Assert.assertTrue(state.ignorePatterns.contains("pattern1"))
        Assert.assertEquals(1, state.ignoredPsiPaths.size)
        Assert.assertTrue(state.ignoredPsiPaths.contains("path1"))
    }
}
