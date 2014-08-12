/*
 *Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.genomics.denovo;

import static com.google.cloud.genomics.denovo.DenovoUtil.Genotype.AA;
import static com.google.cloud.genomics.denovo.DenovoUtil.Genotype.CC;
import static com.google.cloud.genomics.denovo.DenovoUtil.Genotype.TT;
import static com.google.cloud.genomics.denovo.DenovoUtil.InferenceMethod.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.google.api.services.genomics.Genomics;
import com.google.cloud.genomics.denovo.DenovoUtil.TrioIndividual;
import com.google.cloud.genomics.utils.GenomicsFactory;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class BayesInferMapTest {

  private static Genomics genomics;
  private static ExperimentRunner expRunner;
  private static BayesInfer bayesInferrer;

  @BeforeClass
  public static void setUp() throws Exception {

    String homeDir = System.getProperty("user.home");

    String argsString = "stage1 " + "--job_name BayesInferTest "
        + "--client_secrets_filename " + homeDir + "/Downloads/client_secrets.json "
        + "--seq_err_rate 1e-2 " + "--denovo_mut_rate 1e-8";
    String[] args = argsString.split(" ");

    CommandLine cmdLine = new CommandLine();
    cmdLine.setArgs(args);

    genomics = GenomicsFactory.builder("genomics_denovo_caller").build()
        .fromClientSecretsFile(new File(cmdLine.clientSecretsFilename));

    expRunner = new ExperimentRunner(cmdLine, genomics);
    
    bayesInferrer = new BayesInfer(cmdLine.sequenceErrorRate, cmdLine.denovoMutationRate);
  }

  @Test
  public void testGenomicsIsNotNull() {
    assertNotNull(genomics);
  }

  @Test
  public void testExpRunnerIsNotNull() {
    assertNotNull(expRunner);
  }
  
  @Test
  public void testTrioPos816785MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(816785L, expRunner.getReadMap("chr1", 816785L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);
    
    assertFalse(result.isDenovo());
    assertEquals("816785 => [CC,CC,CC]", Arrays.asList(CC,CC,CC), result.getMaxTrioGenoType());
  }
  
  @Test
  public void testTrioPos846600MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(846600L, expRunner.getReadMap("chr1", 846600L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);
    
    assertFalse(result.isDenovo());
    assertEquals("846600 => [CC,CC,CC]", Arrays.asList(CC,CC,CC), result.getMaxTrioGenoType());
  }

  @Test
  public void testTrioPos763769MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(763769L, expRunner.getReadMap("chr1", 763769L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);
    
    assertFalse(result.isDenovo());
    assertEquals("763769 => [AA,AA,AA]", Arrays.asList(AA,AA,AA), result.getMaxTrioGenoType());
  }

  @Test
  public void testTrioPos1298169MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(1298169L, expRunner.getReadMap("chr1", 1298169L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);

    assertFalse(result.isDenovo());
    assertEquals("1298169 => [TT,TT,TT]", Arrays.asList(TT, TT, TT), result.getMaxTrioGenoType());
  }
  
  @Test
  @Ignore("Known Borderline Failure")
  /*chr1,70041751,readCounts=DAD:{T=2, C=58};MOM:{T=2, C=51};
   * CHILD:{T=8, C=28},maxGenoType=[CC, CC, CT],isDenovo=true
   */
  public void testTrioPos70041751MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(70041751L, expRunner.getReadMap("chr1", 70041751L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);

    assertEquals("70041751 => [CC,CC,CC]", Arrays.asList(CC, CC, CC), result.getMaxTrioGenoType());
    assertFalse(result.isDenovo());
  }

  @Test
  /*chr1,149035163,readCounts=DAD:{T=24, A=2, C=225, -=5};MOM:{T=22, G=3, A=6, C=223, -=2};
   * CHILD:{T=34, G=1, A=2, C=218, -=1},maxGenoType=[CC, CC, CT],isDenovo=true
   */
  public void testTrioPos149035163MAP() throws IOException {
    Map<TrioIndividual, ReadSummary> readSummaryMap =
        expRunner.getReadSummaryMap(149035163L, expRunner.getReadMap("chr1", 149035163L));
    BayesInfer.InferenceResult result = bayesInferrer.infer(readSummaryMap, MAP);

    assertEquals("149035163 => [CC,CC,CC]", Arrays.asList(CC, CC, CC), result.getMaxTrioGenoType());
    assertFalse(result.isDenovo());
  }
}