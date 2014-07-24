package com.google.cloud.genomics.denovo;

import static org.junit.Assert.*;

import com.google.cloud.genomics.denovo.DenovoUtil.Genotypes;
import com.google.cloud.genomics.denovo.DenovoUtil.TrioIndividual;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Insert description here. (generated by smoitra)
 */
public class DenovoBayesNetTest {

  /**
   * Test method for
   * {@link com.google.cloud.genomics.denovo.DenovoBayesNet#DenovoBayesNet(double, double)}.
   */

  private DenovoBayesNet dbn;
  private Map<List<Genotypes>, Double> conditionalProbabilityTable;
  private double EPS = 1e-12;

  @Before
  public void setUp() {
    dbn = new DenovoBayesNet(1e-2, 1e-8);

    conditionalProbabilityTable = new HashMap<>();
    int numGenotypes = Genotypes.values().length;
    for (Genotypes genotype : Genotypes.values()) {
      conditionalProbabilityTable.put(Collections.singletonList(genotype),
          Double.valueOf(1.0 / numGenotypes));
    }

    // makes sure conditionalProbabilityTable is set up properly
    double totProb = 0.0;
    for (Double prob : conditionalProbabilityTable.values()) {
      totProb += prob;
    }
    assertTrue((totProb >= 1 - EPS && totProb <= 1 + EPS));
  }

  @After
  public void tearDown() {
    dbn = new DenovoBayesNet(1e-2, 1e-8);
  }

  @Test
  public void testDenovoBayesNet() {
    assertNotNull(dbn);
    assertTrue(
        dbn.getSequenceErrorRate() >= 1e-2 - 1e-12 && dbn.getSequenceErrorRate() <= 1e-2 + 1e-12);
    assertTrue(
        dbn.getDenovoMutationRate() >= 1e-8 - 1e-12 && dbn.getDenovoMutationRate() <= 1e-8 + 1e-12);
  }

  /**
   * Test method for {@link com.google.cloud.genomics.denovo.DenovoBayesNet#addNode(com.google.cloud.genomics.denovo.Node)}
   */
  @Test
  public void testAddNodeNodeOfTrioIndividualGenotypes() {
    Node<TrioIndividual, Genotypes> dadNode =
        new Node<>(TrioIndividual.DAD, null, conditionalProbabilityTable);

    Node<TrioIndividual, Genotypes> momNode =
        new Node<>(TrioIndividual.MOM, null, conditionalProbabilityTable);

    Node<TrioIndividual, Genotypes> childNode = new Node<>(TrioIndividual.CHILD,
        Arrays.asList(dadNode, momNode), conditionalProbabilityTable);

    dbn.addNode(dadNode);
    dbn.addNode(momNode);
    dbn.addNode(childNode);

    assertEquals(dbn.nodeMap.get(TrioIndividual.DAD), dadNode);
    assertEquals(dbn.nodeMap.get(TrioIndividual.MOM), momNode);
    assertEquals(dbn.nodeMap.get(TrioIndividual.CHILD), childNode);

    assertEquals(dbn.nodeMap.get(TrioIndividual.CHILD).parents, Arrays.asList(dadNode, momNode));
    assertEquals(dbn.nodeMap.get(TrioIndividual.DAD).parents, null);
    assertEquals(dbn.nodeMap.get(TrioIndividual.MOM).parents, null);
  }
}
