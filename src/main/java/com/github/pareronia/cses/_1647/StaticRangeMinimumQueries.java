package com.github.pareronia.cses._1647;

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
import java.util.List;
import java.util.StringTokenizer;

/**
 * Static Range Minimum Queries
 * @see <a href="https://cses.fi/problemset/task/1647">https://cses.fi/problemset/task/1647</a>
 */
public class StaticRangeMinimumQueries {

    private final InputStream in;
    private final PrintStream out;
    
    public StaticRangeMinimumQueries(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final int q = sc.nextInt();
        final MinSparseTable st = new MinSparseTable(sc.nextIntArray(n));
        final StringBuilder sb = new StringBuilder();
        for (int j = 0; j < q; j++) {
            final int a = sc.nextInt() - 1;
            final int b = sc.nextInt() - 1;
            sb.append(st.queryMin(a, b)).append(System.lineSeparator());
        }
        final String ans = sb.toString();
        this.out.print(ans);
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
            is = StaticRangeMinimumQueries.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new StaticRangeMinimumQueries(sample, is, out).solve();
        
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
                    = Paths.get(StaticRangeMinimumQueries.class.getResource("sample.out").toURI());
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
        
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
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

    private static class MinSparseTable {
        private final int n;
        private final int P;
        private final int[] log2;
        private final int[][] dp;

        public MinSparseTable(final int[] values) {
          n = values.length;
          P = (int) (Math.log(n) / Math.log(2));
          dp = new int[P + 1][n];
          for (int i = 0; i < n; i++) {
            dp[0][i] = values[i];
          }
          log2 = new int[n + 1];
          for (int i = 2; i <= n; i++) {
            log2[i] = log2[i / 2] + 1;
          }
          for (int p = 1; p <= P; p++) {
            for (int i = 0; i + (1 << p) <= n; i++) {
              final int leftInterval = dp[p - 1][i];
              final int rightInterval = dp[p - 1][i + (1 << (p - 1))];
              dp[p][i] = Math.min(leftInterval, rightInterval);
            }
          }
        }

        public int queryMin(final int l, final int r) {
          final int length = r - l + 1;
          final int p = log2[length];
          final int k = 1 << p;
          return Math.min(dp[p][l], dp[p][r - k + 1]);
        }
    }
}
