package problems.qbf;

import solutions.Solution;

import java.io.*;
import java.util.HashSet;

public class QBF_SC extends QBF_Inverse {
    public HashSet<Integer>[] sets;
    public HashSet<Integer> universe;

    public QBF_SC(String filename) throws IOException {
        super(filename);
    }

    @Override
    public Double evaluateInsertionCost(Integer elem, Solution<Integer> sol) {
        if (coversUniverse(sol)) {
            return super.evaluateInsertionCost(elem, sol);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public Double evaluateRemovalCost(Integer elem, Solution<Integer> sol) {
        if (coversUniverse(sol)) {
            return super.evaluateRemovalCost(elem, sol);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    public Double evaluateExchangeCost(Integer elemIn, Integer elemOut, Solution<Integer> sol) {
        if (coversUniverse(sol)) {
            return super.evaluateExchangeCost(elemIn, elemOut, sol);
        } else {
            return Double.NEGATIVE_INFINITY;
        }
    }

    @Override
    protected Integer readInput(String filename) throws IOException {

        Reader fileInst = new BufferedReader(new FileReader(filename));
        StreamTokenizer stok = new StreamTokenizer(fileInst);

        stok.nextToken();
        Integer _size = (int) stok.nval;
        A = new Double[_size][_size];
        sets = new HashSet[_size];
        universe = new HashSet<>();

        Integer[] set_sizes = new Integer[_size];
        for (int i = 0; i < _size; i++) {
            universe.add(i + 1);
            sets[i] = new HashSet<>();
            stok.nextToken();
            set_sizes[i] = (int) stok.nval;
        }

        for (int i = 0; i < _size; i++) {
            for (int j = 0; j < set_sizes[i]; j++) {
                stok.nextToken();
                sets[i].add((int) stok.nval);
            }
        }

        for (int i = 0; i < _size; i++) {
            for (int j = i; j < _size; j++) {
                stok.nextToken();
                A[i][j] = stok.nval;
                if (j>i)
                    A[j][i] = 0.0;
            }
        }

        return _size;
    }

    public boolean coversUniverse(Solution<Integer> sol) {
        HashSet<Integer> coveredElements = new HashSet<>();
        for (Integer selectedSetIndex : sol) {
            coveredElements.addAll(sets[selectedSetIndex]);
        }

        return coveredElements.size() == universe.size();
    }
}
