package org.sweetiebelle.mcprofiler;

import java.util.ArrayList;

class ArrayUtils {
    ArrayUtils() {
    }

    /**
     * Merges the two paramaters into one array.
     * @param original
     * @param altAccounts
     * @return
     */
    Object[] appendAll(final Object[] original, final Object[] altAccounts) {
        int size = actualSize(original);
        for (int i = 0; i < actualSize(altAccounts); i++, size++)
            original[size + 1] = altAccounts[i];
        return original;
    }

    /**
     * Checks if the ArrayList of AltAccounts contains the element
     * @param alreadyadded an ArrayList
     * @param element the element to check
     * @return true if found, else false
     */
    boolean containsUUID(final ArrayList<AltAccount> alreadyadded, final AltAccount element) {
        for (int i = 0; i < alreadyadded.size(); i++)
            if (alreadyadded.get(i).uuid.equals(element.uuid))
                return true;
        return false;
    }

    boolean containsElement(final Object[] original, final Object element) {
        if (element == null)
            throw new NullPointerException("Element cannot be null! ArrayUtils#26");
        for (int i = 0; i < actualSize(original); i++)
            if (original[i].equals(element))
                return true;
        return false;
    }

    Object[] append(final Object[] original, final Object element) {
        if (element == null)
            throw new NullPointerException("Element cannot be null! ArrayUtils#33");
        original[actualSize(original)] = element;
        return original;
    }

    /**
     *
     * @param array an Array to return actual size on
     * @return the actual size of this array. This is the last element position, such that calling this element would result in null.
     */
    int actualSize(final Object[] altAccounts) {
        for (int i = 0; i < altAccounts.length; i++)
            if (altAccounts[i] == null)
                return i;
        return altAccounts.length;
    }
}
