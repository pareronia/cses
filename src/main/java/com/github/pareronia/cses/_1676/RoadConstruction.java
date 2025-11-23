package com.github.pareronia.cses._1676;

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
import java.util.function.Supplier;

/**
 * Road Construction
 * @see <a href="https://cses.fi/problemset/task/1676">https://cses.fi/problemset/task/1676</a>
 */
public class RoadConstruction {

    private final boolean sample;
    private final InputStream in;
    private final PrintStream out;
    
    public RoadConstruction(
            final Boolean sample, final InputStream in, final PrintStream out) {
        this.sample = sample;
        this.in = in;
        this.out = out;
    }
    
    @SuppressWarnings("unused")
    private void log(final Supplier<Object> supplier) {
        if (!sample) {
            return;
        }
        System.out.println(supplier.get());
    }
    
    public void solve() {
        try (final FastScanner sc = new FastScanner(this.in)) {
        	final int n = sc.nextInt();
        	final UnionFind dsu = new UnionFind(n);
        	final int m = sc.nextInt();
            for (int i = 0; i < m; i++) {
            	final int a = sc.nextInt() - 1;
            	final int b = sc.nextInt() - 1;
            	dsu.unify(a, b);
            	this.out.print(dsu.numComponents);
            	this.out.print(" ");
				this.out.println(dsu.largest);
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
            is = RoadConstruction.class.getResourceAsStream("sample.in");
            out = new PrintStream(baos, true);
            timerStart = System.nanoTime();
        } else {
            is = System.in;
            out = System.out;
        }
        
        new RoadConstruction(sample, is, out).solve();
        
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
                    = Paths.get(RoadConstruction.class.getResource("sample.out").toURI());
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
        
        @SuppressWarnings("unused")
        public int[] nextIntArray(final int n) {
            final int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = nextInt();
            }
            return a;
        }
        
        @SuppressWarnings("unused")
        public long nextLong() {
            return Long.parseLong(next());
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

	public class UnionFind {

		private final int[] sz;

		private final int[] id;

		private int numComponents;

		private int largest;

		public UnionFind(final int size) {

			if (size <= 0) {
				throw new IllegalArgumentException("Size <= 0 is not allowed");
			}

			this.numComponents = size;
			sz = new int[size];
			id = new int[size];

			for (int i = 0; i < size; i++) {
				id[i] = i;
				sz[i] = 1;
			}
			this.largest = 1;
		}

		public int find(final int p) {
			if (id[p] == p) {
				return p;
			}
			id[p] = find(id[p]);
			return id[p];
		}

		public void unify(final int p, final int q) {
			final int root1 = find(p);
			final int root2 = find(q);
			
			if (root1 == root2) {
				return;
			}

			if (sz[root1] < sz[root2]) {
				sz[root2] += sz[root1];
				id[root1] = root2;
				sz[root1] = 0;
				if (sz[root2] > largest) {
					largest = sz[root2];
				}
			} else {
				sz[root1] += sz[root2];
				id[root2] = root1;
				sz[root2] = 0;
				if (sz[root1] > largest) {
					largest = sz[root1];
				}
			}

			numComponents--;
		}
	}
}
