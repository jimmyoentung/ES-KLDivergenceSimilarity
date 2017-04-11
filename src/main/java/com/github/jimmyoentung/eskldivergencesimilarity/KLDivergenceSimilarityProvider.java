package com.github.jimmyoentung.eskldivergencesimilarity;

//
// Created by Harry Scells on 6/4/17.
// Modified by Jimmy on 11 Apr 2017
//


import org.apache.lucene.search.similarities.Similarity;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.similarity.AbstractSimilarityProvider;



/**
 * {@link SimilarityProvider} for {@link LMDirichletSimilarity}.
 * <p>
 * Configuration options available:
 * <ul>
 *     <li>mu</li>
 * </ul>
 * @see LMDirichletSimilarity For more information about configuration
 */
public class KLDivergenceSimilarityProvider extends AbstractSimilarityProvider {

    private final KLDivergenceSimilarity similarity;

    public KLDivergenceSimilarityProvider(String name, Settings settings) {
        super(name);
        float mu = settings.getAsFloat("mu", 2000f);

        this.similarity = new KLDivergenceSimilarity(mu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Similarity get() {
        return similarity;
    }
}