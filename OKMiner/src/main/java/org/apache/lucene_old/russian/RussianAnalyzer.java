/**
 * Copyright 2009 Alexander Kuznetsov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.lucene_old.russian;

import org.apache.lucene_old.analyzer.MorphologyAnalyzer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.Reader;


/**
 * Adapted by alexeyev
 */
public class RussianAnalyzer extends MorphologyAnalyzer {
    public RussianAnalyzer() throws IOException {
        super(new RussianLuceneMorphology());
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        throw new NotImplementedException();
    }
}
