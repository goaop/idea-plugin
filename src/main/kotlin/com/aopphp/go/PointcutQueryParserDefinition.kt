package com.aopphp.go

import com.aopphp.go.parser.PointcutLexer
import com.aopphp.go.parser.PointcutParser
import com.aopphp.go.psi.PointcutFile
import com.aopphp.go.psi.PointcutTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.Language
import com.intellij.lang.ParserDefinition
import com.intellij.lexer.FlexAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class PointcutQueryParserDefinition : ParserDefinition {

    companion object {
        @JvmField val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        @JvmField val COMMENTS = TokenSet.create(PointcutTypes.T_COMMENT)
        @JvmField val FILE = IFileElementType(Language.findInstance(PointcutQueryLanguage::class.java))
    }

    override fun createLexer(project: Project) = FlexAdapter(PointcutLexer(null))

    override fun getWhitespaceTokens() = WHITE_SPACES

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createParser(project: Project) = PointcutParser()

    override fun getFileNodeType() = FILE

    override fun createFile(viewProvider: FileViewProvider) = PointcutFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) =
        ParserDefinition.SpaceRequirements.MAY

    override fun createElement(node: ASTNode) = PointcutTypes.Factory.createElement(node)!!
}
