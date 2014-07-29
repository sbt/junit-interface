package com.novocode.junit;

import org.junit.experimental.categories.Category;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.*;

/**
 * Copied from https://github.com/junit-team/junit/blob/master/src/main/java/org/junit/experimental/categories/Categories.java,
 * since JUnit 4.11 only supports including/excluding one category at a time, and since it's an experimental API that
 * could change at any time.
 */
public class CategoryFilter extends Filter {
    private final Set<Class<?>> included;
    private final Set<Class<?>> excluded;
    private final boolean includedAny;
    private final boolean excludedAny;

    public static CategoryFilter include(boolean matchAny, Class<?>... categories) {
        if (hasNull(categories)) {
            throw new NullPointerException("has null category");
        }
        return categoryFilter(matchAny, createSet(categories), true, null);
    }

    public static CategoryFilter include(Class<?> category) {
        return include(true, category);
    }

    public static CategoryFilter include(Class<?>... categories) {
        return include(true, categories);
    }

    public static CategoryFilter exclude(boolean matchAny, Class<?>... categories) {
        if (hasNull(categories)) {
            throw new NullPointerException("has null category");
        }
        return categoryFilter(true, null, matchAny, createSet(categories));
    }

    public static CategoryFilter exclude(Class<?> category) {
        return exclude(true, category);
    }

    public static CategoryFilter exclude(Class<?>... categories) {
        return exclude(true, categories);
    }

    public static CategoryFilter categoryFilter(boolean matchAnyInclusions, Set<Class<?>> inclusions,
                                                boolean matchAnyExclusions, Set<Class<?>> exclusions) {
        return new CategoryFilter(matchAnyInclusions, inclusions, matchAnyExclusions, exclusions);
    }

    protected CategoryFilter(boolean matchAnyIncludes, Set<Class<?>> includes,
                             boolean matchAnyExcludes, Set<Class<?>> excludes) {
        includedAny = matchAnyIncludes;
        excludedAny = matchAnyExcludes;
        included = copyAndRefine(includes);
        excluded = copyAndRefine(excludes);
    }

    /**
     * @see #toString()
     */
    @Override
    public String describe() {
        return toString();
    }

    /**
     * Returns string in the form <tt>&quot;[included categories] - [excluded categories]&quot;</tt>, where both
     * sets have comma separated names of categories.
     *
     * @return string representation for the relative complement of excluded categories set
     * in the set of included categories. Examples:
     * <ul>
     *  <li> <tt>&quot;categories [all]&quot;</tt> for all included categories and no excluded ones;
     *  <li> <tt>&quot;categories [all] - [A, B]&quot;</tt> for all included categories and given excluded ones;
     *  <li> <tt>&quot;categories [A, B] - [C, D]&quot;</tt> for given included categories and given excluded ones.
     * </ul>
     * @see Class#toString() name of category
     */
    @Override public String toString() {
        StringBuilder description= new StringBuilder("categories ")
                .append(included.isEmpty() ? "[all]" : included);
        if (!excluded.isEmpty()) {
            description.append(" - ").append(excluded);
        }
        return description.toString();
    }

    @Override
    public boolean shouldRun(Description description) {
        if (hasCorrectCategoryAnnotation(description)) {
            return true;
        }

        for (Description each : description.getChildren()) {
            if (shouldRun(each)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCorrectCategoryAnnotation(Description description) {
        final Set<Class<?>> childCategories= categories(description);

        // If a child has no categories, immediately return.
        if (childCategories.isEmpty()) {
            return included.isEmpty();
        }

        if (!excluded.isEmpty()) {
            if (excludedAny) {
                if (matchesAnyParentCategories(childCategories, excluded)) {
                    return false;
                }
            } else {
                if (matchesAllParentCategories(childCategories, excluded)) {
                    return false;
                }
            }
        }

        if (included.isEmpty()) {
            // Couldn't be excluded, and with no suite's included categories treated as should run.
            return true;
        } else {
            if (includedAny) {
                return matchesAnyParentCategories(childCategories, included);
            } else {
                return matchesAllParentCategories(childCategories, included);
            }
        }
    }

    /**
     * @return <tt>true</tt> if at least one (any) parent category match a child, otherwise <tt>false</tt>.
     * If empty <tt>parentCategories</tt>, returns <tt>false</tt>.
     */
    private boolean matchesAnyParentCategories(Set<Class<?>> childCategories, Set<Class<?>> parentCategories) {
        for (Class<?> parentCategory : parentCategories) {
            if (hasAssignableTo(childCategories, parentCategory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return <tt>false</tt> if at least one parent category does not match children, otherwise <tt>true</tt>.
     * If empty <tt>parentCategories</tt>, returns <tt>true</tt>.
     */
    private boolean matchesAllParentCategories(Set<Class<?>> childCategories, Set<Class<?>> parentCategories) {
        for (Class<?> parentCategory : parentCategories) {
            if (!hasAssignableTo(childCategories, parentCategory)) {
                return false;
            }
        }
        return true;
    }

    private static Set<Class<?>> categories(Description description) {
        Set<Class<?>> categories= new HashSet<Class<?>>();
        Collections.addAll(categories, directCategories(description));
        Collections.addAll(categories, directCategories(parentDescription(description)));
        return categories;
    }

    private static Description parentDescription(Description description) {
        Class<?> testClass= description.getTestClass();
        return testClass == null ? null : Description.createSuiteDescription(testClass);
    }

    private static Class<?>[] directCategories(Description description) {
        if (description == null) {
            return new Class<?>[0];
        }

        Category annotation= description.getAnnotation(Category.class);
        return annotation == null ? new Class<?>[0] : annotation.value();
    }

    private static Set<Class<?>> copyAndRefine(Set<Class<?>> classes) {
        HashSet<Class<?>> c= new HashSet<Class<?>>();
        if (classes != null) {
            c.addAll(classes);
        }
        c.remove(null);
        return c;
    }

    private static boolean hasNull(Class<?>... classes) {
        if (classes == null) return false;
        for (Class<?> clazz : classes) {
            if (clazz == null) {
                return true;
            }
        }
        return false;
    }

    private static Set<Class<?>> createSet(Class<?>... t) {
        final Set<Class<?>> set = new HashSet<Class<?>>();
        if (t != null) {
            Collections.addAll(set, t);
        }
        return set;
    }

    private static boolean hasAssignableTo(Set<Class<?>> assigns, Class<?> to) {
        for (final Class<?> from : assigns) {
            if (to.isAssignableFrom(from)) {
                return true;
            }
        }
        return false;
    }
}