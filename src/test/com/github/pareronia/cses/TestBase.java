package com.github.pareronia.cses;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

public abstract class TestBase<T> {
	
	protected final Class<T> klass;

    protected TestBase(final Class<T> klass) {
		this.klass = klass;
	}

    @TestFactory
    public Stream<DynamicTest> tests() throws URISyntaxException, FileNotFoundException, IOException {
        return DynamicTest.stream(
                    allInputFiles(), displayNameGenerator(), testExecutor());
    }
    
    private Iterator<File> allInputFiles() throws URISyntaxException, FileNotFoundException, IOException {
        final URL resource = this.klass.getResource(".");
        if (resource == null) {
            return Stream.<File> empty().iterator();
        }
        final File folder = Paths.get(resource.toURI()).toFile();
        final Stream<File> files = Stream.of(folder.listFiles())
                .filter(f -> f.getName().endsWith("input.txt"))
                .filter(this::useFile);
        final List<File> fromZips = new ArrayList<>();
        for (final File z : folder.listFiles()) {
        	if (z.getName().endsWith(".zip")) {
        		zippedFiles(z).stream().filter(this::useFile).forEach(fromZips::add);
        	}
        }
		return Stream.concat(files, fromZips.stream()).iterator();
    }

    protected List<File> zippedFiles(final File zipFile) throws FileNotFoundException, IOException {
    	final List<File> files = new ArrayList<>();
    	try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
    		final Path tmp = Files.createTempDirectory(this.klass.getSimpleName());
    		ZipEntry ze;
    		while ((ze = zis.getNextEntry()) != null) {
    			if (ze.isDirectory() || !(ze.getName().endsWith(".in") || ze.getName().endsWith(".out"))) {
    				continue;
    			}
    			final String name = String.format("%s %s", zipFile.getName(), ze.getName());
    			final File file = tmp.resolve(name).toFile();
				file.createNewFile();
				zis.transferTo(new FileOutputStream(file));
				if (file.getName().endsWith(".in")) {
					files.add(file);
				}
    		}
    	}
    	return files;
    }
    
    protected boolean useFile(final File f) {
        return true;
    }

    private Function<File, String> displayNameGenerator() {
        return input -> StringUtils.substringBeforeLast(input.getName(), "_");
    }
    
    private ThrowingConsumer<File> testExecutor() {
        return input -> {
            final List<String> result = run(new FileInputStream(input));
            final Path outPath = outputForInput(input.toPath());
            final List<String> expected = Files.readAllLines(outPath).stream()
                    .map(String::stripTrailing)
                    .collect(toList());
            assertThat(result, is(expected));
        };
    }
    
    private Path outputForInput(final Path input) {
        final String fileName = input.getFileName().toString();
		final String outputFileName;
		if (fileName.endsWith("input.txt")) {
			outputFileName = StringUtils.substringBefore(fileName, "input.txt") + "output.txt";
		} else {
			outputFileName = StringUtils.substringBefore(fileName, ".in") + ".out";
		}
        return input.getParent().resolve(outputFileName);
    }
    
	protected List<String> run(final InputStream in)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
    	
    	final Constructor<T> constructor
    			= this.klass.getDeclaredConstructor(
    			        Boolean.class, InputStream.class, PrintStream.class);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	final PrintStream out = new PrintStream(baos, true);
        final T solution = constructor.newInstance(FALSE, in, out);
    	final Method solve = this.klass.getDeclaredMethod("solve");
    	solve.invoke(solution);
        return asList(baos.toString().split("\\r?\\n"));
    }
    
    protected List<String> runWithTempFile(final ThrowingConsumer<BufferedWriter> writer) throws Throwable {
        final Path temp = createTempFile();
        try (final BufferedWriter theWriter = Files.newBufferedWriter(temp)) {
           writer.accept(theWriter);
        }
        return run(Files.newInputStream(temp));
    }

    protected Path createTempFile() throws IOException {
        final FileAttribute<?>[] attrs = new FileAttribute[] {};
        final Path temp = Files.createTempFile(null, null, attrs);
        temp.toFile().deleteOnExit();
        return temp;
    }
}
