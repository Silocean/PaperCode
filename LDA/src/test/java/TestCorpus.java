/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 17:22</create-date>
 *
 * <copyright file="TestCorpus.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hankcs
 */
public class TestCorpus {
    @Test
    public void testAddDocument() throws Exception {
        List<String> doc1 = new ArrayList<String>();
        doc1.add("hello");
        doc1.add("word");
        List<String> doc2 = new ArrayList<String>();
        doc2.add("hankcs");
        Corpus corpus = new Corpus();
        corpus.addDocument(doc1);
        corpus.addDocument(doc2);
        System.out.println(corpus);
    }

    @Test
    public void testAll() throws Exception {
        // 1. Load corpus from disk
        Corpus corpus = Corpus.load("src/main/resources/data");
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(10);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topicMap);
        // 5. TODO:Predict. I'm not sure whether it works, it is not stable.
        int[] document = Corpus.loadDocument("src/main/resources/data/军事_510.txt", corpus.getVocabulary());
        double[] tp = LdaGibbsSampler.inference(phi, document);
        Map<String, Double> topic = LdaUtil.translate(tp, phi, corpus.getVocabulary(), 10);
        LdaUtil.explain(topic);
    }

    @Test
    public void myTest() throws Exception {
        Corpus corpus = Corpus.load("src/main/resources/data3");
        // System.out.println(corpus);
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        ldaGibbsSampler.gibbs(1);
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 5);
        LdaUtil.explain(topicMap);
    }

    @Test
    public void myTest2() throws Exception {
        Corpus corpus = Corpus.load("src/main/resources/data2");
        // System.out.println(corpus);
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        ldaGibbsSampler.gibbs(2);
        double[][] phi = ldaGibbsSampler.getPhi();
        int[] document = Corpus.loadDocument("src/main/resources/data/健康_1150.txt", corpus.getVocabulary());
        double[] tp = LdaGibbsSampler.inference(phi, document);
        Map<String, Double> topic = LdaUtil.translate(tp, phi, corpus.getVocabulary(), 9);
        LdaUtil.explain(topic);
    }
}
