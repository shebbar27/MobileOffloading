package cse535.group35.mobileoffloading.matrixutil;

import java.util.ArrayList;
import java.util.List;

public class MatrixUtil {

    private int[][] A;
    private int[][] B;

    public MatrixUtil(int[][] A, int[][] B) {
        this.A = A;
        this.B = B;
    }

    public List<MultiplicationResult> getMultiplicationResult(List<Integer> idxsToCalculate) {
        List<MultiplicationResult> multiplicationResults = new ArrayList<>();


        return multiplicationResults;
    }


    public static class MultiplicationResult {
        private int[] row;
        private int resultRowId;

        private MultiplicationResult(int[] row, int resultRowId) {
            this.row = row;
            this.resultRowId = resultRowId;
        }

        public int[] getRow() {
            return this.row;
        }

        public int getResultRowId() {
            return this.resultRowId;
        }
    }
}
