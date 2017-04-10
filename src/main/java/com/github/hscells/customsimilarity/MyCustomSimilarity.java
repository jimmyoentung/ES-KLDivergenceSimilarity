package com.github.hscells.customsimilarity;

/*
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Normalization;
import org.apache.lucene.search.similarities.Similarity;

import org.apache.lucene.search.similarities.BM25Similarity;


import java.io.IOException;

//
// * Created by Harry Scells on 6/4/17.
//

class MyCustomSimilarity extends Similarity {

    private Normalization normalization;

    MyCustomSimilarity(Normalization normalization) {
        this.normalization = normalization;
    }

    @Override
    public long computeNorm(FieldInvertState state) {
        return 0;
    }

    @Override
    public SimWeight computeWeight(CollectionStatistics collectionStats, TermStatistics... termStats) {
        return null;
    }

    @Override
    public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {
        return null;
    }
}
*/


/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.List;
import java.util.Locale;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.BasicStats;

/**
 * Bayesian smoothing using Dirichlet priors. From Chengxiang Zhai and John
 * Lafferty. 2001. A study of smoothing methods for language models applied to
 * Ad Hoc information retrieval. In Proceedings of the 24th annual international
 * ACM SIGIR conference on Research and development in information retrieval
 * (SIGIR '01). ACM, New York, NY, USA, 334-342.
 * <p>
 * The formula as defined the paper assigns a negative score to documents that
 * contain the term, but with fewer occurrences than predicted by the collection
 * language model. The Lucene implementation returns {@code 0} for such
 * documents.
 * </p>
 *
 * @lucene.experimental
 */
public class MyCustomSimilarity extends LMSimilarity {
    /** The &mu; parameter. */
    private final float mu;

    /** Instantiates the similarity with the provided &mu; parameter. */
    public MyCustomSimilarity(CollectionModel collectionModel, float mu) {
        super(collectionModel);
        this.mu = mu;
    }

    /** Instantiates the similarity with the provided &mu; parameter. */
    public MyCustomSimilarity(float mu) {
        this.mu = mu;
    }

    /** Instantiates the similarity with the default &mu; value of 2000. */
    public MyCustomSimilarity(CollectionModel collectionModel) {
        this(collectionModel, 2000);
    }

    /** Instantiates the similarity with the default &mu; value of 2000. */
    public MyCustomSimilarity() {
        this(2000);
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        float score = stats.getBoost() * (float)(Math.log(1 + freq /
                (mu * ((LMStats)stats).getCollectionProbability())) +
                Math.log(mu / (docLen + mu)));
        return score > 0.0f ? score : 0.0f;
    }

    @Override
    protected void explain(List<Explanation> subs, BasicStats stats, int doc,
                           float freq, float docLen) {
        if (stats.getBoost() != 1.0f) {
            subs.add(Explanation.match(stats.getBoost(), "boost"));
        }

        subs.add(Explanation.match(mu, "mu"));
        Explanation weightExpl = Explanation.match(
                (float)Math.log(1 + freq /
                        (mu * ((LMStats)stats).getCollectionProbability())),
                "term weight");
        subs.add(weightExpl);
        subs.add(Explanation.match(
                (float)Math.log(mu / (docLen + mu)), "document norm"));
        super.explain(subs, stats, doc, freq, docLen);
    }

    /** Returns the &mu; parameter. */
    public float getMu() {
        return mu;
    }

    @Override
    public String getName() {
        return String.format(Locale.ROOT, "Dirichlet(%f)", getMu());
    }
}