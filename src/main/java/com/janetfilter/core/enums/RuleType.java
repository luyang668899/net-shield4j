package com.janetfilter.core.enums;

import com.janetfilter.core.rulers.*;

public enum RuleType {
    PREFIX(new PrefixRuler()),
    PREFIX_IC(new PrefixICRuler()),
    SUFFIX(new SuffixRuler()),
    SUFFIX_IC(new SuffixICRuler()),
    KEYWORD(new KeywordRuler()),
    KEYWORD_IC(new KeywordICRuler()),
    EQUAL(new EqualRuler()),
    EQUAL_IC(new EqualICRuler()),
    CONTAINS_ANY(new ContainsAnyRuler()),
    CONTAINS_ANY_IC(new ContainsAnyICRuler()),
    REGEXP(new RegExpRuler());

    private final Ruler ruler;

    RuleType(Ruler ruler) { // Lazy here. No lazy loading
        this.ruler = ruler;
    }

    public Ruler getRuler() {
        return ruler;
    }
}
