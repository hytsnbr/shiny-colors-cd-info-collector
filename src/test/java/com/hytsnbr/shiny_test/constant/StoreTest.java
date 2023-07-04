package com.hytsnbr.shiny_test.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StoreTest {
    
    @Test
    void success_getByDomain() {
        final var targetStoreName = "shop.asobistore.jp";
        final var excepted = Store.ASOBI_STORE;
        final var actual = Store.getByDomain(targetStoreName);
        
        assertEquals(excepted, actual);
    }
    
    @Test
    void throwException_getByDomain() {
        final var targetStoreName = "TEST";
        
        assertThrows(IllegalArgumentException.class, () -> Store.getByDomain(targetStoreName));
    }
}   