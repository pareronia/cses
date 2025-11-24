package com.github.pareronia.cses._1619;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Restaurant Customers
 * @see <a href="https://cses.fi/problemset/task/1619">https://cses.fi/problemset/task/1619</a>
 */
public class RestaurantCustomers {

    private final InputStream in;
    private final PrintStream out;

    public RestaurantCustomers(final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @SuppressWarnings("unused")
    private void handleTestCase(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int j = 0; j < n; j++) {
            map.put(sc.nextInt(), 1);
            map.put(sc.nextInt(), -1);
        }
        int tot = 0;
        int hi = 0;
        for (final int v : map.values()) {
            tot += v;
            hi = Math.max(hi, tot);
        }
        this.out.println(hi);
    }

    private void handleTestCaseAlt(final int i, final FastScanner sc) {
        final int n = sc.nextInt();
        final TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int j = 0; j < n; j++) {
            map.put(sc.nextInt(), 1);
            map.put(sc.nextInt(), -1);
        }
        final int[] b = new int[2 * n + 1];
        int curr = 0;
        int k = 0;
        final Iterator<Entry<Integer, Integer>> keys = map.entrySet().iterator();
        for (int j = 0; j < 2 * n; j++) {
            final Entry<Integer, Integer> v = keys.next();
            if (j > 0 && v.getKey() > curr) {
                k++;
                curr = v.getKey();
            }
            b[k + 1] += v.getValue();
        }
        final int[] p = new int[2 * n + 1];
        for (int j = 1; j < 2 * n + 1; j++) {
            p[j] = p[j - 1] + b[j - 1];
        }
        int hi = 0;
        for (int j = 1; j < 2 * n + 1; j++) {
            hi = Math.max(hi, p[j]);
        }
        this.out.println(hi);
    }

    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = isSample() ? sc.nextInt() : 1;
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCaseAlt(i, sc);
            }
        }
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        final boolean sample = isSample();
        final InputStream is;
        final PrintStream out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long timerStart = 0;
        if (sample) {
            is = RestaurantCustomers.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }

        new RestaurantCustomers(sample, is, out).solve();

        out.flush();
        if (sample) {
            final long timeSpent = (System.nanoTime() - timerStart) / 1_000;
            final double time;
            final String unit;
            if (timeSpent < 1_000) {
                time = timeSpent;
                unit = "µs";
            } else if (timeSpent < 1_000_000) {
                time = timeSpent / 1_000.0;
                unit = "ms";
            } else {
                time = timeSpent / 1_000_000.0;
                unit = "s";
            }
            final Path path =
                    Paths.get(RestaurantCustomers.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format("Expected %s, got %s", expected, actual));
            }
            actual.forEach(System.out::println);
            System.out.println(String.format("took: %.3f %s", time, unit));
        }
    }

    private static boolean isSample() {
        try {
            return "sample".equals(System.getProperty("cses"));
        } catch (final SecurityException e) {
            return false;
        }
    }

    private static final class FastScanner implements Closeable {
        private final BufferedReader br;
        private StringTokenizer st;

        public FastScanner(final InputStream in) {
            this.br = new BufferedReader(new InputStreamReader(in));
            st = new StringTokenizer("");
        }

        public String next() {
            while (!st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        @Override
        public void close() {
            try {
                this.br.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }
}
