package de.nrunodos.plugins

import com.intellij.diff.DiffContext
import com.intellij.diff.DiffExtension
import com.intellij.diff.FrameDiffTool
import com.intellij.diff.requests.DiffRequest
import java.awt.BorderLayout

class ComparisonExtension : DiffExtension() {
    override fun onViewerCreated(
        viewer: FrameDiffTool.DiffViewer,
        context: DiffContext,
        request: DiffRequest,
    ) {
        if (viewer.javaClass.name == "com.intellij.diff.tools.dir.DirDiffViewer") {
            return
        }
        val gui = IntegratedConfigurationPanel(context, viewer)
        viewer.component.add(gui.rootPanel, BorderLayout.SOUTH)
    }
}
