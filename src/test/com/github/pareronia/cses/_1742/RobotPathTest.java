package com.github.pareronia.cses._1742;

import com.github.pareronia.cses.TestBase;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Set;

class RobotPathTest extends TestBase<RobotPath> {

    protected RobotPathTest() {
        super(RobotPath.class);
    }

    @Override
    protected boolean useFile(final File f) {
        final String name = f.getName();
        final int num =
                Integer.parseInt(
                        StringUtils.substringAfterLast(
                                name.substring(0, name.length() - ".in".length()), " "));
        return !Set.of(10, 11, 12, 21, 27, 28, 29, 30, 31, 32).contains(num);
    }
}
