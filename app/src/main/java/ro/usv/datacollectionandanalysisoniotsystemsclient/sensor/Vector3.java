package ro.usv.datacollectionandanalysisoniotsystemsclient.sensor;

import androidx.annotation.NonNull;

public class Vector3 {
    final float x;
    final float y;
    final float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "  \"x\" : \"" + x + "\",\n" +
                "  \"y\" : \"" + y + "\",\n" +
                "  \"z\" : \"" + z + "\"\n" +
                "}";
    }
}
