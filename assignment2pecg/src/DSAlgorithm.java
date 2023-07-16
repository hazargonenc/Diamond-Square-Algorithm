import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class DSAlgorithm {
    public static final String FILE_NAME = "terrain.obj";

    public static void main(String[] args) throws FileNotFoundException {
        float[][] heightmap = generateDSHeightmap(6,(float) 1.25,10);
        exportToObj(heightmap);
    }
    //n -> size
    //r -> roughness
    //s -> scale
    public static float[][] generateDSHeightmap(int n, float r, float s){
        int totalSize = ((int) Math.pow(2, n)) + 1;
        float[][] heightmap = new float[totalSize][totalSize];

        Random rand = new Random();

        heightmap[0][0] = (float)rand.nextGaussian();
        heightmap[0][totalSize - 1] = (float)rand.nextGaussian();
        heightmap[totalSize - 1][0] = (float)rand.nextGaussian();
        heightmap[totalSize - 1][totalSize - 1] = (float)rand.nextGaussian();

        int availableSize = totalSize - 1;
        int tileSize = availableSize;

        for (int i = 0; i < n; i++) {

            //Diamond Step
            for (int y = 0; y < availableSize; y+=tileSize) {
                for (int x = 0; x < availableSize; x+=tileSize) {

                    float topLeft = heightmap[x][y];
                    float topRight = heightmap[x][y + tileSize];
                    float bottomLeft = heightmap[x + tileSize][y];
                    float bottomRight = heightmap[x + tileSize][y + tileSize];

                    float middle = (topLeft + topRight + bottomLeft + bottomRight) / 4 + (float) rand.nextGaussian() * s;

                    heightmap[x + tileSize/2][y + tileSize/2] = middle;
                }
            }

            for (int y = 0; y < availableSize; y+=tileSize) {
                for (int x = 0; x < availableSize; x += tileSize) {

                    int tileMiddleX = x + tileSize / 2;
                    int tileMiddleY = y + tileSize / 2;

                    float topLeft = heightmap[x][y];
                    float topRight = heightmap[x + tileSize][y];
                    float bottomLeft = heightmap[x][y + tileSize];
                    float bottomRight = heightmap[x + tileSize][y + tileSize];
                    float middle = heightmap[tileMiddleX][tileMiddleY];

                    float top = (topLeft + middle + topRight) / 3 + (float) rand.nextGaussian() * s;
                    float bottom = (bottomLeft + middle + bottomRight) / 3 + (float) rand.nextGaussian() * s;
                    float left = (topLeft + middle + bottomLeft) / 3 + (float) rand.nextGaussian() * s;
                    float right = (topRight + middle + bottomRight) / 3 + (float) rand.nextGaussian() * s;

                    heightmap[tileMiddleX][y] = top;
                    heightmap[tileMiddleX][y + tileSize] = bottom;
                    heightmap[x][tileMiddleY] = left;
                    heightmap[x + tileSize][tileMiddleY] = right;
                }
            }

            tileSize /= 2;
            s /= Math.pow(2, r);
        }

        return heightmap;
    }

    public static void exportToObj(float[][] heightMap) throws FileNotFoundException {
        int size = heightMap.length;

        PrintWriter writer = new PrintWriter(FILE_NAME);

        // Write Vertices
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float z = heightMap[x][y];
                String vertex = String.format("v %f %f %f", (float) x, (float) y, z);
                writer.println(vertex);
            }
        }

        // Write Faces
        for (int i = 1; i <= size * (size - 1); i++) {
            if (i % size == 0) {
                continue;
            }

            String faceUp = String.format("f %d %d %d", i, i + 1, i + size);
            String faceDown = String.format("f %d %d %d", i + size, i + 1, i + size + 1);

            writer.println(faceUp);
            writer.println(faceDown);
        }

        writer.flush();
        writer.close();
    }
}
