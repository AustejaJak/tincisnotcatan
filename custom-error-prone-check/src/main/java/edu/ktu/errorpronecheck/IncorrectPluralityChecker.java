package edu.ktu.errorpronecheck;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.Tree.Kind;

import javax.lang.model.element.Name;

@AutoService(BugChecker.class)
@BugPattern(
    name = "IncorrectPluralityCheck", 
    summary = "Array variables should be in plural", 
    severity = BugPattern.SeverityLevel.ERROR
)
public class IncorrectPluralityChecker extends BugChecker implements BugChecker.VariableTreeMatcher {
    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        Name name = tree.getName();
        Tree type = tree.getType();
        if (type.getKind() != Kind.ARRAY_TYPE) {
            return Description.NO_MATCH;
        }
        if (Character.toLowerCase(name.charAt(name.length() - 1)) == 's') {
            return Description.NO_MATCH;
        }
        return describeMatch(tree);
    }
}