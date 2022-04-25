package cse535.group35.mobileoffloading.matrixutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiplicationResult {

    private static final Logger LOGGER = Logger.getLogger(MatrixUtil.class.getName());
    private final int[] row;
    private final int resultRowId;

    public MultiplicationResult(int[] row, int resultRowId) {
        this.row = row;
        this.resultRowId = resultRowId;
    }

    public String getRowValues() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : row) {
            stringBuilder.append(value);
            stringBuilder.append(", ");
        }

        return stringBuilder.toString();
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
