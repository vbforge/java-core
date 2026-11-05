package com.vbforge.practice.practice_5_enum_as_field;

/** Domain class for {@link FreshJuiceDemo} and {@link JuiceSizeParsingDemo}: the size field holds one of three constants. */
class FreshJuice {

    enum FreshJuiceSize { SMALL, MEDIUM, LARGE }

    // size can only hold one of the three predefined values of FreshJuiceSize
    FreshJuiceSize size;

    public FreshJuice(FreshJuiceSize size) {
        this.size = size;
    }

    public String getDescription() {
        return "Fresh Juice Size: " + size;
    }
}
