package cse535.group35.mobileoffloading.matrixutil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cse535.group35.mobileoffloading.master.MasterActivity;

public class MatrixUtil {

    private static final Logger LOGGER = Logger.getLogger(MatrixUtil.class.getName());

    private final int[][] A;
    private final int[][] transposeOfB;

    public MatrixUtil(int[][] A, int[][] B) {
        this.A = A;
        this.transposeOfB =transpose(B);
    }

    public List<MultiplicationResult> getMultiplicationResult(List<Integer> indexesToCalculate) {
        LOGGER.log(Level.WARNING, "Starting multiplication");
        List<MultiplicationResult> multiplicationResults = new ArrayList<>();
        for(int idxToCalculate : indexesToCalculate) {
            multiplicationResults.add(getMultiplicationResultForRow(idxToCalculate));
        }

        return multiplicationResults;
    }

    public static JSONArray getMultiplicationResultJSONArray(List<MultiplicationResult> multiplicationResults) {
        JSONArray multiplicationResultsJSONArray = new JSONArray();
        for(MultiplicationResult multiplicationResult : multiplicationResults) {
            multiplicationResultsJSONArray.put(multiplicationResult.toJSON());
        }

        return multiplicationResultsJSONArray;
    }

    public static String getStringFromMatrix() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Result: \n");
        for(int[] row : MasterActivity.matrixResult) {
            for(int num : row) {
                stringBuilder.append(num).append(" ");
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public MultiplicationResult getMultiplicationResultForRow(int rowIdx) {
        int[] row = new int[this.A.length];
        for(int i = 0; i < this.A.length; i++) {
            int idxResult = 0;
            for(int j = 0; j < this.A.length; j++)  {
                idxResult += (this.A[rowIdx][j] * this.transposeOfB[i][j]);
            }
            row[i] = idxResult;
        }

        return new MultiplicationResult(row, rowIdx);
    }

    private static int[][] transpose(int[][] mat) {
        int[][] transpose = new int[mat.length][mat.length];
        for(int i = 0; i < mat.length; i++) {
            for(int j = 0; j < mat.length; j++) {
                transpose[j][i] = mat[i][j];
            }
        }

        return transpose;
    }
}
