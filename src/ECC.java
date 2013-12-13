
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Ryan Kasprzyk
 */
public class ECC {

    public static int k, a, b, prime, nA;
    public static int[] G, c1, c2, testC1, testC2, tPlain, pub;
    public static int[][] tCipher;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new FileReader("input.txt"));
        String line = in.readLine();
        prime = Integer.parseInt(line);
        String[] split = in.readLine().split(" ");
        a = Integer.parseInt(split[0]);
        b = Integer.parseInt(split[1]);
        split = in.readLine().split(" ");
        G = new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        line = in.readLine();
        nA = Integer.parseInt(line);
        split = in.readLine().split(" ");
        tPlain = new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        line = in.readLine();
        k = Integer.parseInt(line);
        split = in.readLine().split(" ");
        testC1 = new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        testC2 = new int[]{Integer.parseInt(split[2]), Integer.parseInt(split[3])};
        tCipher = new int[][]{testC1, testC2};
        in.close();
        BufferedWriter out = new BufferedWriter(new FileWriter("output.txt"));
        pub = keyGen();
        int[][] enc = encrypt(tPlain);
        int[] pl = decrypt(tCipher);
        out.write(toString(pub));
        out.newLine();
        out.write(Integer.toString(nA));
        out.newLine();
        out.write(toString(enc[0]) + toString(enc[1]));
        out.newLine();
        out.write(toString(pl));
        out.close();
    }

    public static int[] keyGen() {
        if (nA == 0) {
            return new int[]{0, 0};
        } else if (nA == 1) {
            return G;
        } else {
            int[] result = doub(G);
            for (int i = 2; i < nA; i++) {
                result = add(result, G);
            }
            return result;
        }
    }

    public static int[][] encrypt(int[] p) {
        if (k == 0) {
            c1 = new int[]{0, 0};
            c2 = add(new int[]{0, 0}, p);
        } else if (k == 1) {
            c1 = G;
            c2 = add(pub, p);
        } else {
            c1 = doub(G);
            c2 = doub(pub);
            for (int i = 2; i < k; i++) {
                c1 = add(c1, G);
                c2 = add(c2, pub);
            }
            c2 = add(c2, p);
        }
        return new int[][]{c1, c2};
    }

    public static int[] decrypt(int[][] c) {
        int[] plain;
        if (nA == 0) {
            plain = new int[]{0, 0};
        } else if (nA == 1) {
            plain = c[0];
        } else {
            plain = doub(c[0]);
            for (int i = 2; i < nA; i++) {
                plain = add(plain, c[0]);
            }
        }
        int[] result = negate(plain);
        plain = add(result, c[1]);
        return plain;
    }

    public static int[] add(int[] p1, int[] p2) {
        int x3, y3;

        if (p1[0] == p2[0]) {
            return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        } else if (p1[0] == Integer.MAX_VALUE) {
            return p2;
        } else if (p2[0] == Integer.MAX_VALUE) {
            return p1;
        } else {
            x3 = square((mod(p2[1] - p1[1])) * EEA(mod((p2[0] - p1[0])), prime)[0]) - p1[0] - p2[0];
            x3 = mod(x3);

            y3 = (mod((p2[1] - p1[1])) * EEA((p2[0] - p1[0]), prime)[0]) * mod(p1[0] - x3) - p1[1];
            y3 = mod(y3);
            return new int[]{x3, y3};
        }
    }

    public static int[] doub(int[] coord) {
        int x4, y4;

        x4 = ((3 * square(coord[0])) + a);
        x4 *= EEA(2 * coord[1], prime)[0];
        x4 = square(x4);
        x4 -= (2 * (coord[0]));
        x4 = mod(x4);

        y4 = ((3 * square(coord[0])) + a);
        y4 *= EEA(2 * coord[1], prime)[0];
        y4 = mod(y4);
        y4 *= mod(coord[0] - x4);
        y4 += mod(-coord[1]);
        y4 = mod(y4);

        return new int[]{x4, y4};
    }

    public static int[] EEA(int a, int b) {
        if (b == 0) {
            return new int[]{1, 0};
        } else {
            int q = a / b;
            int r = mod(a, b);
            int[] R = EEA(b, r);
            int i = mod(R[0] - q * R[1]);
            return new int[]{R[1], i};
        }
    }

    public static int[] negate(int[] in) {
        int[] result = in;
        result[1] = mod(result[1] * -1);
        return result;
    }

    public static int mod(int i) {
        int result = i;
        if (result >= 0) {
            result = result % prime;
        } else {
            while (result < 0) {
                result += prime;
            }
        }
        return result;
    }

    public static int mod(int i, int mo) {
        int result = i;
        if (result >= 0) {
            result %= Math.abs(mo);
        } else {
            while (result < 0) {
                result += mo;
            }
        }
        return result;
    }

    public static int square(int i) {
        return i * i;
    }

    public static String toString(int[] output) {
        String out = "";
        for (int i = 0; i < output.length; i++) {
            out += Integer.toString(output[i]) + " ";
        }
        return out;
    }
}
