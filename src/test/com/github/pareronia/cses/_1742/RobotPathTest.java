package com.github.pareronia.cses._1742;

import java.io.File;

import com.github.pareronia.cses.TestBase;

class RobotPathTest extends TestBase<RobotPath> {

    protected RobotPathTest() {
        super(RobotPath.class);
    }

    @Override
    protected boolean useFile(final File f) {
        return !f.getName().startsWith("test12");
    }
}
