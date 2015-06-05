package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;

import java.util.Set;
import java.util.regex.Pattern;

public class SignaturePointcut implements Pointcut
{
    private final Set<KindFilter> filterKind;
    private final String name;
    private final String regexp;
    private final PointFilter modifierFilter;

    private PointFilter classFilter = null;

    public SignaturePointcut(Set<KindFilter> filterKind, String name, PointFilter modifierFilter) {
        this.filterKind = filterKind;
        this.name       = name;
        this.regexp     = Pattern.quote(name)
                .replace("\\*", "[^\\\\]+?")
                .replace("\\*\\*", ".+?")
                .replace("\\?", ".")
                .replace("\\|", "|");
        this.modifierFilter = modifierFilter;
    }

    @Override
    public PointFilter getClassFilter() {
        return classFilter;
    }

    public void setClassFilter(PointFilter classFilter) {
        this.classFilter = classFilter;
    }

    @Override
    public boolean matches(PhpNamedElement element) {
        if (!(element instanceof PhpClassMember) || !modifierFilter.matches(element)) {
            return false;
        }

        String elementName = element.getName();
        return elementName.equals(this.name) || elementName.matches(this.regexp);
    }

    @Override
    public Set<KindFilter> getKind() {
        return filterKind;
    }
}
