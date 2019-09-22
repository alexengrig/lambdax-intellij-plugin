package io.github.alexengrig.lambdax.intellij.plugin.inspection.collection;

import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.*;
import org.junit.After;
import org.junit.Before;

public class MapGetInspectionTest extends UsefulTestCase {
    private CodeInsightTestFixture testFixture;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        IdeaTestFixtureFactory ideaTestFixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
        TestFixtureBuilder<IdeaProjectTestFixture> testFixtureBuilder = ideaTestFixtureFactory.createFixtureBuilder(getName());
        JavaTestFixtureFactory javaTestFixtureFactory = JavaTestFixtureFactory.getFixtureFactory();
        testFixture = javaTestFixtureFactory.createCodeInsightFixture(testFixtureBuilder.getFixture());
        JavaModuleFixtureBuilder javaModuleFixtureBuilder = testFixtureBuilder.addModule(JavaModuleFixtureBuilder.class);
        javaModuleFixtureBuilder.addContentRoot(testFixture.getTempDirPath());
        javaModuleFixtureBuilder.addSourceRoot("");
        javaModuleFixtureBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
        testFixture.setUp();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        testFixture.tearDown();
        testFixture = null;
    }
}