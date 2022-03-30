package com.github.pareronia.cses._1132;

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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Tree Distances I
 * @see <a href="https://cses.fi/problemset/task/1132">https://cses.fi/problemset/task/1132</a>
 */
public class TreeDistancesI {

    private final InputStream in;
    private final PrintStream out;
    
    public TreeDistancesI(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        @SuppressWarnings("unchecked")
        final List<Integer>[] adj = new ArrayList[n + 1];
        for (int j = 0; j <= n; j++) {
            adj[j] = new ArrayList<>();
        }
        for (int j = 0; j < n - 1; j++) {
            final int v1 = sc.nextInt();
            final int v2 = sc.nextInt();
            adj[v1].add(v2);
            adj[v2].add(v1);
        }
        final int a = Distances.get(adj, 1, 0, 0, new int[n + 1]);
        final int[] dista = new int[n + 1];
        final int b = Distances.get(adj, a, 0, 0, dista);
        final int[] distb = new int[n + 1];
        Distances.get(adj, b, 0, 0, distb);
        for (int j = 1; j <= n; j++) {
            if (j > 1) {
                this.out.print(" ");
            }
            final int ans = Math.max(dista[j], distb[j]);
            this.out.print(ans);
        }
        this.out.println();
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
            final int numberOfTestCases = isSample() ? sc.nextInt() : 1;
            for (int i = 0; i < numberOfTestCases; i++) {
                handleTestCase(i, sc);
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
            is = TreeDistancesI.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new TreeDistancesI(sample, is, out).solve();
        
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
            final Path path
                    = Paths.get(TreeDistancesI.class.getResource("sample.out").toURI());
            final List<String> expected = Files.readAllLines(path);
            final List<String> actual = asList(baos.toString().split("\\r?\\n"));
            if (!expected.equals(actual)) {
                throw new AssertionError(String.format(
                        "Expected %s, got %s", expected, actual));
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
    
    private static class Distances {
        
        public static int get(
                final List<Integer>[] adj,
                final int start,
                final int parent,
                final int distance,
                final int[] dist
        ) {
            dist[start] = distance;
            int max = -1;
            for (final int n : adj[start]) {
                if (n == parent) {
                    continue;
                }
                final int d = get(adj, n, start, distance + 1, dist);
                if (max == -1 || dist[d] > dist[max]) {
                    max = d;
                }
            }
            return max == -1 ? start : max;
        }
    }
}
