package ru.stachek66.okminer.utils;

/**
 *  Copyright (C) 2012 Kyle Gorman
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright 
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY KYLE GORMAN ''AS IS'' AND ANY EXPRESS OR 
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 *  NO EVENT SHALL KYLE GORMAN BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  ru.stachek66.okminer.utils.Trie.java: a prefix trie for Strings
 *  @author Kyle Gorman <kgorman@ling.upenn.edu>
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Trie {

    HashMap<Character, HashMap> root;

    /**
     * Default contructor
     */
    public Trie() {
        root = new HashMap<Character, HashMap>();
    }

    /**
     * Contructor that takes a String[] of words to add
     *
     * @param sa a String[] of words to be added
     */
    public Trie(String[] sa) {
        this();
        addAll(sa);
    }

    /**
     * Constructor that takes a Collection<String> of words to add
     *
     * @param sc a Collection<String> of words to be added
     */
    public Trie(Collection<String> sc) {
        this();
        addAll(sc);
    }

    /**
     * Adds a string to the trie
     *
     * @param s String to add to the trie
     */
    public void add(String s) {
        HashMap<Character, HashMap> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = curr_node.get(c);
            else {
                curr_node.put(c, new HashMap<Character, HashMap>());
                curr_node = curr_node.get(c);
            }
        }
        curr_node.put('\0', new HashMap<Character, HashMap>(0)); // term
    }

    /**
     * Adds a String[] of words to the trie
     *
     * @param sa String[] to add to the trie
     */
    public void addAll(String[] sa) {
        for (String s : sa)
            add(s);
    }

    /**
     * Adds a Collection<String> of words to the trie
     *
     * @param sc Collection<String> to add to the trie
     */
    public void addAll(Collection<String> sc) {
        for (String s : sc)
            add(s);
    }

    /**
     * Returns true iff the String is in the trie
     *
     * @param s query
     * @return true if the query is in the trie
     */
    public boolean contains(String s) {
        HashMap<Character, HashMap> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = curr_node.get(c);
            else
                return false;
        }
        return (curr_node.containsKey('\0'));
    }

    /**
     * Returns true if the String prefix is in the trie
     *
     * @param s query
     * @return true if the query is in the trie
     * @author alexeyev
     */
    public boolean containsAsPrefix(String s) {
        HashMap<Character, HashMap> curr_node = root;
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey(c))
                curr_node = curr_node.get(c);
            else
                return false;
        }
        return true;
    }

    /**
     * Returns position if prefix found in the trie
     *
     * @param s query
     * @return true if the query is in the trie
     */
    public List<Integer> foundPrefixes(String s) {
        HashMap<Character, HashMap> curr_node = root;
        List<Integer> positions = new LinkedList<>();
        for (int i = 0, n = s.length(); i < n; i++) {
            Character c = s.charAt(i);
            if (curr_node.containsKey('\0')) {
                positions.add(i);
            }
            if (curr_node.containsKey(c)) {
                curr_node = curr_node.get(c);
            } else {
                return positions;
            }
        }
        if (curr_node.containsKey('\0'))
            positions.add(s.length());
        return positions;
    }
}