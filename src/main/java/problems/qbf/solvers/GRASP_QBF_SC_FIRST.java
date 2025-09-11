package problems.qbf.solvers;

import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;

public class GRASP_QBF_SC_FIRST extends GRASP_QBF_SC {
    public GRASP_QBF_SC_FIRST(Double alpha, Integer iterations, String filename) throws IOException {
        super(alpha, iterations, filename);
    }

    @Override
    public Solution<Integer> localSearch() {
        boolean improved = true;

        while (improved && System.currentTimeMillis() - startTime < timeLimitMillis) {
            improved = false;

            // Cria a lista de candidatos para inserção
            ArrayList<Integer> candidatesToInsert = makeCL();
            candidatesToInsert.removeAll(sol);

            // Avalia inserções (Primeira Melhoria)
            for (Integer candIn : candidatesToInsert) {
                if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                    return sol;
                }
                double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
                if (deltaCost < -Double.MIN_VALUE && deltaCost != Double.NEGATIVE_INFINITY) {
                    sol.add(candIn);
                    ObjFunction.evaluate(sol);
                    improved = true;
                    break; // Para o loop assim que a primeira melhoria é encontrada
                }
            }
            if (improved) continue;

            // Avalia remoções (Primeira Melhoria)
            for (Integer candOut : sol) {
                if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                    return sol;
                }
                double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                if (deltaCost < -Double.MIN_VALUE && deltaCost != Double.NEGATIVE_INFINITY) {
                    sol.remove(candOut);
                    ObjFunction.evaluate(sol);
                    improved = true;
                    break;
                }
            }
            if (improved) continue;

            // Avalia trocas (Primeira Melhoria)
            for (Integer candIn : candidatesToInsert) {
                for (Integer candOut : sol) {
                    if (System.currentTimeMillis() - startTime > timeLimitMillis) {
                        return sol;
                    }
                    double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                    if (deltaCost < -Double.MIN_VALUE && deltaCost != Double.NEGATIVE_INFINITY) {
                        sol.remove(candOut);
                        sol.add(candIn);
                        ObjFunction.evaluate(sol);
                        improved = true;
                        break;
                    }
                }
                if (improved) break; // Quebra o loop interno também
            }
        }

        return sol;
    }
}
