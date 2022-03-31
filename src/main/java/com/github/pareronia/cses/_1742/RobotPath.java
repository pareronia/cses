package com.github.pareronia.cses._1742;

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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Robot Path
 * @see <a href="https://cses.fi/problemset/task/1742">https://cses.fi/problemset/task/1742</a>
 */
public class RobotPath {

    private final InputStream in;
    private final PrintStream out;
    
    public RobotPath(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.in = in;
        this.out = out;
    }
    
    private void handleTestCase(final Integer i, final FastScanner sc) {
        final int n = sc.nextInt();
        final Set<Position> seen = new HashSet<>();
        Position curr = Position.of(0, 0);
        seen.add(curr);
        int ans = 0;
        String prevs = "";
        boolean is180;
        outer:
        for (int j = 0; j < n; j++) {
           final String s = sc.next();
           int d = sc.nextInt();
           int dx;
           int dy;
           if ("U".equals(s)) {
               dx = 0;
               dy = 1;
               is180 = "D".equals(prevs);
           } else if ("D".equals(s)) {
               dx = 0;
               dy = -1;
               is180 = "U".equals(prevs);
           } else if ("L".equals(s)) {
               dx = -1;
               dy = 0;
               is180 = "R".equals(prevs);
           } else {
               dx = 1;
               dy = 0;
               is180 = "L".equals(prevs);
           }
           prevs = s;
           while (d > 0) {
               curr = Position.of(curr.x + dx, curr.y + dy);
               if (!is180) {
                   ans++;
               }
               if (seen.contains(curr)) {
                   break outer;
               }
               seen.add(curr);
               d--;
           }
        }
        this.out.println(ans);
    }
    
    private static class Position {
        private final int x;
        private final int y;
        
        private Position(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
        
        public static Position of(final int x, final int y) {
            return new Position(x, y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Position other = (Position) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Position[x=").append(x).append(", y=").append(y).append("]");
            return builder.toString();
        }
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
            is = RobotPath.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new RobotPath(sample, is, out).solve();
        
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
                    = Paths.get(RobotPath.class.getResource("sample.out").toURI());
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
}
