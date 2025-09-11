package problems.qbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;

public class GRASP_QBF_SC_SAMPLED extends GRASP_QBF_SC {
    public int sampleSize;

    public GRASP_QBF_SC_SAMPLED(Double alpha, Integer sampleSize, Integer iterations, String filename) throws IOException {
        super(alpha, iterations, filename);
        this.sampleSize = sampleSize;
    }

    @Override
    public Solution<Integer> constructiveHeuristic() {
        CL = makeCL(); // A CL inicial ainda é a lista completa de candidatos
        RCL = makeRCL();
        sol = createEmptySol();

        while (System.currentTimeMillis() - startTime < timeLimitMillis) {
            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;

            // Cria uma amostra aleatória da CL
            ArrayList<Integer> sampleCL = new ArrayList<>();
            while (sampleCL.size() < sampleSize && !CL.isEmpty()) {
                int rndIndex = rng.nextInt(CL.size());
                sampleCL.add(CL.remove(rndIndex));
            }

            // Se a amostra estiver vazia, a construção termina.
            if (sampleCL.isEmpty()) {
                break;
            }

            // Explora a amostra e encontra os custos min/max
            for (Integer c : sampleCL) {
                Double deltaCost = ObjFunction.evaluateRemovalCost(c, sol);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }

            // Preenche o RCL com base na amostra
            for (Integer c : sampleCL) {
                Double deltaCost = ObjFunction.evaluateRemovalCost(c, sol);
                if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
                    RCL.add(c);
                }
            }

            // Se o RCL estiver vazio (nenhum candidato na amostra foi bom o suficiente), a construção termina
            if (RCL.isEmpty()) {
                break;
            }

            // Escolhe um candidato aleatório do RCL e remove
            int rndIndex = rng.nextInt(RCL.size());
            Integer outCand = RCL.get(rndIndex);
            sol.remove(outCand);
            sol.cost = ObjFunction.evaluate(sol);
            RCL.clear();
        }

        return sol;
    }

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_QBF_SC grasp = new GRASP_QBF_SC_SAMPLED(0.05, 5, 1000, "instances/qbfsc/qbfsc025");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
