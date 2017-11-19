package com.aopphp.go.pointcut;

import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpClassMember;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Pattern;

public class SignaturePointcut implements Pointcut
{
    private final Set<KindFilter> filterKind;
    private final String name;
    private final Pattern regexp;
    private final PointFilter modifierFilter;

    private PointFilter classFilter = null;

    public SignaturePointcut(Set<KindFilter> filterKind, @NotNull String name, PointFilter modifierFilter) {
        this.filterKind = filterKind;
        this.name       = name;
        // http://stackoverflow.com/questions/14134558/
        String regexp   = name.replaceAll("([\\\\\\.\\[\\{\\(\\*\\+\\?\\^\\$\\|])", "\\\\$1");
        this.regexp     = Pattern.compile(
            regexp
                .replace("\\*\\*", ".+?")
                .replace("\\*", "[^\\\\]+?")
                .replace("\\?", ".")
                .replace("\\|", "|"));
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

        if (element instanceof PhpClassMember && !modifierFilter.matches(element)) {
            return false;
        }

        String elementName = "";
        if (element instanceof PhpClassMember) {
            elementName = element.getName();
        } else if (element instanceof PhpClass) {
            String elementFQN = element.getFQN();
            elementName = elementFQN != null ? elementFQN.substring(1) : "";
        }

        return elementName.equals(name) || regexp.matcher(elementName).matches();
    }

    @Override
    public Set<KindFilter> getKind() {
        return filterKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignaturePointcut)) return false;

        SignaturePointcut that = (SignaturePointcut) o;

        if (!filterKind.equals(that.filterKind)) return false;
        if (!name.equals(that.name)) return false;
        if (!modifierFilter.equals(that.modifierFilter)) return false;
        return classFilter != null ? classFilter.equals(that.classFilter) : that.classFilter == null;
    }

    @Override
    public int hashCode() {
        int result = filterKind.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + modifierFilter.hashCode();
        result = 31 * result + (classFilter != null ? classFilter.hashCode() : 0);
        return result;
    }
}
