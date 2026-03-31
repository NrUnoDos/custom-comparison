package de.nrunodos.plugins

import com.intellij.diff.DiffContentFactory
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class CustomDiffProviderTest : BasePlatformTestCase() {

    private lateinit var provider: CustomDiffProvider
    private lateinit var config: CustomDiffConfigState

    override fun setUp() {
        super.setUp()
        provider = CustomDiffProvider()
        config = CustomDiffConfigState.getInstance()
        
        // Reset config
        config.ignoreWhitespaces = false
        config.ignorePatterns.clear()
        config.ignoredPsiPaths.clear()
    }

    fun testIgnoreWhitespaces() {
        config.ignoreWhitespaces = true
        val text = " \r\n"
        val content = DiffContentFactory.getInstance().create(text, PlainTextFileType.INSTANCE)
        
        val ranges = provider.getIgnoredRanges(project, text, content)

        Assert.assertTrue("Should have ignored ranges", ranges.isNotEmpty())
    }

    fun testIgnorePatterns() {
        config.ignorePatterns.add("foo")
        val text = "foo bar foo"
        val content = DiffContentFactory.getInstance().create(text, PlainTextFileType.INSTANCE)
        
        val ranges = provider.getIgnoredRanges(project, text, content)

        Assert.assertTrue("Should have some ignored ranges", ranges.isNotEmpty())
    }

    fun testIgnorePsiPaths() {
        val text = "foo bar"
        val content = DiffContentFactory.getInstance().create(text, PlainTextFileType.INSTANCE)
        
        // We use a dummy PSI to get a valid path
        val psiFile = com.intellij.psi.PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, text)
        val element = psiFile.findElementAt(0)!!
        val path = PsiPathUtils.retrievePsiPath(element)
        config.ignoredPsiPaths.add(path)
        
        val ranges = provider.getIgnoredRanges(project, text, content)
        Assert.assertTrue("Should have ignored ranges for PSI path", ranges.isNotEmpty())
    }
}
