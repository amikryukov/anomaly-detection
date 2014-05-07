package ru.spbsu.amik.timeseries.implementations;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class FragmentLengthRectifierTest {
    FragmentLengthRectifier fragmentRectifier;

    @BeforeClass
    public void setUp() {
        fragmentRectifier = new FragmentLengthRectifier();
    }

    @Test
    // values : {0, 1, 2}
    public void simpleRectifyTest() {

        List<Point> survey = new ArrayList<Point>(3);
        for (int i = 0; i < 3; i++) {
            survey.add(new Point(i, i));
        }

        double rectification = fragmentRectifier.rectify(survey);

        assertEquals("", 2d, rectification);
    }

    @Test
    // one value
    public void oneValueRectifyTest() {

        List<Point> survey = new ArrayList<Point>(1);
        double value = 123;
        survey.add(new Point(0, value));

        double rectification = fragmentRectifier.rectify(survey);

        assertEquals("should return the same value", value, rectification);
    }

    @Test
    // no values in fragment
    public void noValuesRectifyTest() {
        List<Point> survey = new ArrayList<Point>(0);
        double value = 0d;

        assertEquals("should return 0", value, fragmentRectifier.rectify(survey));
    }

}
