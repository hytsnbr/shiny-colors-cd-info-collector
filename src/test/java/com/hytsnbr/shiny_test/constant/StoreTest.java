package com.hytsnbr.shiny_test.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoreTest {
    
    @Test
    void success_getByDomain() {
        final var targetHtmlName = "Yodobashi";
        final var excepted = Store.YODOBASHI;
        final var actual = Store.getByHtmlName(targetHtmlName);
        
        assertEquals(excepted, actual);
    }
    
    @Test
    void throwException_getByDomain() {
        final var targetHtmlName = "TEST";
        
        assertThrows(IllegalArgumentException.class, () -> Store.getByHtmlName(targetHtmlName));
    }
}   