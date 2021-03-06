package hudson.plugins.analysis.core;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.jvnet.localizer.Localizable;

import hudson.model.Result;

import hudson.plugins.analysis.Messages;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Tests the class {@link BuildResultEvaluator}.
 *
 * @author Ulli Hafner
 */
public class BuildResultEvaluatorTest {
    /** Error message. */
    private static final String WRONG_BUILD_RESULT = "Wrong build result";
    /** Error message. */
    private static final String WRONG_BUILD_FAILURE_STATE = "Wrong build failure state.";

    /**
     * Checks whether valid thresholds are correctly converted.
     */
    @Test
    public void checkThresholds() {
        BuildResultEvaluator parser = new BuildResultEvaluator();

        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, ""));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, "0"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, "1"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, "-1"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, "A"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(0, null));

        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, ""));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, "1"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, "2"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, "-1"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, null));
        assertTrue(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(1, "0"));

        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, ""));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, "2"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, "3"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, "-1"));
        assertFalse(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, null));
        assertTrue(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, "0"));
        assertTrue(WRONG_BUILD_FAILURE_STATE, parser.isAnnotationCountExceeded(2, "1"));
    }

    /**
     * Checks whether valid thresholds are correctly converted.
     */
    @Test
    public void checkResultComputation() {
        BuildResultEvaluator parser = new BuildResultEvaluator();
        List<FileAnnotation> allAnnotations = new ArrayList<FileAnnotation>();
        List<FileAnnotation> newAnnotations = new ArrayList<FileAnnotation>();

        StringBuilder logger = new StringBuilder();
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("0", "0", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "0", "0"), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("0", "0", "0", "0"), allAnnotations, newAnnotations));
        allAnnotations.add(createAnnotation());
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.UNSTABLE,
                parser.evaluateBuildResult(logger, newDescriptor("0", "", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("", "0", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("0", "0", "", ""), allAnnotations, newAnnotations));
        newAnnotations.add(createAnnotation());
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.UNSTABLE,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "0", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "", "0"), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "0", "0"), allAnnotations, newAnnotations));

        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("", "", "", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.UNSTABLE,
                parser.evaluateBuildResult(logger, newDescriptor("0", "", "0", ""), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("0", "", "", "0"), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("", "0", "", "0"), allAnnotations, newAnnotations));
        assertEquals(WRONG_BUILD_RESULT, Result.FAILURE,
                parser.evaluateBuildResult(logger, newDescriptor("", "0", "0", ""), allAnnotations, newAnnotations));
    }

    /**
     * Verifies that the messages contain the expected result.
     */
    @Test
    public void checkMessages() {
        Locale.setDefault(Locale.ENGLISH);

        BuildResultEvaluator parser = new BuildResultEvaluator();
        List<FileAnnotation> allAnnotations = new ArrayList<FileAnnotation>();
        List<FileAnnotation> newAnnotations = new ArrayList<FileAnnotation>();

        StringBuilder logger = new StringBuilder();
        assertEquals(WRONG_BUILD_RESULT, Result.SUCCESS,
                parser.evaluateBuildResult(logger, newDescriptor("0", "0", "0", "0"), allAnnotations, newAnnotations));
        assertEquals("Wrong message", Messages._BuildResultEvaluator_success().toString(Locale.ENGLISH),
                logger.toString());

        allAnnotations.add(createAnnotation());
        allAnnotations.add(createAnnotation());
        allAnnotations.add(createAnnotation());
        allAnnotations.add(createAnnotation());

        checkMessage(parser, allAnnotations, newAnnotations,
                newDescriptor("0", "", "", ""), Messages._BuildResultEvaluator_failure_all(4, 0, 4));
        checkMessage(parser, allAnnotations, newAnnotations,
                newDescriptor("2", "", "", ""), Messages._BuildResultEvaluator_failure_all(4, 2, 2));
        checkMessage(parser, newAnnotations, allAnnotations,
                newDescriptor("", "", "0", ""), Messages._BuildResultEvaluator_failure_new(4, 0, 4));
        checkMessage(parser, newAnnotations, allAnnotations,
                newDescriptor("", "", "2", ""), Messages._BuildResultEvaluator_failure_new(4, 2, 2));
    }

    private void checkMessage(final BuildResultEvaluator parser, final List<FileAnnotation> allAnnotations,
            final List<FileAnnotation> newAnnotations, final Thresholds thresholds, final Localizable expected) {
        StringBuilder logger = new StringBuilder();
        assertEquals(WRONG_BUILD_RESULT, Result.UNSTABLE,
                parser.evaluateBuildResult(logger, thresholds, allAnnotations, newAnnotations));
        assertEquals("wrong message", expected.toString(Locale.ENGLISH),
                logger.toString());
    }

    /**
     * Creates a thresholds object.
     *
     * @param unstableThreshold
     *            Annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param failureThreshold
     *            Annotation threshold to be reached if a build should be
     *            considered as failure.
     * @param newUnstableThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as unstable.
     * @param newFailureThreshold
     *            New annotations threshold to be reached if a build should be
     *            considered as failure.
     * @return the health descriptor
     */
    private Thresholds newDescriptor(final String unstableThreshold, final String failureThreshold,
            final String newUnstableThreshold, final String newFailureThreshold) {
        Thresholds thresholds = new Thresholds();
        thresholds.unstableTotalAll = unstableThreshold;
        thresholds.failedTotalAll = failureThreshold;
        thresholds.unstableNewAll = newUnstableThreshold;
        thresholds.failedNewAll = newFailureThreshold;
        return thresholds;
    }

    /**
     * Returns an annotation with {@link Priority#HIGH}.
     *
     * @return an annotation with {@link Priority#HIGH}
     */
    private FileAnnotation createAnnotation() {
        FileAnnotation annotation = mock(FileAnnotation.class);
        when(annotation.getPriority()).thenReturn(Priority.HIGH);
        when(annotation.getFileName()).thenReturn(StringUtils.EMPTY);

        return annotation;
    }
}

