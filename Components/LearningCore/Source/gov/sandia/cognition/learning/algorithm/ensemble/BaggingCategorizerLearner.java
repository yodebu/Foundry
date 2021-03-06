/*
 * File:                BaggingCategorizerLearner.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.BatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.Collection;
import java.util.Random;
import java.util.Set;

/**
 * Learns an categorization ensemble by randomly sampling with replacement
 * (duplicates allowed) some percentage of the size of the data (defaults to
 * 100%) on each iteration to train a new ensemble member. The random sample is
 * referred to as a bag. Each learned ensemble member is given equal weight.
 * The idea here is that randomly sampling from the data and learning a
 * categorizer that has high variance (such as a decision tree) with respect to
 * the input data, one can improve the performance of that
 *
 * By default, the algorithm runs the maxIterations number of steps to create
 * that number of ensemble members. However, one can also use out-of-bag (OOB)
 * error on each iteration to determine a stopping criteria. The OOB error is
 * determined by looking at the performance of the categorizer on the examples
 * that it has not seen.
 *
 * @param   <InputType>
 *      The input type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the input type for the learned ensemble.
 * @param   <CategoryType>
 *      The output type for supervised learning. Passed on to the internal
 *      learning algorithm. Also the output type of the learned ensemble.
 * @author  Justin Basilico
 * @since   3.0
 */
@PublicationReference(
    title="Bagging Predictors",
    author="Leo Breiman",
    year=1996,
    type=PublicationType.Journal,
    publication="Machine Learning",
    pages={123, 140},
    url="http://www.springerlink.com/index/L4780124W2874025.pdf")
public class BaggingCategorizerLearner<InputType, CategoryType>
    extends AbstractBaggingLearner<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>, WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>>
{
    /**
     * Creates a new instance of BaggingCategorizerLearner.
     */
    public BaggingCategorizerLearner()
    {
        this(null);
    }

    /**
     * Creates a new instance of BaggingCategorizerLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     */
    public BaggingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner)
    {
        this(learner, DEFAULT_MAX_ITERATIONS, DEFAULT_PERCENT_TO_SAMPLE, new Random());
    }

    /**
     * Creates a new instance of BaggingCategorizerLearner.
     *
     * @param  learner
     *      The learner to use to create the categorizer on each iteration.
     * @param  maxIterations
     *      The maximum number of iterations to run for, which is also the
     *      number of learners to create.
     * @param   percentToSample
     *      The percentage of the total size of the data to sample on each
     *      iteration. Must be positive.
     * @param  random
     *      The random number generator to use.
     */
    public BaggingCategorizerLearner(
        final BatchLearner<? super Collection<? extends InputOutputPair<? extends InputType, CategoryType>>, ? extends Evaluator<? super InputType, ? extends CategoryType>> learner,
        final int maxIterations,
        final double percentToSample,
        final Random random)
    {
        super(learner, maxIterations, percentToSample, random);
    }

    @Override
    protected WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>> createInitialEnsemble()
    {
        final Set<CategoryType> categories =
            DatasetUtil.findUniqueOutputs(this.getData());
        return new WeightedVotingCategorizerEnsemble<InputType, CategoryType, Evaluator<? super InputType, ? extends CategoryType>>(
            categories);
    }

    @Override
    protected void addEnsembleMember(
        final Evaluator<? super InputType, ? extends CategoryType> member)
    {
        // Add the categorizer to the ensemble and give it equal weight.
        this.ensemble.add(member, 1.0);
    }

}

