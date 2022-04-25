package cse535.group35.mobileoffloading.matrixutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixUtil {

    private static final Logger LOGGER = Logger.getLogger(MatrixUtil.class.getName());

    private int[][] A;
    private int[][] bTranspose;

    public MatrixUtil(int[][] A, int[][] B) {
        this.A = A;
        this.bTranspose =transpose(B);
    }

    public List<MultiplicationResult> getMultiplicationResult(List<Integer> idxsToCalculate) {
        LOGGER.log(Level.WARNING, "Starting multiplication");
        List<MultiplicationResult> multiplicationResults = new ArrayList<>();
        for(int idxToCalculate : idxsToCalculate) {
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

    private static int[][] transpose(int[][] mat) {
        int[][] transpose = new int[mat.length][mat.length];
        for(int i = 0; i < mat.length; i++) {
            for(int j = 0; j < mat.length; j++) {
                transpose[j][i] = mat[i][j];
            }
        }

        return transpose;
    }

    public MultiplicationResult getMultiplicationResultForRow(int rowIdx) {
        int[] row = new int[this.A.length];
        for(int i = 0; i < this.A.length; i++) {
            int idxResult = 0;
            for(int j = 0; j < this.A.length; j++)  {
                idxResult += (this.A[rowIdx][j] * this.bTranspose[i][j]);
            }
            row[i] = idxResult;
        }

        return new MultiplicationResult(row, rowIdx);
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

        public JSONObject toJSON() {
            try {
                JSONObject resultJSON = new JSONObject();
                resultJSON.put("rowIdx", this.resultRowId);
                JSONArray rowJSONArray = new JSONArray();
                for(int num : this.row) {
                    rowJSONArray.put(num);
                }

                resultJSON.put("row", rowJSONArray);
                return resultJSON;
            } catch (JSONException e) {
                LOGGER.log(Level.SEVERE, "Cannot convert matrix result to JSON", e);
                return null;
            }
        }
    }

}
