package de.nrunodos.plugins

import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class IgnoredPsiVisitorTest : BasePlatformTestCase() {

    private lateinit var config: CustomDiffConfigState
    private lateinit var visitor: IgnoredPsiVisitor

    override fun setUp() {
        super.setUp()
        config = CustomDiffConfigState()
        // Initialize with defaults to match tests
        config.ignoreWhitespaces = false
        config.ignorePatterns.clear()
        config.ignoredPsiPaths.clear()
        
        visitor = IgnoredPsiVisitor(config)
    }

    fun testVisitWhiteSpaceIgnored() {
        config.ignoreWhitespaces = true
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "   ")
        val whiteSpace = psiFile.findElementAt(0)!!

        visitor.visitElement(whiteSpace)

        Assert.assertEquals(1, visitor.textRanges.size)
        Assert.assertEquals(whiteSpace.textRange, visitor.textRanges[0])
    }

    fun testVisitWhiteSpaceNotIgnored() {
        config.ignoreWhitespaces = false
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "   ")
        val whiteSpace = psiFile.findElementAt(0)!!

        visitor.visitElement(whiteSpace)

        Assert.assertTrue(visitor.textRanges.isEmpty())
    }

    fun testVisitPatternMatch() {
        config.ignorePatterns.add("foo.*")
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "foobar")
        val element = psiFile.firstChild // This should be the TEXT element in PlainText

        visitor.visitElement(element)

        Assert.assertEquals(1, visitor.textRanges.size)
        Assert.assertEquals(element.textRange, visitor.textRanges[0])
    }

    fun testVisitPsiPathMatch() {
        // In PlainText, the structure is usually FILE -> TEXT (leaf)
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "content")
        val element = psiFile.findElementAt(0)!!

        val path = PsiPathUtils.retrievePsiPath(element)
        config.ignoredPsiPaths.add(path)

        visitor.visitElement(element)

        Assert.assertEquals(1, visitor.textRanges.size)
        Assert.assertEquals(element.textRange, visitor.textRanges[0])
    }

    fun testVisitPsiPathMatchRecursive() {
        // Test that if a parent is ignored, children are also ignored or the parent's range is added
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "content")
        val element = psiFile.findElementAt(0)!!
        val parent = element.parent

        val path = PsiPathUtils.retrievePsiPath(parent)
        config.ignoredPsiPaths.add(path)

        println("[DEBUG_LOG] Parent path: $path")
        println("[DEBUG_LOG] File path: ${PsiPathUtils.retrievePsiPath(psiFile)}")

        visitor.visitElement(psiFile)

        Assert.assertTrue("Visitor should have ignored ranges", visitor.textRanges.isNotEmpty())
        Assert.assertTrue("Should contain parent's range", visitor.textRanges.contains(parent.textRange))
    }

    fun testVisitPsiPathMatchToString() {
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "content")
        val element = psiFile.findElementAt(0)!!
        
        val expectedPath = PsiPathUtils.retrievePsiPath(element)
        config.ignoredPsiPaths.add(expectedPath)

        visitor.visitElement(element)

        Assert.assertEquals(1, visitor.textRanges.size)
        Assert.assertEquals(element.textRange, visitor.textRanges[0])
    }

    fun testVisitPartialPsiPathMatch() {
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText("test.txt", PlainTextFileType.INSTANCE, "content")
        val element = psiFile.findElementAt(0)!! // TEXT element
        val parent = element.parent // FILE element
        
        val parentPath = PsiPathUtils.retrievePsiPath(parent)
        val elementPath = PsiPathUtils.retrievePsiPath(element)
        
        // Ensure elementPath starts with parentPath
        Assert.assertTrue("Element path '$elementPath' should start with parent path '$parentPath'", elementPath.startsWith(parentPath))
        
        config.ignoredPsiPaths.add(parentPath)
        
        // Visiting the element directly should also trigger an ignore if parent is ignored
        visitor.visitElement(element)
        
        Assert.assertEquals("Element should be ignored because its parent's path is in the ignore list", 1, visitor.textRanges.size)
        Assert.assertEquals(element.textRange, visitor.textRanges[0])
    }
}
