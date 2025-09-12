package problems.qbf.solvers;

import metaheuristics.grasp.AbstractGRASP;
import solutions.Solution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GRASP_MAIN {
    public static void run(AbstractGRASP<Integer> grasp, String filename) {
        long startTime = System.currentTimeMillis();
        Solution<Integer> bestSol = grasp.solve();
        System.out.println("maxVal = " + bestSol);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

        try (FileWriter escritor = new FileWriter(filename, true)) { // `true` para anexar ao arquivo
            // Converte a solução para uma string e escreve no arquivo
            escritor.write("maxVal = " + bestSol.toString() + "\nTime = "+(double)totalTime/(double)1000+" seg\n");
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        String inputDir = "instances/qbfsc/";
        String outputDir = "instances/qbfsc_out/";

        File folder = new File(inputDir);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println("Processando: " + file.getName());
                run(new GRASP_QBF_SC(0.05, 1000, file.getPath()), outputDir + file.getName() + "ALPHA005.txt");
                run(new GRASP_QBF_SC(0.1, 1000, file.getPath()), outputDir + file.getName() + "ALPHA01.txt");
                run(new GRASP_QBF_SC_FIRST(0.05, 1000, file.getPath()), outputDir + file.getName() + "FIRST.txt");
                run(new GRASP_QBF_SC_RANGREEDY(5, 1000, file.getPath()), outputDir + file.getName() + "RANGREEDY.txt");
                run(new GRASP_QBF_SC_SAMPLED(0.05, 5, 1000, file.getPath()), outputDir + file.getName() + "SAMPLED.txt");
            }
        }
    }
}
