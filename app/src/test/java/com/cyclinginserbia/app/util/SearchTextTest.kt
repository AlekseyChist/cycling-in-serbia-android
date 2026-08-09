package com.cyclinginserbia.app.util

import org.junit.Test
import org.junit.Assert.*

class SearchTextTest {

    @Test
    fun `folds every serbian diacritic to its plain letter`() {
        assertEquals("s", "š".foldForSearch())
        assertEquals("s", "Š".foldForSearch())
        assertEquals("c", "č".foldForSearch())
        assertEquals("c", "Č".foldForSearch())
        assertEquals("c", "ć".foldForSearch())
        assertEquals("c", "Ć".foldForSearch())
        assertEquals("z", "ž".foldForSearch())
        assertEquals("z", "Ž".foldForSearch())
        assertEquals("d", "đ".foldForSearch())
        assertEquals("d", "Đ".foldForSearch())
    }

    @Test
    fun `folds a real region name`() {
        assertEquals("sumadija", "Šumadija".foldForSearch())
    }

    @Test
    fun `folds the d with stroke`() {
        assertEquals("derdap", "Đerdap".foldForSearch())
    }

    @Test
    fun `leaves plain ascii alone apart from case`() {
        assertEquals("abc123", "ABC123".foldForSearch())
    }

    @Test
    fun `keeps digits spaces and punctuation`() {
        assertEquals("a b c 1 2 3 ! ? . ,", "A B C 1 2 3 ! ? . ,".foldForSearch())
    }

    @Test
    fun `empty string folds to empty string`() {
        assertEquals("", "".foldForSearch())
    }

    @Test
    fun `finds a track when the query drops diacritics`() {
        assertTrue("Šabac loop".containsFolded("sabac"))
    }

    @Test
    fun `finds a track when the query has diacritics but the name does not`() {
        assertTrue("Sabac loop".containsFolded("Šabac"))
    }

    @Test
    fun `does not match an unrelated query`() {
        assertFalse("Šabac loop".containsFolded("beograd"))
    }

    @Test
    fun `empty query always matches`() {
        assertTrue("anything".containsFolded(""))
    }
}
