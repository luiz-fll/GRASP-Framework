package problems.qbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;

public class GRASP_QBF_SC_RANGREEDY extends GRASP_QBF_SC {

    public GRASP_QBF_SC_RANGREEDY(Integer randomMoves, Integer iterations, String filename) throws IOException {
        super(randomMoves.doubleValue(), iterations, filename);
    }

    @Override
    public Solution<Integer> constructiveHeuristic() {
        CL = makeCL();
        RCL = makeRCL();
        sol = createEmptySol();

        // Define o número de movimentos aleatórios.
        int randomMoves = alpha.intValue();

        // Fase 1: Remoções aleatórias viáveis
        for (int i = 0; i < randomMoves; i++) {
            ArrayList<Integer> removalCandidates = new ArrayList<>();
            // Encontra todos os candidatos que podem ser removidos sem invalidar a solução.
            for (Integer cand : sol) {
                if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                    return sol;
                }
                double deltaCost = ObjFunction.evaluateRemovalCost(cand, sol);
                if (deltaCost != Double.NEGATIVE_INFINITY) {
                    removalCandidates.add(cand);
                }
            }

            // Se não houver candidatos viáveis para remoção, a fase aleatória termina.
            if (removalCandidates.isEmpty() || System.currentTimeMillis() - startTime >= timeLimitMillis) {
                break;
            }

            // Escolhe um candidato aleatório viável e o remove.
            int rndIndex = rng.nextInt(removalCandidates.size());
            Integer candToRemove = removalCandidates.get(rndIndex);
            sol.remove(candToRemove);
            sol.cost = ObjFunction.evaluate(sol);
        }

        // Fase 2: Remoções gulosas (best-improving)
        while (System.currentTimeMillis() - startTime < timeLimitMillis) {
            Double minDeltaCost = Double.POSITIVE_INFINITY;
            Integer bestCandOut = null;

            // Encontra o melhor candidato para remoção
            for (Integer candOut : sol) {
                if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                    return sol;
                }
                Double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                // Verifica se a remoção é viável e se melhora a solução.
                if (deltaCost < minDeltaCost && deltaCost != Double.NEGATIVE_INFINITY) {
                    minDeltaCost = deltaCost;
                    bestCandOut = candOut;
                }
            }

            // Se o melhor movimento não for uma melhoria, a construção termina.
            if (minDeltaCost >= -Double.MIN_VALUE || bestCandOut == null) {
                break;
            }

            sol.remove(bestCandOut);
            sol.cost = ObjFunction.evaluate(sol);
        }

        return sol;
    }
}
