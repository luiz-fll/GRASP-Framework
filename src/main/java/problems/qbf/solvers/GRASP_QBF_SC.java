package problems.qbf.solvers;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF_SC;
import solutions.Solution;

import java.io.IOException;
import java.util.ArrayList;

public class GRASP_QBF_SC extends AbstractGRASP<Integer> {

    public GRASP_QBF_SC(Double alpha, Integer iterations, String filename) throws IOException {
        super(new QBF_SC(filename), alpha, iterations);
    }

    @Override
    public ArrayList<Integer> makeCL() {
        ArrayList<Integer> _CL = new ArrayList<Integer>();
        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            Integer cand = i;
            _CL.add(cand);
        }

        return _CL;
    }

    @Override
    public ArrayList<Integer> makeRCL() {
        ArrayList<Integer> _RCL = new ArrayList<Integer>();

        return _RCL;
    }

    @Override
    public void updateCL() {
        //
    }

    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<Integer>();

        for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
            sol.add(i);
        }

        sol.cost = ObjFunction.evaluate(sol);

        return sol;
    }

    @Override
    public Solution<Integer> constructiveHeuristic() {

        CL = makeCL();
        RCL = makeRCL();
        sol = createEmptySol();
        cost = Double.POSITIVE_INFINITY;

        /* Main loop, which repeats until the stopping criteria is reached. */
        while (System.currentTimeMillis() - startTime < timeLimitMillis) {

            double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
            cost = ObjFunction.evaluate(sol);

            /*
             * Explore all candidate elements to enter the solution, saving the
             * highest and lowest cost variation achieved by the candidates.
             */
            for (Integer c : CL) {
                Double deltaCost = ObjFunction.evaluateRemovalCost(c, sol);
                if (deltaCost < minCost)
                    minCost = deltaCost;
                if (deltaCost > maxCost)
                    maxCost = deltaCost;
            }

            /*
             * Among all candidates, insert into the RCL those with the highest
             * performance using parameter alpha as threshold.
             */
            for (Integer c : CL) {
                Double deltaCost = ObjFunction.evaluateRemovalCost(c, sol);
                if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
                    RCL.add(c);
                }
            }

            /* Choose a candidate randomly from the RCL (there might be no candidate if all solutions are invalid) */
            if (!RCL.isEmpty()) {
                int rndIndex = rng.nextInt(RCL.size());
                Integer outCand = RCL.get(rndIndex);
                CL.remove(outCand);
                sol.remove(outCand);
                ObjFunction.evaluate(sol);
                RCL.clear();
            } else {
                break;
            }
        }

        return sol;
    }

    @Override
    public Solution<Integer> localSearch() {

        Double minDeltaCost;
        Integer bestCandIn = null, bestCandOut = null;

        do {
            minDeltaCost = Double.POSITIVE_INFINITY;

            ArrayList<Integer> candidatesToInsert = makeCL();
            candidatesToInsert.removeAll(sol);

            // Evaluate insertions
            for (Integer candIn : candidatesToInsert) {
                double deltaCost = ObjFunction.evaluateInsertionCost(candIn, sol);
                if (deltaCost < minDeltaCost && deltaCost != Double.NEGATIVE_INFINITY) {
                    minDeltaCost = deltaCost;
                    bestCandIn = candIn;
                    bestCandOut = null;
                }
            }
            // Evaluate removals
            for (Integer candOut : sol) {
                double deltaCost = ObjFunction.evaluateRemovalCost(candOut, sol);
                if (deltaCost < minDeltaCost && deltaCost != Double.NEGATIVE_INFINITY) {
                    minDeltaCost = deltaCost;
                    bestCandIn = null;
                    bestCandOut = candOut;
                }
            }
            // Evaluate exchanges
            for (Integer candIn : candidatesToInsert) {
                for (Integer candOut : sol) {
                    double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, sol);
                    if (deltaCost < minDeltaCost && deltaCost != Double.NEGATIVE_INFINITY) {
                        minDeltaCost = deltaCost;
                        bestCandIn = candIn;
                        bestCandOut = candOut;
                    }
                }
            }
            // Implement the best move, if it reduces the solution cost.
            if (minDeltaCost < -Double.MIN_VALUE) {
                if (bestCandOut != null) {
                    sol.remove(bestCandOut);
                }
                if (bestCandIn != null) {
                    sol.add(bestCandIn);
                }
                ObjFunction.evaluate(sol);
            }
        } while (minDeltaCost < -Double.MIN_VALUE && System.currentTimeMillis() - startTime < timeLimitMillis);

        return null;
    }

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();
        GRASP_QBF_SC grasp = new GRASP_QBF_SC(0.05, 1000, "instances/qbfsc/qbfsc025");
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

    }
}
